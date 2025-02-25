package cc.unknown.module.impl.move;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Phase", description = "Transpasa bloques", category = Category.MOVEMENT)
public class Phase extends Module {

	private boolean phasing;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		phasing = false;
		final double rotation = Math.toRadians(mc.player.rotationYaw);
		final double x = Math.sin(rotation);
		final double z = Math.cos(rotation);

		if (mc.player.isCollidedHorizontally) {
			mc.player.setPosition(mc.player.posX - x * 0.005, mc.player.posY, mc.player.posZ + z * 0.005);
			phasing = true;
		} else if (PlayerUtil.insideBlock()) {
			PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX - x * 1.5, mc.player.posY, mc.player.posZ + z * 1.5, false));

			mc.player.motionX *= 0.3D;
			mc.player.motionZ *= 0.3D;

			phasing = true;
		}
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockBB = event -> {
		if (event.getBlock() instanceof BlockAir && phasing) {
			final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

			if (y < mc.player.posY) {
				event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
			}
		}
	};

}
