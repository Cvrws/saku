package cc.unknown.module.impl.ghost;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Te ayuda a apuntar", category = Category.GHOST)
public final class AimAssist extends Module {

	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 45.0, 5.0, 100.0, 1.0);
	private final NumberValue horizontalCompl = new NumberValue("Horizontal Complement", this, 35.0, 2.0, 97.0, 1.0);
	
	private BooleanValue vertical = new BooleanValue("Vertical", this, false);
	private NumberValue verticalSpeed = new NumberValue("Vertical Aim Speed", this, 10, 1, 15, 1, () -> !vertical.getValue());
	private NumberValue verticalCompl = new NumberValue("Vertical Complement", this, 5, 1, 10, 1, () -> !vertical.getValue());
	private BooleanValue verticalRandom = new BooleanValue("Vertical Random", this, false, () -> !vertical.getValue());
	private NumberValue verticalRandomization = new NumberValue("Vertical Randomization", this, 1.2, 0.1, 5, 0.01, () -> !verticalRandom.getValue());

	private final ModeValue randomMode = new ModeValue("Speed Type", this)
			.add(new SubMode("Thread Local Random"))
			.add(new SubMode("Random Secure"))
			.add(new SubMode("Gaussian"))
			.setDefault("Thread Local Random");
	
	private final NumberValue maxAngle = new NumberValue("Max Angle", this, 180, 1, 180, 1);
	private final NumberValue distance = new NumberValue("Distance", this, 4, 1, 8, 0.1);
	private final BooleanValue clickAim = new BooleanValue("Require Clicking", this, true);
	private final BooleanValue ignoreFriend = new BooleanValue("Ignore Friends", this, false);
	private final BooleanValue ignoreInvisibles = new BooleanValue("Ignore invisibles", this, false);
	private final BooleanValue ignoreTeams = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue visibilityCheck = new BooleanValue("Visibility Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !visibilityCheck.getValue());
	private final BooleanValue checkBlockBreak = new BooleanValue("Check Block Break", this, false);
	private final BooleanValue onlyBed = new BooleanValue("Only Bed", this, false, () -> !checkBlockBreak.getValue());
	private final BooleanValue weaponOnly = new BooleanValue("Weapons Only", this, false);
	public EntityPlayer target;
	private Random random = new Random();
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (noAim()) {
	        return;
	    }

	    target = getEnemy();
	    if (target == null) {
	        return;
	    }

	    double yawSpeed = horizontalSpeed.getValue().doubleValue();
	    double yawCompl = horizontalCompl.getValue().doubleValue();
	    double yawOffset = MathUtil.nextSecureRandom(yawSpeed, yawCompl).doubleValue() / 180;
	    double yawFov = PlayerUtil.fovFromTarget(target);
	    double pitchEntity = PlayerUtil.pitchFromTarget(target, 0);
	    float yawAdjustment = getSpeedRandomize(randomMode.getValue().getName(), yawFov, yawOffset, yawSpeed, yawCompl);

	    double verticalRandomOffset = MathUtil.nextRandom(verticalCompl.getValue().doubleValue() - 1.47328, verticalCompl.getValue().doubleValue() + 2.48293).doubleValue() / 100;
	    float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0D - MathUtil.nextRandom(verticalSpeed.getValue().doubleValue() - 4.723847, verticalSpeed.getValue().doubleValue()).doubleValue())));

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
	    int fov = maxAngle.getValue().intValue();
	    Vec3 playerPos = new Vec3(mc.player);

	    target = null;
	    double targetFov = 180.0;
	    EntityPlayer potentialTarget = null;

	    for (EntityPlayer player : mc.theWorld.playerEntities) {
	        if (!isValidTarget(player, playerPos, fov)) {
	            continue;
	        }

	        double fovToPlayer = Math.abs(PlayerUtil.getFov(player.posX, player.posZ));
	        if (fovToPlayer < targetFov) {
	            potentialTarget = player;
	            targetFov = fovToPlayer;
	        }
	    }

	    target = potentialTarget;
	    return target;
	}
	
	private boolean isValidTarget(EntityPlayer player, Vec3 playerPos, int fov) {
	    if (player == mc.player || !player.isEntityAlive() || player.deathTime > 0) {
	        return false;
	    }

	    if (EnemyUtil.isEnemy(player)) return false;
	    if (player.getName().contains("[NPC]") || player.getName().contains("MEJORAS") || player.getName().contains("CLICK DERECHO")) {
	        return false;
	    }

	    if (!ignoreInvisibles.getValue() && player.isInvisible()) {
	        return false;
	    }

	    if (FriendUtil.isFriend(player) && !ignoreFriend.getValue()) {
	        return false;
	    }

	    if (ignoreTeams.getValue() && PlayerUtil.isTeam(player, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) {
	        return false;
	    }

	    if (playerPos.distanceTo(player) > distance.getValue().doubleValue()) {
	        return false;
	    }

	    if (visibilityCheck.getValue() && !mc.player.canEntityBeSeen(player)) {
	        return false;
	    }

	    return fov == 180 || PlayerUtil.fov(player, fov);
	}

	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) {
	        return true;
	    }

	    if (weaponOnly.getValue() && !PlayerUtil.isHoldingWeapon()) {
	        return true;
	    }

	    if (clickAim.getValue() && !PlayerUtil.isClicking()) {
	        return true;
	    }

	    if (mouseOverEntity.getValue() 
	        && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)) {
	        return true;
	    }
	    
	    if (checkBlockBreak.getValue() && mc.objectMouseOver != null) {
	        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	        if (blockPos != null) {
	            Block block = mc.theWorld.getBlockState(blockPos).getBlock();

	            if (block != Blocks.air) {
	                if (onlyBed.getValue()) {
	                    if (block instanceof BlockBed) {
	                        return true;
	                    }
	                } else {
	                    if (!(block instanceof BlockLiquid) && block instanceof Block) {
	                        return true;
	                    }
	                }
	            }
	        }
	    }

	    return false;
	}

	private boolean onTarget(EntityPlayer target) {
		return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
				&& mc.objectMouseOver.typeOfHit != MovingObjectType.BLOCK
				&& mc.objectMouseOver.entityHit == target;
	}
	
	private void applyYaw(double yawFov, float yawAdjustment) {
	    if (isYawFov(yawFov)) {
	        mc.player.rotationYaw += yawAdjustment;
	        mc.player.setAngles(Math.abs(yawAdjustment / 50), 0.0f);
	    }
	}

	private void applyPitch(float resultVertical) {
	    if (vertical.getValue()) {
	        float pitchChange = random.nextBoolean() ? -MathUtil.nextRandom(0F, verticalRandomization.getValue().floatValue()).floatValue() : MathUtil.nextRandom(0F, verticalRandomization.getValue().floatValue()).floatValue();
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
	            randomComplement = MathUtil.nextSecureRandom(complement - 1.47328, complement + 2.48293).doubleValue() / 100;
	            result = calculateResult(fov, offset, speed, MathUtil.nextSecureRandom(speed - 4.723847, speed).doubleValue());
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
