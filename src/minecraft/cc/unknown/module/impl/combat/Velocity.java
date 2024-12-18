package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PostUpdateEvent;
import cc.unknown.event.impl.player.PreLivingUpdateEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(aliases = "Velocity", description = "Te vuelve un gordito come hamburguesas haciendo que no tengas kb.", category = Category.COMBAT)
public final class Velocity extends Module {

	private boolean reduced;

	@Override
	public void onEnable() {
		reduced = false;
	}
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.player.hurtTime == 9 && !mc.player.isBurning()) {
			reduced = true;
		} else reduced = false;
	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		if (reduced) {
			event.setJump(true);
		}
	};
	
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		Packet p = event.getPacket();
		
		if (p instanceof S12PacketEntityVelocity) {
			final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;

			if (wrapper.getEntityID() == mc.player.getEntityId()) {	
				if (reduced) {
					mc.player.jump();
				}
			}
		}
	};
}