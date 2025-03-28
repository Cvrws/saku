package cc.unknown.script.api.wrapper.impl;

import cc.unknown.script.api.wrapper.ScriptWrapper;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import net.minecraft.util.BlockPos;

public class ScriptBlockPos extends ScriptWrapper<BlockPos> {

    public ScriptBlockPos(final BlockPos wrapped) {
        super(wrapped);
    }

    public ScriptVector3d getPosition() {
        return new ScriptVector3d(this.wrapped.getX(), this.wrapped.getY(), this.wrapped.getZ());
    }

    public float getHardness() {
        return InventoryUtil.getPlayerRelativeBlockHardness(MC.player, MC.world, this.wrapped, MC.player.inventory.currentItem);
    }

    public float getHardness(int hotBarSlot) {
        return InventoryUtil.getPlayerRelativeBlockHardness(MC.player, MC.world, this.wrapped, hotBarSlot);
    }

    public ScriptBlock getBlock() {
        return new ScriptBlock(PlayerUtil.block(this.wrapped));
    }
}
