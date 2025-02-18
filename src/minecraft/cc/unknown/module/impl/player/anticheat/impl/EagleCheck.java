package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;

public class EagleCheck extends Check {
    private int sneakFlag;

    @Override
    public String getName() {
        return "Eagle";
    }

    @Override
    public void onPlayer(EntityPlayer player) {
        if (player.isSneaking()) {
        	stopWatch.reset();
            sneakFlag += 1;
        }

        if (stopWatch.finished(140)) {
            sneakFlag = 0;
        }
        
        if (player.rotationPitch > 75 && player.rotationPitch < 90 && player.isSwingInProgress) {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock) {
                if (MoveUtil.strafe(player) >= 0.10 && player.onGround && sneakFlag > 5) {
                    flag(player, "Sneak too fast");
                }
                if (MoveUtil.strafe(player) >= 0.21 && !player.onGround && sneakFlag > 5) {
                    flag(player, "Sneak too fast");
                }
            }
        }
    }
}