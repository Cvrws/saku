package cc.unknown.module.impl.ghost;

import java.util.Random;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
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
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Advanced"))
			.setDefault("Advanced");
	
	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 45.0, 5.0, 100.0, 1.0);
	private final NumberValue horizontalCompl = new NumberValue("Horizontal Complement", this, 35.0, 2.0, 97.0, 1.0);

	private final ModeValue randomMode = new ModeValue("Speed Randomization", this)
			.add(new SubMode("Thread Local Random"))
			.add(new SubMode("Random Secure"))
			.add(new SubMode("Gaussian"))
			.setDefault("Thread Local Random");
	
	private final NumberValue maxAngle = new NumberValue("Max Angle", this, 180, 1, 180, 1);
	private final NumberValue distance = new NumberValue("Distance", this, 4, 1, 8, 0.1);
	private final BooleanValue clickAim = new BooleanValue("Aim on Click", this, true);
	private final BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friends", this, false);
	private final BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Targets", this, false);
	private final BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !lineOfSightCheck.getValue());
	private final BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable While Breaking Blocks", this, false);
	private final BooleanValue weaponOnly = new BooleanValue("Only Aim While Holding at Weapon", this, false);
	public EntityPlayer target;
	private StopWatch stopWatch = new StopWatch();
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (noAim()) {
			return;
		}

		target = getEnemy();
		if (target == null) return;

        double yawSpeed = this.horizontalSpeed.getValue().doubleValue();
        double yawCompl = this.horizontalCompl.getValue().doubleValue();
        double yawOffset = MathUtil.nextSecureDouble(yawSpeed, yawCompl) / 180;
        
        double yawFov = PlayerUtil.fovFromTarget(target);
        float yaw = getSpeedRandomize(randomMode.getValue().getName(), yawFov, yawOffset, yawSpeed, yawCompl);
        
        if (onTarget(target)) {
            if (isYawFov(yawFov)) {
                mc.player.rotationYaw += yaw;
                mc.player.setAngles(Math.abs(yaw / 50), 0.0f);
            }
        } else {
        	if (isYawFov(yawFov)) {
            	mc.player.rotationYaw += yaw;
            	mc.player.setAngles(Math.abs(yaw / 50), 0.0f);
        	}
        }
	};

	@Override
	public void onDisable() {
		target = null;
	}

	public EntityPlayer getEnemy() {
	    int fov = maxAngle.getValue().intValue();
	    final Vec3 playerPos = new Vec3(mc.player);
	
	    target = null;
	    double targetFov = 180;
	    EntityPlayer potentialTarget = null;
	    int validTargets = 0;
	
	    for (final EntityPlayer player : mc.world.playerEntities) {
	        if (player != mc.player && player.deathTime == 0 && player.isEntityAlive()) {
	            if (getInstance().getEnemyManager().isEnemy(player)) continue;
	            if (player.getName().contains("[NPC]")) continue;
	            if (player.getName().contains("MEJORAS")) continue;
	            if (player.getName().contains("CLICK DERECHO")) continue;
	            if (getInstance().getFriendManager().isFriend(player) && ignoreFriendlyEntities.getValue()) continue;
	            if (ignoreTeammates.getValue() && PlayerUtil.isTeam(player, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) continue;
	            if (playerPos.distanceTo(player) > distance.getValue().doubleValue()) continue;
	            if (lineOfSightCheck.getValue() && !mc.player.canEntityBeSeen(player)) continue;
	            if (fov != 180 && !PlayerUtil.fov(player, fov)) continue;
	            double fov2 = Math.abs(PlayerUtil.getFov(player.posX, player.posZ));
	            if (fov2 < targetFov) {
	                potentialTarget = player;
	                targetFov = fov2;
	            }
	            validTargets++;
	        }
	    }
	
	    if (validTargets == 1) {
	        //maxAngle.setValue(180);
	    }
	
	    target = potentialTarget;
	    return target;
	}

	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
	    if (weaponOnly.getValue() && !PlayerUtil.isHoldingWeapon()) return true;
	    if (clickAim.getValue() && !PlayerUtil.isClicking()) return true;
	    if (mouseOverEntity.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)) return true;
	    if (disableAimWhileBreakingBlock.getValue() && mc.objectMouseOver != null) {
	        BlockPos p = mc.objectMouseOver.getBlockPos();
	        if (p != null) {
	            Block bl = mc.world.getBlockState(p).getBlock();
	            if (bl != Blocks.air && !(bl instanceof BlockLiquid) && (bl instanceof Block || bl instanceof BlockBed)) {
	                return true;
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

	public float getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
	    double randomComplement;
	    float result;

	    switch (mode) {
	        case "Thread Local Random":
	            randomComplement = MathUtil.nextDouble(complement - 1.47328, complement + 2.48293) / 100;
	            result = (float) (-(fov * offset + fov / (101.0 - MathUtil.nextDouble(speed - 4.723847, speed))));
	            return (float) (randomComplement + result);

	        case "Random Secure":
	            randomComplement = MathUtil.nextSecureDouble(complement - 1.47328, complement + 2.48293) / 100;
	            result = (float) (-(fov * offset + fov / (101.0 - MathUtil.nextSecureDouble(speed - 4.723847, speed))));
	            return (float) (randomComplement + result);

	        case "Gaussian":
	            randomComplement = complement + new Random().nextGaussian() * 0.5;
	            randomComplement /= 100;
	            result = (float) (-(fov * offset + fov / (101.0 - (speed + new Random().nextGaussian() * 0.3))));
	            return (float) (randomComplement + result);

	        default:
	            throw new IllegalArgumentException("Unknown mode: " + mode);
	    }
	}

	private boolean isYawFov(double fov) {
		return fov > 1.0D || fov < -1.0D;
	}
}
