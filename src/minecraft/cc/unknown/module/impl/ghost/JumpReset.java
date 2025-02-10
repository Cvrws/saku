package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Jump Reset", description = "Salta automáticamente al recibir daño para resetear el KB.", category = Category.GHOST)
public class JumpReset extends Module {

    private final BooleanValue onlyTarget = new BooleanValue("Require Target", this, false);
    private final BooleanValue disabledWhileHold = new BooleanValue("Disable while holding S", this, false);
    private final BooleanValue onlyClick = new BooleanValue("Require Clicking", this, false);
    private final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
    private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump", this, true);
    private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

    private boolean shouldJump;

    @EventLink
    public final Listener<PreStrafeEvent> onPreMotion = event -> {
	    double chanceValue = chance.getValue().doubleValue();

        if (noAction()) return;
	    if (!MathUtil.shouldPerformAction(chanceValue, MathUtil.getRandomFactor(chanceValue))) return;
	    
    	if (mc.player.hurtTime == 9 && mc.player.ticksSinceVelocity <= 14) {
    		if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !checks()) {
    			mc.player.jump();
    		}
    	}
    };

    private boolean noAction() {
        return mc.player.getActivePotionEffects().stream().anyMatch(effect ->
            (notWhileSpeed.getValue() && effect.getPotionID() == Potion.moveSpeed.getId()) ||
            (notWhileJumpBoost.getValue() && effect.getPotionID() == Potion.jump.getId()));
    }

    private boolean shouldPerformAction() {
        return !(onlyClick.getValue() && !mc.player.isSwingInProgress)
                && !(onlyTarget.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null))
                && !(disabledWhileHold.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
    }
    private boolean checks() {
        return mc.player.isInWeb || mc.player.isInLava() || mc.player.isBurning() || mc.player.isInWater();
    }
}
