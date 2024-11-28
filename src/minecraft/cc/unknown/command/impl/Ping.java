package cc.unknown.command.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.command.Command;
import static cc.unknown.util.client.StreamerUtil.*;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

public final class Ping extends Command {
    
    public Ping() {
        super("Muestra tu latencia", "ping");
    }
    
    @Override
    public void execute(final String[] args) {
        int ping = PlayerUtil.getPing(mc.player);
        String color;
        if (ping >= 0 && ping <= 99) {
        	color = green.toString();
        } else if (ping >= 100 && ping <= 199) {
        	color = yellow.toString();
        } else {
        	color = red.toString();
        }
        PlayerUtil.display(reset.toString() + " Tu ping actual es de: " + color + ping + "ms");
    }
}