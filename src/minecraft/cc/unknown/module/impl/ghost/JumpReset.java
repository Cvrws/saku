package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Jump Reset", description = "Salta automáticamente al recibir daño para resetear el KB.", category = Category.GHOST)
public class JumpReset extends Module {

    private final BooleanValue onlyTarget = new BooleanValue("Require Target", this, false);
    private final BooleanValue disabledWhileHold = new BooleanValue("Disable while holding S", this, false);
    private final BooleanValue onlyClick = new BooleanValue("Require Clicking", this, false);
    private final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
    private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump", this, true);
    private final BooleanValue legitTiming = new BooleanValue("Normal Distribution", this, false);
    private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

    private boolean shouldJump;

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> shouldJump = false;

    @EventLink
    public final Listener<MoveInputEvent> onMoveInput = event -> {
        if (!shouldJump || noAction() || !MoveUtil.isMoving() || !shouldPerformAction()) return;
        event.setJump(true);
    };

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.isCancelled() || !mc.player.onGround || noAction() || !shouldPerformAction()) return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
        	S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
            if (wrapper.getEntityID() == mc.player.getEntityId() && wrapper.motionY > 0 &&
                (!legitTiming.getValue() || mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1)) {
                shouldJump = true;
            }
        }
    };

    private boolean noAction() {
        return mc.player.getActivePotionEffects().stream().anyMatch(effect ->
            (notWhileSpeed.getValue() && effect.getPotionID() == Potion.moveSpeed.getId()) ||
            (notWhileJumpBoost.getValue() && effect.getPotionID() == Potion.jump.getId()));
    }

    private boolean shouldPerformAction() {
        return MathUtil.nextRandom(0, 100).intValue() <= chance.getValue().doubleValue()
                && !(onlyClick.getValue() && !mc.player.isSwingInProgress)
                && !(onlyTarget.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null))
                && !(disabledWhileHold.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
    }
}
