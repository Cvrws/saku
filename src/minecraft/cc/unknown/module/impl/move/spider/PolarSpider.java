package cc.unknown.module.impl.move.spider;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.move.Spider;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PolarSpider extends Mode<Spider> {
	public PolarSpider(String name, Spider parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		boolean isInsideBlock = insideBlock();
		
		if (mc.player.isCollidedHorizontally && !isInsideBlock) {
			double yaw = MoveUtil.direction();
			MoveUtil.stop();
			MoveUtil.keybindStop();
		}

		if (Mouse.isButtonDown(getParent().mouseButton.getValue().intValue()) && isInsideBlock) {
			if (getParent().fast.getValue()) {
				if (mc.player.onGround) {
					mc.player.motionY += 0.65999D;
				} else {
					mc.player.motionY -= 0.005D;
				}
			} else {
				if (mc.player.onGround) {
					mc.player.motionY += 0.64456D;
				} else {
					mc.player.motionY -= 0.005D;
				}
			}
		}
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockBB = event -> {
		if (insideBlock()) {
			BlockPos playerPos = new BlockPos(mc.player);
			BlockPos blockPos = event.getBlockPos();
			if (blockPos.getY() > playerPos.getY())
				event.setBoundingBox(null);
		}
	};

	private boolean insideBlock(final AxisAlignedBB bb) {
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(mc.world, new BlockPos(x, y, z), mc.world.getBlockState(new BlockPos(x, y, z)))) != null && bb.intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean insideBlock() {
		if (mc.player.ticksExisted < 5) {
			return false;
		}

		return insideBlock(mc.player.getEntityBoundingBox());
	}
}
