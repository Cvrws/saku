package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.DisconnectionEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;

@ModuleInfo(aliases = "Fake Lag", description = "Retiene los datos del servidor ocasionando lag (BETA)", category = Category.LATENCY)
public class FakeLag extends Module {
	
	private ModeValue mode = new ModeValue("Withhold Mode", this)
			.add(new SubMode("Delay"))
			.add(new SubMode("Tick"))
			.setDefault("Delay");
	
	private NumberValue tick = new NumberValue("Tick", this, 2, 1, 10, 1, () -> !mode.is("Tick"));
	private NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 10, () -> !mode.is("Delay"));
	
	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private StopWatch stopWatch = new StopWatch();

	@Override
	public void onDisable() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
	}
	
	@EventLink
	public Listener<PacketSendEvent> onSend = event -> {
		if (mc.player == null) return;
		Packet<?> packet = event.getPacket();
		
		packets.add(packet);
		event.setCancelled();
	};
	
	@EventLink
	public final Listener<GameEvent> onGame = event -> {
		if (this.isEnabled() && mc.player != null) {
			switch (mode.getValue().getName()) {
			case "Delay":
		    	if (stopWatch.reached(delay.getValue().intValue())) {
		    		sendPackets();
		    	    stopWatch.reset();
		    	}
				break;
			case "Tick":
				for (int i = 0; i < tick.getValue().intValue(); i++) {
					sendPackets();
				}
				break;
			}
		}
	};
	
	public void sendPackets() {
	    while (!packets.isEmpty()) {
			PacketUtil.sendNoEvent(packets.get(0));
			packets.remove(packets.get(0));
	    }
	}
 	
	@EventLink
	public Listener<DisconnectionEvent> onDisconnect = event -> {
		this.toggle();
	};
}
