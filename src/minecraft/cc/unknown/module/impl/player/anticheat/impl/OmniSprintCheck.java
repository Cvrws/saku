package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class OmniSprintCheck extends Check {
    @Override
    public String getName() {
        return "Omni Sprint";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (player.isSprinting() && (player.moveForward < 0.0f || player.moveForward == 0.0f && player.moveStrafing != 0.0f)) {
            flag(player, "Sprinting when moving backward");
        }
    }
}