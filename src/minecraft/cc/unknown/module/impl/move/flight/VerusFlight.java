package cc.unknown.module.impl.move.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.MoveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.move.Flight;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class VerusFlight extends Mode<Flight> {

	private int ticks = 0;

	public VerusFlight(String name, Flight parent) {
		super(name, parent);
	}

	@Override
	public void onDisable() {
		MoveUtil.stop();
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.player.motionY = 0.42F;
			}
		}

		++ticks;
	};

	@EventLink
	public final Listener<MoveEvent> onMove = event -> {
		if (mc.player.onGround && ticks % 14 == 0) {
			event.setPosY(0.42F);
			MoveUtil.strafe(0.69);
			mc.player.motionY = -(mc.player.posY - Math.floor(mc.player.posY));
		} else {
			if (mc.player.onGround) {
				MoveUtil.strafe(1.01 + MoveUtil.speedPotionAmp(0.15));
			} else
				MoveUtil.strafe(0.41 + MoveUtil.speedPotionAmp(0.05));
		}

		mc.player.setSprinting(true);
		mc.player.omniSprint = true;

		ticks++;
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (event.getBlock() instanceof BlockAir && !mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindJump.isKeyDown()) {
			final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

			if (y < mc.player.posY) {
				event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
			}
		}

	};

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> event.setSneak(false);
}