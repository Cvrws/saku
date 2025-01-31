package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "WTap", description = "Suelta brevemente la W después de atacar para aumentar el knockback dado", category = Category.GHOST)
public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Silent"))
			.add(new SubMode("Legit"))
			.setDefault("Normal");

	private BoundsNumberValue hits = new BoundsNumberValue("Hits", this, 1, 2, 0, 10, 1);
	private NumberValue delay = new NumberValue("Delay", this, 500, 50, 1000, 10);
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);
	private NumberValue sprintingTicks = new NumberValue("Sprinting Ticks", this, 50, 0, 600, 10, () -> !mode.is("Silent"));
	private NumberValue targetHurtime = new NumberValue("Target HurtTime", this, 10, 1, 10, 1);
	private NumberValue ownHurtime = new NumberValue("Own HurtTime", this, 10, 1, 10, 1);
	private BooleanValue rotationThreshold = new BooleanValue("Rotation Threshold", this, false);
	private NumberValue yawDiff = new NumberValue("Yaw Difference", this, 120, 0, 180, 1, () -> !rotationThreshold.getValue());
	private BooleanValue distance = new BooleanValue("Distance", this, false);
	private NumberValue distanceToEntity = new NumberValue("Distance To Entity", this, 4, 3, 10, 1, () -> !distance.getValue());
	
	private BooleanValue onlyGround = new BooleanValue("Only Ground", this, false);
	private BooleanValue onlyMove = new BooleanValue("Only Move", this, false);
	private BooleanValue onlyMoveForward = new BooleanValue("Only Forward", this, false, () -> !onlyMove.getValue());
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false);
	private BooleanValue checkLiquids = new BooleanValue("Check Liquids", this, true);
	private BooleanValue onlyWeapons = new BooleanValue("Only Weapons", this, false);

	private final StopWatch stopWatch = new StopWatch();
	private int ticks;
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		double chanceValue = chance.getValue().doubleValue();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);
		if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
		EntityPlayer player = (EntityPlayer) event.getTarget();
		
		if (event.getTarget() instanceof IMob || event.getTarget() instanceof INpc) return;
		if (mc.player == null || player == null) return;
		
		if (player.hurtTime > targetHurtime.getValue().intValue()) return;
		if (mc.player.hurtTime > ownHurtime.getValue().intValue()) return;
		if (!stopWatch.finished(delay.getValue().intValue())) return;
		if (ignoreTeammates.getValue() && PlayerUtil.isTeam(player, true, true)) return;
		if (checkLiquids.getValue() && shouldJump()) return;
		if (PlayerUtil.unusedNames(player)) return;
		if (onlyWeapons.getValue() && !PlayerUtil.isHoldingWeapon()) return;
		if (onlyMove.getValue() && !MoveUtil.isMoving()) return;
		if (onlyMoveForward.getValue() && mc.player.movementInput.moveStrafe != 0f) return;
		if (onlyGround.getValue() && !mc.player.onGround) return;
		if (rotationThreshold.getValue() && exceedsRotationThreshold(player)) return;
		if (distance.getValue() && mc.player.getDistanceToEntity(player) <= distanceToEntity.getValue().floatValue()) return;
		    
		if (stopWatch.finished(delay.getValue().intValue())) {
			stopWatch.reset();
			ticks = 2;
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        switch (ticks) {
        case 2: {
        	switch (mode.getValue().getName()) {
                case "Normal":
                    mc.gameSettings.keyBindForward.pressed = false;
                    break;
                case "Silent":
                	mc.player.sprintingTicksLeft = 0;
                    break;
                case "Legit":
                    mc.player.setSprinting(false);
                    break;
            }
            ticks--;
        }
            break;
        case 1: {
            switch (mode.getValue().getName()) {
                case "Normal":
                    mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                    break;
                case "Silent":
                	mc.player.sprintingTicksLeft = sprintingTicks.getValue().intValue();
                    break;
                case "Legit":
                    mc.player.setSprinting(true);
                    break;
            }
            ticks--;
        }
            break;
        }
	};

	private boolean exceedsRotationThreshold(EntityPlayer target) {
	    double deltaX = mc.player.posX - target.posX;
	    double deltaZ = mc.player.posZ - target.posZ;
	    float calculatedYaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0 / Math.PI - 90.0);
	    float yawDifference = Math.abs(MathHelper.wrapAngleTo180_float(calculatedYaw - target.rotationYawHead));
	    return yawDifference > yawDiff.getValue().floatValue();
	}
	
	private boolean shouldJump() {
	    return mc.player.isInWater() || mc.player.isInLava();
	}
}