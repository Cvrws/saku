package cc.unknown.module.impl.ghost;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Jump Reset", description = "Salta automaticamente al recibir daño para resetear el kb.", category = Category.GHOST)
public class JumpReset extends Module {
	
	private final BooleanValue onlyTarget = new BooleanValue("Require Target", this, false);
	private final BooleanValue disabledWhileHold = new BooleanValue("Disable while holding S", this, false);
	private final BooleanValue onlyClick = new BooleanValue("Require Clicking", this, false);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
        if (onlyClick.getValue() && mc.player.isSwingInProgress) {
            return;
        }
        
        if (onlyTarget.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
            return;
        }
        
        if (disabledWhileHold.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            return;
        }

	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = getRandomFactor(chanceValue);

	    if (!shouldPerformAction(chanceValue, randomFactor)) return;
	    
		if (MoveUtil.isMoving() && mc.player.hurtTime > 0 && mc.player.motionY > 0 && (mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1)) {
			event.setJump(true);			
		}
	};

	private double getRandomFactor(double chanceValue) {
	    return Math.abs(Math.sin(System.nanoTime() * Double.doubleToLongBits(chanceValue))) * 100.0;
	}

	private boolean shouldPerformAction(double chanceValue, double randomFactor) {
	    return chanceValue >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D + randomFactor) < chanceValue;
	}

}