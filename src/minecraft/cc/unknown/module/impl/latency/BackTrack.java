package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.BoundsNumberValue;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

@ModuleInfo(aliases = "Back Track", description = "Incrementa la distancia al golpear utilizando lag", category = Category.LATENCY)
public final class BackTrack extends Module {
	
	private final BoundsNumberValue client = new BoundsNumberValue("ClientSide Delay", this, 100, 200, 0, 500, 1);
	private final BoundsNumberValue server = new BoundsNumberValue("ServerSide Delay", this, 20, 30, 0, 500, 1);
	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private final List<Packet> s14 = new CopyOnWriteArrayList<>();
	private StopWatch sendTime = new StopWatch();
	private StopWatch receiveTime = new StopWatch();

	@Override
	public void onEnable() {
		packets.clear();
	}

	@Override
	public void onDisable() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
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
        	if (sendTime.reached(client.getValue().intValue(), client.getSecondValue().intValue())) {
        	    while (!packets.isEmpty()) {
        			PacketUtil.sendNoEvent(packets.get(0));
        			packets.remove(packets.get(0));
        	    }
        	    sendTime.reset();
        	}
        	
        	if (receiveTime.reached(server.getValue().intValue(), server.getSecondValue().intValue())) {
        		while (!s14.isEmpty()) {
        			s14.get(0).processPacket(mc.getNetHandler().getNetworkManager().packetListener);
        			s14.remove(s14.get(0));
        		}
        		receiveTime.reset();
        	}
        }
	}

}