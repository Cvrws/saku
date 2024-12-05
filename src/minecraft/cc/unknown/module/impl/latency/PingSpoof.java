package cc.unknown.module.impl.latency;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S00PacketKeepAlive;

@ModuleInfo(aliases = "Ping Spoof", description = "Simula un estado de conexión muy alto", category = Category.LATENCY)
public class PingSpoof extends Module {
	
	private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 2000, 50);
	
	private ConcurrentHashMap<Packet<?>, Long> packetQueue = new ConcurrentHashMap();
	
    @EventLink
    public final Listener<PacketSendEvent> onPacketReceive = event -> {
    	Packet<?> packet = event.getPacket();
        if (packet instanceof C00PacketKeepAlive) {
            this.packetQueue.put(packet, System.currentTimeMillis() + this.delay.getValue().longValue());
            event.setCancelled();
         }
    };
    
    @EventLink
    public final Listener<GameEvent> onGame = event -> {
    	Iterator iterator = this.packetQueue.entrySet().iterator();

    	Entry entry;
        while(iterator.hasNext()) {
           entry = (Entry)iterator.next();
           if ((Long)entry.getValue() < System.currentTimeMillis()) {
              PacketUtil.sendNoEvent((Packet)entry.getKey());
              iterator.remove();
           }
        }
    };
}
