package cc.unknown.module.impl.world.cancelpackets;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.world.CancelPackets;
import cc.unknown.value.Mode;

public class IncomingCancel extends Mode<CancelPackets> {
	public IncomingCancel(String name, CancelPackets parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        if (getParent().shouldCancelPacket(event.getPacket())) {
            event.setCancelled(true);
        }
    };
}
