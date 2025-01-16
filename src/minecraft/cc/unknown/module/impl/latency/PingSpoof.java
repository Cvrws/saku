package cc.unknown.module.impl.latency;

import java.util.ArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;

@ModuleInfo(aliases = "Ping Spoof", description = "Simula un estado de conexión muy alto", category = Category.LATENCY)
public class PingSpoof extends Module {
	
	private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 4000, 50);
	private final ArrayList<C00PacketKeepAlive> keepAlivePackets = new ArrayList<C00PacketKeepAlive>();
	
    @Override
    public void onEnable() {
        super.onEnable();
        keepAlivePackets.clear();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        if (!keepAlivePackets.isEmpty()) {
            keepAlivePackets.clear();
        }
    }
	
    @EventLink
    public final Listener<PacketSendEvent> onPacketReceive = event -> {
    	Packet<?> packet = event.getPacket();
        if (packet instanceof C00PacketKeepAlive) {
            final C00PacketKeepAlive keepAlive = (C00PacketKeepAlive) packet;
            keepAlivePackets.add(new C00PacketKeepAlive(keepAlive.getKey(), (long)(System.currentTimeMillis() + delay.getValue().longValue() + MathUtil.nextRandom(0L, 200L).longValue())));
            event.setCancelled(true);
        }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	if (mc.player != null && !keepAlivePackets.isEmpty()) {
    		final ArrayList<C00PacketKeepAlive> remove = new ArrayList<C00PacketKeepAlive>();
    		for (final C00PacketKeepAlive packet : keepAlivePackets) {
    			if (packet.getTime() < System.currentTimeMillis()) {
    				PacketUtil.sendNoEvent(new C00PacketKeepAlive(packet.getKey(), 0));
    				remove.add(packet);
    			}
            }
            keepAlivePackets.removeIf(remove::contains);
        }
    };
}
