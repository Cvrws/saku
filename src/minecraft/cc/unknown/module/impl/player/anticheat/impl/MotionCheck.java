package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.entity.player.EntityPlayer;

public class MotionCheck extends Check {

    @Override
    public String getName() {
        return "Invalid motion";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        double base = MoveUtil.getBaseMoveSpeed(player);
        double speed = Math.hypot(player.motionX, player.motionZ);
        if (speed > (base * 1.25f) && player.hurtTime == 0) {
            flag(player, "Too fast");
        }

        if (!player.onGround && !MoveUtil.isMoving(player) && player.motionY == 0.0D && player.offGroundTicks >= 5) {
            flag(player, "Not moving on air for a long time");
        }
    }
}