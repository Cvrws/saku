package cc.unknown.command.impl;

import static cc.unknown.util.streamer.StreamerUtil.red;
import static cc.unknown.util.streamer.StreamerUtil.reset;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.component.impl.player.PingComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public final class Ping extends Command {

	private AtomicBoolean toggle = new AtomicBoolean(false);
    
    public Ping() {
        super("Analiza tu latencia", "ping");
        Sakura.instance.getEventBus().register(this);
    }
    
    @Override
    public void execute(final String[] args) {
        toggle.set(!toggle.get());        
        ChatUtil.display("Tienes " + PingComponent.getPing() + "ms");
    }
}