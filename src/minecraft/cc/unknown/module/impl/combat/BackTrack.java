package cc.unknown.module.impl.combat;

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
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.BoundsNumberValue;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

@ModuleInfo(aliases = "Back Track", description = "Utiliza la latencia para atacar desde más lejos", category = Category.COMBAT)
public final class BackTrack extends Module {
	private final BoundsNumberValue client = new BoundsNumberValue("Delay", this, 100, 200, 0, 500, 1);
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
        	if (stopWatch.reached(client.getValue().intValue(), client.getSecondValue().intValue())) {
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