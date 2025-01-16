package cc.unknown.handlers;

import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.reset;
import static cc.unknown.util.render.ColorUtil.yellow;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionHandler implements Accessor {
	
	private static AtomicBoolean toggle = new AtomicBoolean(false);

	public static void start() {
		toggle.set(!toggle.get());
	}
	
    @EventLink
    public final Listener<PacketReceiveEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (!toggle.get()) return;
        
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            PlayerUtil.displayInClient(yellow + "[" + green + "*" + yellow + "] " + reset + String.format(red + "Transaction " + reset + " (ID: %s) (WindowID: %s)", wrapper.actionNumber, wrapper.windowId));
        }
    };
}
