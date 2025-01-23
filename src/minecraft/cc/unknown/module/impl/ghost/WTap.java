package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.Clutch;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "WTap", description = "Suelta brevemente la W después de atacar para aumentar el knockback dado", category = Category.GHOST)
public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Silent"))
			.setDefault("Normal");

	private NumberValue delay = new NumberValue("Delay", this, 500, 50, 1000, 10, () -> !isTwo());
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1, () -> !isTwo());
	private NumberValue hurtTime = new NumberValue("HurtTime", this, 10, 1, 10, 10, () -> !isTwo());
	private BoundsNumberValue hits = new BoundsNumberValue("Hits", this, 1, 2, 0, 10, 1, () -> !isTwo());
	private BooleanValue onlyCombo = new BooleanValue("Rotation Threshold", this, false, () -> !isTwo());
	private BooleanValue onlyGround = new BooleanValue("Only Ground", this, false, () -> !isTwo());
	private BooleanValue onlyMove = new BooleanValue("Only Move", this, false, () -> !isTwo());
	private BooleanValue onlyMoveForward = new BooleanValue("Only Forward", this, false, () -> !onlyMove.getValue() || !isTwo());
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false, () -> !isTwo());

	private final StopWatch stopWatch = new StopWatch();
	private int ticks;
	private EntityPlayer target = null;

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
	    if (mc.player == null || event.getTarget() == null || !(event.getTarget() instanceof EntityPlayer)) return;

	    target = (EntityPlayer) event.getTarget();

	    if (!shouldPerformWithChance(chance.getValue().doubleValue())) return;

	    if (onlyCombo.getValue() && exceedsRotationThreshold(target)) return;

	    if (isInvalidTarget(event) || !stopWatch.finished(delay.getValue().intValue())) return;

	    switch (mode.getValue().getName()) {
	    case "Normal":
	    case "Silent":
	    	ticks = 2;
	    	stopWatch.reset();
	    	break;
	    default:
	    	break;
	    }
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (isAnyModuleEnabled(Scaffold.class, LegitScaffold.class, Clutch.class)) return;

	    if (target != null && MoveUtil.isMoving()) {
	        handleModeActions();
	        target = null;
	    }
	};
	
	private boolean isTwo() {
		return mode.is("Normal") || mode.is("Silent");
	}
	
	private boolean shouldPerformWithChance(double chanceValue) {
	    double randomFactor = MathUtil.getRandomFactor(chanceValue);
	    return MathUtil.shouldPerformAction(chanceValue, randomFactor);
	}
	
	private boolean exceedsRotationThreshold(EntityPlayer target) {
	    double deltaX = mc.player.posX - target.posX;
	    double deltaZ = mc.player.posZ - target.posZ;
	    float calculatedYaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0 / Math.PI - 90.0);
	    float yawDifference = Math.abs(MathHelper.wrapAngleTo180_float(calculatedYaw - target.rotationYawHead));
	    return yawDifference > 120.0f;
	}
	
	private boolean isInvalidTarget(AttackEvent event) {
	    EntityPlayer target = (EntityPlayer) event.getTarget();
	    return event.getTarget().hurtTime > hurtTime.getValue().intValue()
	            || (onlyGround.getValue() && !mc.player.onGround)
	            || (ignoreTeammates.getValue() && PlayerUtil.isTeam(target, true, true))
	            || PlayerUtil.unusedNames(target)
	            || (onlyMove.getValue() && (!MoveUtil.isMoving() || (onlyMoveForward.getValue() && mc.player.movementInput.moveStrafe != 0f)));
	}
	
	private void handleModeActions() {
	    switch (mode.getValue().getName()) {
	        case "Normal":
	            handleNormalMode();
	            break;
	        case "Silent":
	            handleSilentMode();
	            break;
	        default:
	            break;
	    }
	}

	private void handleNormalMode() {
	    switch (ticks) {
	        case 2:
	            mc.gameSettings.keyBindForward.pressed = false;
	            ticks--;
	            break;
	        case 1:
	            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
	            ticks--;
	            break;
	    }
	}

	private void handleSilentMode() {
	    mc.player.sprintingTicksLeft = 0;
	    ticks--;
	}
}