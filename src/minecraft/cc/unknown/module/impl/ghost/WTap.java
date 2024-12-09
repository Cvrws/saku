package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreLivingUpdateEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(aliases = "WTap", description = "Suelta brevemente la W después de atacar para aumentar el knockback dado", category = Category.GHOST)
public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Fast"))
			.add(new SubMode("Advanced"))
			.setDefault("Normal");
	
	private NumberValue delay = new NumberValue("Delay", this, 500, 0, 2000, 5);
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);
	private NumberValue hurtTime = new NumberValue("HurtTime", this, 10, 1, 10, 10, () -> !mode.is("Advanced"));
	private BoundsNumberValue ticksUntilBlock = new BoundsNumberValue("Ticks Until Block", this, 0, 2, 0, 5, 1, () -> !mode.is("Advanced"));
	private BoundsNumberValue reSprintTicks = new BoundsNumberValue("ReSprint Ticks", this, 0, 2, 0, 5, 1, () -> !mode.is("Advanced"));
	private NumberValue minEnemyRotDiffToIgnore = new NumberValue("Rotation Diff From Enemy To Ignore", this, 180, 0, 180, 1, () -> !mode.is("Advanced"));
	private BoundsNumberValue hits = new BoundsNumberValue("Hits", this, 1, 2, 0, 10, 1, () -> !isTwo());
	private BooleanValue debug = new BooleanValue("Debug", this, false, () -> !isTwo());
	private BooleanValue onlyGround = new BooleanValue("Only Ground", this, false);
	private BooleanValue onlyMove = new BooleanValue("only Move", this, false);
	private BooleanValue onlyMoveForward = new BooleanValue("Only Forward", this, false, () -> !onlyMove.getValue());

    private final StopWatch stopWatch = new StopWatch();
    private int ticks;
    
    private int blockInputTicks = MathUtil.nextSecureInt(ticksUntilBlock.getValue().intValue(), ticksUntilBlock.getSecondValue().intValue());
    private int blockTicksElapsed = 0;
    private boolean startWaiting = false;
    private boolean blockInput = false;
    private int allowInputTicks = MathUtil.nextSecureInt(reSprintTicks.getValue().intValue(), reSprintTicks.getSecondValue().intValue());
    private int ticksElapsed = 0;
    
    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = MathUtil.getRandomFactor(chanceValue);
	    if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
	    if (mc.player == null) return;

	    if (event.getTarget().hurtTime > hurtTime.getValue().intValue() || !stopWatch.finished(delay.getValue().intValue()) || (onlyGround.getValue() && !mc.player.onGround)) {
	        return;
	    }

	    if (onlyMove.getValue() && (!MoveUtil.isMoving() || (onlyMoveForward.getValue() && mc.player.movementInput.moveStrafe != 0f))) {
	        return;
	    }
	    
    	switch (mode.getValue().getName()) {
    	case "Normal":
    	case "Fast":
    		if (stopWatch.reached(delay.getValue().intValue())) {
    			stopWatch.reset();
    			ticks = hits.getSecondValue().intValue();
        	}
        	break;
        	
    	case "Advanced":
    	    if (mc.player.isSprinting() && mc.player.serverSprintState && !blockInput && !startWaiting) {
    	    	double delayMultiplier = 1.0 / 3.0;
    	    	
    	        blockInputTicks = (int) (MathUtil.nextSecureInt(ticksUntilBlock.getValue().intValue(), ticksUntilBlock.getSecondValue().intValue()) * delayMultiplier);

    	        blockInput = blockInputTicks == 0;

    	        if (!blockInput) {
    	            startWaiting = true;
    	        }

    	        allowInputTicks = (int) (MathUtil.nextSecureInt(reSprintTicks.getValue().intValue(), reSprintTicks.getSecondValue().intValue()) * delayMultiplier);
    	    }
    		break;
    	}
    };
    
    @EventLink
    public final Listener<PreLivingUpdateEvent> onPreLiving = event -> {
    	if (mode.is("Advanced")) {
            if (blockInput) {
                if (ticksElapsed++ >= allowInputTicks) {
                    blockInput = false;
                    ticksElapsed = 0;
                }
            } else {
                if (startWaiting) {
                    blockInput = blockTicksElapsed++ >= blockInputTicks;

                    if (blockInput) {
                        startWaiting = false;
                        blockTicksElapsed = 0;
                    }
                }
            }
    	}
    };

	@EventLink
	public final Listener<PreMotionEvent> onPreUpdate = event -> {
	    if (ticks == hits.getSecondValue().intValue()) {
	        switch (mode.getValue().getName()) {
	            case "Normal":
	                mc.gameSettings.keyBindForward.pressed = false;
	                break;
	            case "Fast":
	                mc.gameSettings.keyBindForward.pressed = false;
	                mc.gameSettings.keyBindBack.pressed = true;
	                break;
	        }
	    } else if (ticks == hits.getValue().intValue()) {
	        switch (mode.getValue().getName()) {
            case "Normal":
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                break;
            case "Fast":
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
                break;
	        }
	    }

	    if (ticks > 0) {
	        ticks--;
	    }
	};

	private boolean isTwo() {
		return mode.is("Normal") && mode.is("Fast");
	}
}