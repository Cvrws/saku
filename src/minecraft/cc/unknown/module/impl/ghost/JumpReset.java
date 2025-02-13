package cc.unknown.module.impl.ghost;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

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
	    
        if (mc.currentScreen != null) return;
        if (mc.player.hurtTime == 10) {
        	shouldJump = MathUtil.shouldPerformAction(chanceValue, MathUtil.getRandomFactor(chanceValue));
        }
        
        if (!shouldJump) return;
        if (mc.player.hurtTime >= 8) {
            mc.gameSettings.keyBindJump.pressed = true;
        }
        if (mc.player.hurtTime >= 7) {
            mc.gameSettings.keyBindForward.pressed = true;
        } else if (mc.player.hurtTime >= 4) {
            mc.gameSettings.keyBindJump.pressed = false;
            mc.gameSettings.keyBindForward.pressed = false;
        } else if (mc.player.hurtTime > 1){
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
        }
    };
    
    @Override
    public void onDisable() {
        mc.gameSettings.keyBindJump.pressed = false;
        mc.gameSettings.keyBindForward.pressed = false;
    }

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
