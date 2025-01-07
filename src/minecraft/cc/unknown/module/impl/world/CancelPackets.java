package cc.unknown.module.impl.world;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.api.IncomingPackets;
import cc.unknown.util.netty.api.OutgoingPackets;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;

@ModuleInfo(aliases = "Cancel Packets", description = "Cancela los datos del cliente/servidor", category = Category.WORLD)
public class CancelPackets extends Module {
    
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Outgoing"))
            .add(new SubMode("Incoming"))
            .setDefault("Outgoing");
    
    private final Map<Class<? extends Packet<?>>, BooleanValue> packetSettings = new HashMap<>();

    public CancelPackets() {
        for (Class<? extends Packet<INetHandlerPlayServer>> packet : OutgoingPackets.getOutgoingPackets()) {
            packetSettings.put(packet, new BooleanValue(packet.getSimpleName(), this, false, () -> !mode.is("Outgoing")));
        }
        
        for (Class<? extends Packet<INetHandlerPlayClient>> packet : IncomingPackets.getIncomingPackets()) {
            packetSettings.put(packet, new BooleanValue(packet.getSimpleName(), this, false, () -> !mode.is("Incoming")));
        }
    }

    @EventLink
    public final Listener<PacketSendEvent> onSend = event -> {
        if (shouldCancelPacket(event.getPacket(), mode.is("Outgoing"))) {
            event.setCancelled(true);
        }
    };

    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        if (shouldCancelPacket(event.getPacket(), mode.is("Incoming"))) {
            event.setCancelled(true);
        }
    };
    
    private boolean shouldCancelPacket(Packet<?> packet, boolean isOutgoing) {
    	BooleanValue setting = packetSettings.get(packet.getClass());
    	return setting != null && setting.getValue() && isOutgoing;
    }
}