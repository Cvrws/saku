package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class VelocityCheck extends Check {

    public int vl;
	
    @Override
    public String getName() {
        return "Velocity";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (player.hurtResistantTime > 6 && player.hurtResistantTime < 12 && player.lastTickPosX == player.posX && player.posZ == player.lastTickPosZ && !mc.world.checkBlockCollision(player.getEntityBoundingBox().expand(0.05, 0.0, 0.05))) {
            vl++;
            if (vl >= 50) {
                flag(player, "Invalid velocity");
                vl = 0;
            }
        }
    }
}