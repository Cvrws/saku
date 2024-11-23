package cc.unknown.command.impl;

import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public final class Transaction extends Command {

	private AtomicBoolean toggle = new AtomicBoolean(false);
    
    public Transaction() {
        super("Muestra las transacciones del servidor", "transaction");
        Sakura.instance.getEventBus().register(this);
    }
    
    @Override
    public void execute(final String[] args) {
        toggle.set(!toggle.get());
    }
    
    @EventLink
    public final Listener<PacketReceiveEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (!toggle.get()) return;
        
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            PlayerUtil.display(red + " Transaction " + reset + 
                " (ID: %s) (WindowID: %s)", wrapper.actionNumber, wrapper.windowId);
        }
    };
}