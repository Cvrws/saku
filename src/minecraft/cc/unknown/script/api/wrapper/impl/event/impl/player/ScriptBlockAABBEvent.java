package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.script.api.wrapper.impl.ScriptBlock;
import cc.unknown.script.api.wrapper.impl.ScriptBlockPos;
import cc.unknown.script.api.wrapper.impl.ScriptWorld;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ScriptBlockAABBEvent extends ScriptEvent<BlockAABBEvent> {

    public ScriptBlockAABBEvent(final BlockAABBEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public ScriptWorld getWorld() {
    	return new ScriptWorld(this.wrapped.getWorld());
    }
    
    public ScriptBlock getBlock() {
    	return new ScriptBlock(this.wrapped.getBlock());
    }
    
    public ScriptBlockPos getBlockPos() {
    	return new ScriptBlockPos(this.wrapped.getBlockPos());
    }
    
    public AxisAlignedBB getMaskBoundingBox() {
    	return this.wrapped.getMaskBoundingBox();
    }
    
    public AxisAlignedBB getBoundingBox() {
    	return this.wrapped.getBoundingBox();
    }
    
    public void setBoundingBox(AxisAlignedBB axis) {
    	this.wrapped.setBoundingBox(axis);
    }

    @Override
    public String getHandlerName() {
        return "onBlockAABB";
    }
}
