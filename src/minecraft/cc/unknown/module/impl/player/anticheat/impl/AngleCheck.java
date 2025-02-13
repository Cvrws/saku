package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class AngleCheck extends Check {

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (Math.abs(player.rotationYaw - player.prevRotationYaw) > 50 && player.swingProgress != 0F) {
            flag(player, "Too fast rotate speed");
        }

        if (player.rotationPitch > 90 || player.rotationPitch < -90) {
            flag(player, "Invalid rotation pitch");
        }
    }

    @Override
    public String getName() {
        return "Angle";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }
}