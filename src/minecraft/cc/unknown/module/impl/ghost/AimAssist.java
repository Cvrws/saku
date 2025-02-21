package cc.unknown.module.impl.ghost;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Te ayuda a apuntar", category = Category.GHOST)
public final class AimAssist extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Single"))
			.add(new SubMode("Switch"))
			.setDefault("Single");

	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 45.0, 5.0, 100.0, 1.0);
	private final NumberValue horizontalCompl = new NumberValue("Horizontal Complement", this, 35.0, 2.0, 97.0, 1.0);
	
	private BooleanValue vertical = new BooleanValue("Vertical", this, false);
	private NumberValue verticalSpeed = new NumberValue("Vertical Aim Speed", this, 10, 1, 15, 1, () -> !vertical.getValue());
	private NumberValue verticalCompl = new NumberValue("Vertical Complement", this, 5, 1, 10, 1, () -> !vertical.getValue());
	private BooleanValue verticalRandom = new BooleanValue("Vertical Random", this, false, () -> !vertical.getValue());
	private NumberValue verticalRandomization = new NumberValue("Vertical Randomization", this, 1.2, 0.1, 5, 0.1, () -> !verticalRandom.getValue());

	private final ModeValue speedMode = new ModeValue("Speed Type", this)
			.add(new SubMode("Thread Local Random"))
			.add(new SubMode("Random Secure"))
			.add(new SubMode("Gaussian"))
			.setDefault("Thread Local Random");
	
	private final NumberValue angle = new NumberValue("Angle", this, 180, 1, 180, 1);
	private final NumberValue distance = new NumberValue("Distance", this, 4, 1, 8, 0.1);
	private final BooleanValue clickAim = new BooleanValue("Require Clicking", this, true);
	private final BooleanValue lockTarget = new BooleanValue("Lock Target", this, false);
	private final BooleanValue ignoreFriend = new BooleanValue("Ignore Friends", this, false);
	private final BooleanValue ignoreInvisibles = new BooleanValue("Ignore invisibles", this, false);
	private final BooleanValue ignoreTeams = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue visibilityCheck = new BooleanValue("Visibility Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !visibilityCheck.getValue());
	private final BooleanValue checkBlockBreak = new BooleanValue("Check Block Break", this, false);
	private final BooleanValue weaponOnly = new BooleanValue("Weapons Only", this, false);
	
	private final Set<EntityPlayer> lockedTargets = new HashSet<>();
	public EntityPlayer target;
	private Random random = new Random();
	
    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (lockTarget.getValue() && event.getTarget() instanceof EntityPlayer) {
            EntityPlayer attackedTarget = (EntityPlayer) event.getTarget();
            lockedTargets.add(attackedTarget);
            if (target == null) {
            	target = attackedTarget;
            }
        }
    };
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (noAim()) {
	        return;
	    }
	    
        if (!lockTarget.getValue() || target == null || !onTarget()) {
            target = getEnemy();
        }
        
	    if (target == null) {
	        return;
	    }

	    double yawSpeed = horizontalSpeed.getValueToDouble();
	    double yawCompl = horizontalCompl.getValueToDouble();
	    double yawOffset = MathUtil.nextSecure(yawSpeed, yawCompl).doubleValue() / 180;
	    double yawFov = PlayerUtil.fovFromTarget(target, event.getYaw());
	    double pitchEntity = PlayerUtil.pitchFromTarget(target, 0, event.getPitch());
	    float yawAdjustment = getSpeedRandomize(speedMode.getValue().getName(), yawFov, yawOffset, yawSpeed, yawCompl);

	    double verticalRandomOffset = MathUtil.nextRandom(verticalCompl.getValueToDouble() - 1.47328, verticalCompl.getValueToDouble() + 2.48293).doubleValue() / 100;
	    float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0D - MathUtil.nextRandom(verticalSpeed.getValueToDouble() - 4.723847, verticalSpeed.getValueToDouble()).doubleValue())));

	    if (onTarget(target)) {
	        applyYaw(yawFov, yawAdjustment);
	        applyPitch(resultVertical);
	    } else {
	        applyYaw(yawFov, yawAdjustment);
	        applyPitch(resultVertical);
	    }
	};

	@Override
	public void onDisable() {
		target = null;
	}

    private EntityPlayer getEnemy() {
        int fov = angle.getValueToInt();
        Vec3 playerPos = new Vec3(mc.player);
        EntityPlayer bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        
        for (EntityPlayer player : mc.world.playerEntities) {
            if (lockedTargets.contains(player) && !isValidTarget(player, playerPos, fov)) {
                continue;
            }
            if (!isValidTarget(player, playerPos, fov)) {
                continue;
            }
            
            double score;
            if (mode.is("Switch")) {
                score = RotationUtil.nearestRotation(player.getEntityBoundingBox());
            } else {
                score = playerPos.distanceTo(player.getPositionVector());
            }
            if (score < bestScore) {
                bestTarget = player;
                bestScore = score;
            }
        }
        
        return bestTarget;
    }
	
	private boolean isValidTarget(EntityPlayer player, Vec3 playerPos, int fov) {
	    if (player == mc.player || !player.isEntityAlive() || player.deathTime > 0) {
	        return false;
	    }
	    
	    if (EnemyUtil.isEnemy(player)) return false;
	    if (PlayerUtil.unusedNames(player)) return false;

	    if (!ignoreInvisibles.getValue() && player.isInvisible()) {
	        return false;
	    }

	    if (FriendUtil.isFriend(player) && ignoreFriend.getValue()) {
	        return false;
	    }

	    if (ignoreTeams.getValue() && PlayerUtil.isTeam(player, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) {
	        return false;
	    }

	    if (mc.player.getDistanceToEntity(player) > distance.getValueToDouble()) {
	        return false;
	    }

	    if (visibilityCheck.getValue() && !mc.player.canEntityBeSeen(player)) {
	        return false;
	    }

	    return fov == 180 || PlayerUtil.fov(fov, player);
	}

	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) {
	        return true;
	    }

	    if (weaponOnly.getValue() && !InventoryUtil.isSword()) {
	        return true;
	    }

	    if (clickAim.getValue() && !PlayerUtil.isClicking()) {
	        return true;
	    }

	    if (mouseOverEntity.getValue() 
	        && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)) {
	        return true;
	    }
	    
	    if (checkBlockBreak.getValue() && mc.playerController.isHittingBlock) {
	    	return true;
	    }

	    return false;
	}
	
    private boolean onTarget() {
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                && mc.objectMouseOver.entityHit == target;
    }
    
    private boolean onTarget(EntityPlayer target) {
    	return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
    			&& mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
    			&& mc.objectMouseOver.entityHit == target;
    }
	
	private void applyYaw(double yawFov, float yawAdjustment) {
	    if (isYawFov(yawFov)) {
	        mc.player.rotationYaw += yawAdjustment;
	    }
	}

	private void applyPitch(float resultVertical) {
	    if (vertical.getValue()) {
	        float pitchChange = random.nextBoolean() ? -MathUtil.nextRandom(0F, verticalRandomization.getValueToFloat()).floatValue() : MathUtil.nextRandom(0F, verticalRandomization.getValueToFloat()).floatValue();
	        float pitchAdjustment = verticalRandom.getValue() ? pitchChange : resultVertical;
	        float newPitch = mc.player.rotationPitch + pitchAdjustment;

	        mc.player.rotationPitch += pitchAdjustment;
	        mc.player.rotationPitch = normalizePitch(newPitch);
	    }
	}

	private float normalizePitch(float pitch) {
	    return pitch >= 90f ? pitch - 360f : pitch <= -90f ? pitch + 360f : pitch;
	}

	private float getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
	    double randomComplement;
	    float result;

	    switch (mode) {
	        case "Thread Local Random":
	            randomComplement = MathUtil.nextRandom(complement - 1.47328, complement + 2.48293).doubleValue() / 100;
	            result = calculateResult(fov, offset, speed, MathUtil.nextRandom(speed - 4.723847, speed).doubleValue());
	            break;

	        case "Random Secure":
	            randomComplement = MathUtil.nextSecure(complement - 1.47328, complement + 2.48293).doubleValue() / 100;
	            result = calculateResult(fov, offset, speed, MathUtil.nextSecure(speed - 4.723847, speed).doubleValue());
	            break;

	        case "Gaussian":
	            randomComplement = (complement + new Random().nextGaussian() * 0.5) / 100;
	            result = calculateResult(fov, offset, speed, speed + new Random().nextGaussian() * 0.3);
	            break;

	        default:
	            throw new IllegalArgumentException("Unknown mode: " + mode);
	    }

	    return (float) (randomComplement + result);
	}

	private float calculateResult(double fov, double offset, double speed, double randomizedSpeed) {
	    return (float) (-(fov * offset + fov / (101.0 - randomizedSpeed)));
	}
	
	private boolean isYawFov(double fov) {
		return fov > 1.0D || fov < -1.0D;
	}
}