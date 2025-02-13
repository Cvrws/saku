package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class NoFallCheck extends Check {
    boolean fall;

    @Override
    public String getName() {
        return "No Fall";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (player.fallDistance > 3) {
            fall = true;
        }
        if (fall && player.fallDistance == 0 && player.hurtTime == 0 && !player.isInWater()) {
            flag(player, "Not taking any damage");
            fall = false;
        }
    }
}