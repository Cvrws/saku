package cc.unknown.command.impl;

import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.handlers.TransactionHandler;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public final class Transaction extends Command {
    
    public Transaction() {
        super("Muestra las transacciones del servidor", "transaction");
    }
    
    @Override
    public void execute(final String[] args) {
    	TransactionHandler.start();
    }
}