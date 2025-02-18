package cc.unknown.module.impl.world.cancelpackets;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.module.impl.world.CancelPackets;
import cc.unknown.value.Mode;

public class OutgoingCancel extends Mode<CancelPackets> {
	public OutgoingCancel(String name, CancelPackets parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<PacketSendEvent> onSend = event -> {
        if (getParent().shouldCancelPacket(event.getPacket())) {
            event.setCancelled(true);
        }
    };
}
