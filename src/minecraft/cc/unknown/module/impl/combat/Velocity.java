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
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Velocity", description = "Te vuelve un gordito come hamburguesas haciendo que no tengas kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	private final BooleanValue onSwing = new BooleanValue("Only Click", this, false);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
	    if (shouldSkipUpdate()) return;
	    EntityPlayer player = mc.player;
	    if (player == null) return;

	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = getFactor(chanceValue);

	    if (!shouldPerformAction(chanceValue, randomFactor)) return;
	    
		if (MoveUtil.isMoving() && player.hurtTime > 0 && player.motionY > 0 && (player.ticksSinceVelocity <= 14 || player.onGroundTicks <= 1)) {
			event.setJump(true);
		}
	};
	
	private boolean shouldSkipUpdate() {
	    return onSwing.getValue() && !mc.player.isSwingInProgress;
	}

	private double getFactor(double chanceValue) {
	    return Math.abs(Math.sin(System.nanoTime() * Double.doubleToLongBits(chanceValue))) * 100.0;
	}

	private boolean shouldPerformAction(double chanceValue, double randomFactor) {
	    return chanceValue >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D + randomFactor) < chanceValue;
	}
}