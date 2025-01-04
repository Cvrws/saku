package cc.unknown.handlers;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.TickEndEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.ui.clickgui.rice.RiceGui;
import cc.unknown.util.Accessor;
import cc.unknown.util.geometry.Vector3d;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class SinceTickHandler implements Accessor {

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreMotionEvent> onPre = event -> {
		if (mc.player.hurtTime == 9 && mc.player.motionY / 8000.0D > 0.1 && Math.hypot(mc.player.motionZ / 8000.0D, mc.player.motionX / 8000.0D) > 0.2) {
			mc.player.ticksSinceVelocity++;
		}
	};
	
	@EventLink
	public final Listener<TickEndEvent> onPostTick = event -> {
		for (Module module : Sakura.instance.getModuleManager().getAll()) {
			if (mc.currentScreen instanceof RiceGui) {
				module.guiUpdate();
			}
        }
	};
	
	@EventLink(value = Priority.VERY_LOW)
	public final Listener<TeleportEvent> onTeleport = event -> {
		mc.player.ticksSinceTeleport = 0;
	};
    
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketSendEvent> onPacketSend = event -> {
    	if (mc == null || mc.theWorld == null || event.isCancelled()) return;
    	
    	Packet<?> packet = event.getPacket();
    	
    	if (packet instanceof C08PacketPlayerBlockPlacement && !((C08PacketPlayerBlockPlacement) packet).getPosition().equalsVector(new Vector3d(-1, -1, -1))) {
    		mc.player.ticksSincePlace = 0;
    	}
    };

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<AttackEvent> onAttack = event -> {
        mc.player.ticksSinceAttack = 0;
    };
}
