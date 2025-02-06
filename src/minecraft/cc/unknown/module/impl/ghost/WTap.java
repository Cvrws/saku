package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "WTap", description = "Suelta brevemente la W después de atacar para aumentar el knockback dado", category = Category.GHOST)
public class WTap extends Module {

	private final ModeValue mode = new ModeValue("Mode", this) {
		{
			add(new SubMode("Normal"));
			add(new SubMode("Silent"));
			add(new SubMode("Legit"));
			add(new SubMode("No Stop"));
			add(new SubMode("S-Tap"));
			setDefault("Normal");
		}
	};
	
    private final NumberValue range = new NumberValue("Range", this, 3, 0, 6, 0.1, () -> !mode.is("S-Tap") && !mode.is("No Stop"));
    private final NumberValue combo = new NumberValue("Combo To Start", this, 2, 0, 6, 1, () -> !mode.is("S-Tap") && !mode.is("No Stop"));

	private BoundsNumberValue hits = new BoundsNumberValue("Hits", this, 1, 2, 0, 10, 1, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue delay = new NumberValue("Delay", this, 500, 50, 1000, 10, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue sprintingTicks = new NumberValue("Sprinting Ticks", this, 50, 0, 600, 10, () -> !mode.is("Silent") || mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue targetHurtime = new NumberValue("Target HurtTime", this, 10, 1, 10, 1, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue ownHurtime = new NumberValue("Own HurtTime", this, 10, 1, 10, 1, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue rotationThreshold = new BooleanValue("Rotation Threshold", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue yawDiff = new NumberValue("Yaw Difference", this, 120, 0, 180, 1, () -> !rotationThreshold.getValue() || mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue distance = new BooleanValue("Distance", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private NumberValue distanceToEntity = new NumberValue("Distance To Entity", this, 4, 3, 10, 1, () -> !distance.getValue() || mode.is("S-Tap") || mode.is("No Stop"));
	
	private BooleanValue onlyGround = new BooleanValue("Only Ground", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue onlyMove = new BooleanValue("Only Move", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue onlyMoveForward = new BooleanValue("Only Forward", this, false, () -> !onlyMove.getValue() || mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue checkLiquids = new BooleanValue("Check Liquids", this, true, () -> mode.is("S-Tap") || mode.is("No Stop"));
	private BooleanValue onlyWeapons = new BooleanValue("Only Weapons", this, false, () -> mode.is("S-Tap") || mode.is("No Stop"));

	private final StopWatch stopWatch = new StopWatch();
	private int ticks;
	private int row;
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		double chanceValue = chance.getValue().doubleValue();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);
		if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
		if (event.getTarget() instanceof IMob || event.getTarget() instanceof INpc) return;
		EntityPlayer player = (EntityPlayer) event.getTarget();
		
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
	
    @EventLink
    public final Listener<MoveInputEvent> onMovementInput = event -> {
        EntityLivingBase target = (EntityLivingBase) TargetUtil.getTarget(10);
        double range = this.range.getValue().doubleValue();

        if (mc.player == null) return;
        if (!mc.player.onGround) return;
        
        if (mc.player.ticksSinceAttack <= 7) range -= 0.2;

        if (target == null) {
            row = 0;
            return;
        }

        if (target.hurtTime > 0) row += 1;
        if (mc.player.hurtTime > 0) row = 0;

        if (row <= combo.getValue().intValue() * 8 && combo.getValue().intValue() > 0) {
            return;
        }

        if (PlayerUtil.calculatePerfectRangeToEntity(target) < range - 0.05) {
            final float forward = event.getForward();
            final float strafe = event.getStrafe();

            final double angle = MathHelper.wrapAngleTo180_double(RotationUtil.calculate(target).getX() - 180);

            if (forward == 0 && strafe == 0) {
                return;
            }

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) continue;

                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.player.rotationYaw, predictedForward, predictedStrafe)));
                    final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            switch (mode.getValue().getName()) {
                case "No Stop":
                    if (closestForward == forward * -1) event.setForward(0);
                    if (closestStrafe == strafe * -1) event.setStrafe(0);
                    break;

                case "S-Tap":
                    event.setForward(closestForward);
                    event.setStrafe(closestStrafe);
                    break;
            }
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