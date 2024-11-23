package cc.unknown.command.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.component.impl.player.PingComponent;
import cc.unknown.util.player.PlayerUtil;

public final class Ping extends Command {

	private AtomicBoolean toggle = new AtomicBoolean(false);
    
    public Ping() {
        super("Muestra tu latencia", "ping");
        getInstance().getEventBus().register(this);
    }
    
    @Override
    public void execute(final String[] args) {
        toggle.set(!toggle.get());        
        PlayerUtil.display("Tienes " + PingComponent.getPing() + "ms");
    }
}