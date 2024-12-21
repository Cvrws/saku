package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Velocity", description = "Te vuelve un gordito come hamburguesas haciendo que no tengas kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	private final BooleanValue onSwing = new BooleanValue("Only Click", this, false);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
	    if (shouldSkipUpdate()) return;

	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = getRandomFactor(chanceValue);

	    if (!shouldPerformAction(chanceValue, randomFactor)) return;
	    
		if (MoveUtil.isMoving() && mc.player.hurtTime > 0 && mc.player.motionY > 0 && (mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1)) {
			event.setJump(true);
		}
	};
	
	private boolean shouldSkipUpdate() {
	    return onSwing.getValue() && !mc.player.isSwingInProgress;
	}

	private double getRandomFactor(double chanceValue) {
	    return Math.abs(Math.sin(System.nanoTime() * Double.doubleToLongBits(chanceValue))) * 100.0;
	}

	private boolean shouldPerformAction(double chanceValue, double randomFactor) {
	    return chanceValue >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D + randomFactor) < chanceValue;
	}
}