package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class AutoBlockCheck extends Check {
    private int blockingTime;

    @Override
    public String getName() {
        return "Auto Block";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (player.isBlocking()) ++blockingTime;
        else blockingTime = 0;
        if (blockingTime > 5 && player.isSwingInProgress) {
            flag(player, "Swing when using item or blocking");
        }
    }
}