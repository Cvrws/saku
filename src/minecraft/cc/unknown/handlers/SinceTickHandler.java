package cc.unknown.handlers;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.TickEndEvent;
import cc.unknown.module.Module;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.geometry.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class SinceTickHandler implements Accessor {

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if (mc == null || mc.world == null || event.isCancelled()) return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            Entity entity = mc.world.getEntityByID(wrapper.getEntityID());

            if (entity == null) {
                return;
            }

            entity.lastVelocityDeltaX = wrapper.motionX / 8000.0D;
            entity.lastVelocityDeltaY = wrapper.motionY / 8000.0D;
            entity.lastVelocityDeltaZ = wrapper.motionZ / 8000.0D;
            entity.ticksSinceVelocity = 0;
            if (wrapper.motionY / 8000.0D > 0.1 && Math.hypot(wrapper.motionZ / 8000.0D, wrapper.motionX / 8000.0D) > 0.2) {
                entity.ticksSincePlayerVelocity = 0;
            }
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
    public final Listener<PacketSendEvent> onPacketSend = event -> {
    	if (mc == null || mc.world == null || event.isCancelled()) return;
    	
    	Packet<?> packet = event.getPacket();
    	
    	if (packet instanceof C08PacketPlayerBlockPlacement && !((C08PacketPlayerBlockPlacement) packet).getPosition().equalsVector(new Vector3d(-1, -1, -1))) {
    		mc.player.ticksSincePlace = 0;
    	}
    	
        if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity) packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
        	mc.player.ticksSinceAttack = 0;
        }
        mc.player.ticksSinceAttack++;
    };
}
