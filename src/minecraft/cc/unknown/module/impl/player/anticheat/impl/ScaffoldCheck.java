package cc.unknown.module.impl.player.anticheat.impl;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.anticheat.Check;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;

public class ScaffoldCheck extends Check {

    private float yaw;
    private float cacheYaw;
    private boolean rotate;

    @Override
    public String getName() {
        return "Scaffold";
    }

    @Override
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) {

    }

    @Override
    public void onPreLiving(EntityPlayer player) {
        cacheYaw = yaw;
        yaw = player.rotationYaw;
        if (cacheYaw == yaw + 180) {
            rotate = true;
        }
        if (player.isSwingInProgress && player.rotationPitch > 70 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock && !player.isSneaking() && rotate) {
            flag(player, "Scaffold");
            rotate = false;
        }
    }
}