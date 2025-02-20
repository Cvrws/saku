package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(aliases = "Jump Reset", description = "Salta automáticamente al recibir daño para resetear el KB.", category = Category.GHOST)
public class JumpReset extends Module {

    private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

    private final BooleanValue onlyClick = new BooleanValue("Require Clicking", this, false);
    private final BooleanValue onlyTarget = new BooleanValue("Require Target", this, false);
    private final BooleanValue disabledWhileHold = new BooleanValue("Disable while holding S", this, false);
    private final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
    private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump", this, true);
    
    private final BooleanValue debug = new BooleanValue("Debug", this, true);

    private EntityPlayer target;
    private boolean shouldJump;
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> target = (EntityPlayer) event.getTarget();
	
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
    	shouldJump = false;
    };

    @EventLink
    public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (!MathUtil.isChance(chance, notWhileSpeed, notWhileJumpBoost) && MathUtil.shouldPerformAction(onlyClick, onlyTarget, disabledWhileHold, target)) return;
		if (target == null) return;
		if (isEnabled(NoClip.class)) return;
		
        if (shouldJump && MoveUtil.isMoving()) {
            event.setJump(true);
        }
    };

    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
		if (!MathUtil.isChance(chance, notWhileSpeed, notWhileJumpBoost) && MathUtil.shouldPerformAction(onlyClick, onlyTarget, disabledWhileHold, target)) return;
		if (target == null) return;
		if (isEnabled(NoClip.class)) return;
		
        if (!mc.player.onGround) {
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            if (wrapper.getEntityID() == mc.player.getEntityId() && wrapper.motionY > 0 && (mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1)) {
            	shouldJump = true;
            }
        }
    };
}
