package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;

@ModuleInfo(aliases = "Back Track", description = "Incrementa la distancia al golpear utilizando lag", category = Category.LATENCY)
public final class BackTrack extends Module {
	private final NumberValue client = new NumberValue("Delay", this, 200, 0, 1000, 1);
	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private StopWatch stopWatch = new StopWatch();

	@Override
	public void onEnable() {
		packets.clear();
	}
	
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
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		handleDelay();
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
		handleDelay();
	};
	
	private void handleDelay() {
        if (mc.player != null) {
        	if (stopWatch.finished(client.getValue().intValue())) {
        	    while (!packets.isEmpty()) {
        			PacketUtil.sendNoEvent(packets.get(0));
        			packets.remove(packets.get(0));
        	    }
        	    stopWatch.reset();
        	}
        }
	}
	
	public void releasePackets() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
	}
	
	private boolean prevent() {
		return (getModule(Scaffold.class).isEnabled());
	}
}