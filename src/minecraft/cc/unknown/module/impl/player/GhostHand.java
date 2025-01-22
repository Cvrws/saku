package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Ghost Hand", description = "Te permite abrir cofres detras de las paredes.", category = Category.PLAYER)
public class GhostHand extends Module {

	private final NumberValue radius = new NumberValue("Radius", this, 4, 2, 7, 1);
	private boolean click = false;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!click && mc.gameSettings.keyBindUseItem.isKeyDown()) {
	        new Thread(() -> {
	            int radius = this.radius.getValue().intValue();
	            Block selectedBlock = Block.getBlockById(54);
	            float diff = 114514F;
	            BlockPos targetBlock = null;

	            for (int x = -radius; x < radius; x++) {
	                for (int y = radius; y > -radius + 1; y--) {
	                    for (int z = -radius; z < radius; z++) {
	                        int xPos = (int) mc.player.posX + x;
	                        int yPos = (int) mc.player.posY + y;
	                        int zPos = (int) mc.player.posZ + z;
	                        BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
	                        Block block = getBlock(blockPos);

	                        if (block == selectedBlock) {
	                            float dist = (float) mc.player.getDistanceSqToCenter(blockPos);
	                            if (dist < diff) {
	                                diff = dist;
	                                targetBlock = blockPos;
	                            }
	                        }
	                    }
	                }
	            }

	            if (targetBlock != null) {
	                if (mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getHeldItem(), targetBlock, EnumFacing.DOWN, targetBlock.getVec())) {
	                    mc.player.swingItem();
	                }
	            }
	        }, "GhostHand").start();
	        click = true;
	    } else if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
	        click = false;
	    }
	};
	
	public Block getBlock(BlockPos blockPos) {
	    if (blockPos == null || mc.world == null) {
	        return null;
	    }
	    return mc.world.getBlockState(blockPos).getBlock();
	}
}
