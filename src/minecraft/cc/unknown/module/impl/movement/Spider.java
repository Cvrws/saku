package cc.unknown.module.impl.movement;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = { "Spider",
		"wallclimb" }, description = "Permite trepar por las paredes como una araña.", category = Category.MOVEMENT)
public class Spider extends Module {

	private float direction = 0.0F;

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Vulcan"))
			.add(new SubMode("Polar"))
			.setDefault("Vulcan");

	private final BooleanValue fast = new BooleanValue("Fast", this, true, () -> !mode.is("Polar"));
	private final DescValue help = new DescValue("0 -> Left | 1 -> Right | 2 -> Middle", this, () -> !mode.is("Polar"));
	private final NumberValue mouseButton = new NumberValue("Mouse button to go up faster", this, 1, 0, 2, 1, () -> !mode.is("Polar"));

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mode.is("vulcan")) {
			if (mc.player.isCollidedHorizontally) {
				if (mc.player.ticksExisted % 2 == 0) {
					event.setOnGround(true);
					mc.player.motionY = MoveUtil.jumpMotion();
				}

				final double yaw = MoveUtil.direction();
				event.setPosX(event.getPosX() - -MathHelper.sin((float) yaw) * 0.00000001);
				event.setPosZ(event.getPosZ() - MathHelper.cos((float) yaw) * 0.00000001);
			}
		}

		if (mode.is("polar")) {
			boolean isInsideBlock = insideBlock();

			if (mc.player.isCollidedHorizontally && !isInsideBlock) {
				double yaw = MoveUtil.direction();
				mc.player.setPosition(mc.player.posX + -MathHelper.sin((float) yaw) * 0.05, mc.player.posY, mc.player.posZ + MathHelper.cos((float) yaw) * 0.05);
				MoveUtil.stop();
				MoveUtil.keybindStop();
			}

			if (Mouse.isButtonDown(mouseButton.getValue().intValue()) && isInsideBlock) {
				if (fast.getValue()) {
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
		}
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockBB = event -> {
		if (mode.is("polar")) {
			if (insideBlock()) {
				BlockPos playerPos = new BlockPos(mc.player);
				BlockPos blockPos = event.getBlockPos();
				if (blockPos.getY() > playerPos.getY())
					event.setBoundingBox(null);
			}
		}
	};

	private boolean insideBlock(final AxisAlignedBB bb) {
		final WorldClient world = mc.world;
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)))) != null && bb.intersectsWith(boundingBox)) {
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