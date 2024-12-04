package cc.unknown.script.api.wrapper.impl;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.script.api.wrapper.ScriptWrapper;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ScriptAxisAlignedBB extends ScriptWrapper<AxisAlignedBB> {

    public ScriptAxisAlignedBB(final AxisAlignedBB wrapped) {
        super(wrapped);
    }


}
