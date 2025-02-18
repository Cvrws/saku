package cc.unknown.module.impl.move.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.move.Flight;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockFlight extends Mode<Flight> {

	public BlockFlight(String name, Flight parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		mc.player.inventory.currentItem = InventoryUtil.findBlock();
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.blockNear(3) && mc.player.ticksSinceVelocity > 15) {
			if (Math.abs(MoveUtil.predictedMotion(0.42f) - mc.player.motionY) < 0.0001) {
				event.setOnGround(true);
			} else {
				mc.player.motionY = 0.42f;
			}

			mc.player.motionY = 0.42f;

		}

		if (PlayerUtil.getItemStack() != null && PlayerUtil.getItemStack().getItem() instanceof ItemBlock) {
			if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
				PacketUtil.send(new C0APacketAnimation());

				mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getCurrentEquippedItem(),
						new BlockPos(mc.player.posX, Math.floor(mc.player.posY) - 1, mc.player.posZ), EnumFacing.UP,
						new Vec3(mc.player.posX, Math.floor(mc.player.posY) - 1, mc.player.posZ));
			}
		}
	};
}