package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BoundsNumberValue;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

@ModuleInfo(aliases = "Clumsy", description = "Empeora significativamente tu latencia.", category = Category.LATENCY)
public final class Clumsy extends Module {
	private final BoundsNumberValue client = new BoundsNumberValue("ClientSide Delay", this, 100, 200, 0, 500, 1);
	private final BoundsNumberValue server = new BoundsNumberValue("ServerSide Delay", this, 20, 30, 0, 500, 1);
	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private final List<Packet> s14 = new CopyOnWriteArrayList<>();
	private StopWatch stopWatch = new StopWatch();
	private StopWatch stopWatch2 = new StopWatch();

	@Override
	public void onDisable() {
		this.releasePackets();
	}

	@EventLink
	public final Listener<PacketSendEvent> onPacketSend = event -> {		
		if (this.isEnabled() && mc.player != null) {
			packets.add(event.getPacket());
			event.setCancelled(true);
		}
	};
	
	@EventLink
	public final Listener<PacketReceiveEvent> onPacket = event -> {		
		Packet<?> p = event.getPacket();
		if (p instanceof S14PacketEntity) {
			s14.add(p);
			event.setCancelled(true);
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		handleDelay();
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
		handleDelay();
	};
	
	private void handleDelay() {
        if (this.isEnabled() && mc.player != null) {
        	if (stopWatch.reached(client.getValue().intValue(), client.getSecondValue().intValue())) {
        	    while (!packets.isEmpty()) {
        			PacketUtil.sendNoEvent(packets.get(0));
        			packets.remove(packets.get(0));
        	    }
        	    stopWatch.reset();
        	}
        	
        	if (stopWatch2.reached(server.getValue().intValue(), server.getSecondValue().intValue())) {
        		while (!s14.isEmpty()) {
        			s14.get(0).processPacket(mc.getNetHandler().getNetworkManager().packetListener);
        			s14.remove(s14.get(0));
        		}
        		stopWatch2.reset();
        	}
        }
	}
	
	public void releasePackets() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue
					.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
	}
	
	private boolean prevent() {
		return (getModule(Scaffold.class).isEnabled());
	}
}