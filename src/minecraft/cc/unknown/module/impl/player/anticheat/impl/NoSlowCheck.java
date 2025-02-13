package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;

public class NoSlowCheck extends Check {

    private int sprintBuffer = 0, motionBuffer = 0;

    @Override
    public String getName() {
        return "No Slow";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        if (player.isUsingItem() || player.isBlocking()) {
            if (player.isSprinting()) {
                if (++sprintBuffer > 5) {
                    flag(player, "Sprinting when using item or blocking");
                }
                return;
            }

            double dx = player.prevPosX - player.posX, dz = player.prevPosZ - player.posZ;
            if (dx * dx + dz * dz > 0.07) {
                if (++motionBuffer > 10 && player.hurtTime == 0) {
                    flag(player, "Not sprinting but keep in sprint motion when blocking");
                    motionBuffer = 7;
                    return;
                }
            }
            motionBuffer -= (motionBuffer > 0 ? 1 : 0);
            sprintBuffer -= (sprintBuffer > 0 ? 1 : 0);
        }
    }
}