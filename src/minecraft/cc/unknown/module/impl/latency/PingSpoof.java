package cc.unknown.module.impl.latency;

import java.util.LinkedHashMap;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;

@ModuleInfo(aliases = "Ping Spoof", description = "Simula un estado de conexión muy alto", category = Category.LATENCY)
public class PingSpoof extends Module {
    private final LinkedHashMap<Packet<?>, Long> packetQueue = new LinkedHashMap<>();

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
    	Packet<?> packet = event.getPacket();
    	if (packet instanceof S00PacketKeepAlive) {
    		event.setCancelled();
    		synchronized (packetQueue) {
    			packetQueue.put(packet, System.currentTimeMillis());
    		}
    	}   
    };
}
