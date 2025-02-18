package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class AimCheck extends Check {

    @Override
    public void onPlayer(EntityPlayer player) {
        if (Math.abs(player.rotationPitch) > 90) {
            flag(player, "Invalid rotation pitch");
        }
    }

    @Override
    public String getName() {
        return "Aim";
    }
}