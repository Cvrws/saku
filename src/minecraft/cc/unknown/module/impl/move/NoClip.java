package cc.unknown.module.impl.move;

import java.awt.Color;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.PushOutOfBlockEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "No Clip", description = "Permite atravesar bloques normalmente sólidos", category = Category.MOVEMENT)
public class NoClip extends Module {

	private int lastSlot;

	private final BooleanValue noSwing = new BooleanValue("No Swing", this, false);
	private final BooleanValue spoof = new BooleanValue("Spoof Slot", this, true);

	@Override
	public void onEnable() {
		lastSlot = -1;
	}

	@Override
	public void onDisable() {
		mc.player.noClip = false;
		mc.player.inventory.currentItem = lastSlot;
		SpoofHandler.stopSpoofing();
	}

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (PlayerUtil.insideBlock()) {
			event.setBoundingBox(null);

			if (!(event.getBlock() instanceof BlockAir) && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				final double 
				x = event.getBlockPos().getX(), 
				y = event.getBlockPos().getY(),		
				z = event.getBlockPos().getZ();

				if (y < mc.player.posY) {
					event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
				}
			}
		}
	};

	@EventLink
	public final Listener<PushOutOfBlockEvent> onPushOutOfBlock = CancellableEvent::setCancelled;

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (lastSlot == -1) {
			lastSlot = mc.player.inventory.currentItem;
		}

		mc.player.noClip = true;

		if (getModule(Scaffold.class).isEnabled() || (getModule(KillAura.class).isEnabled() && getModule(KillAura.class).target != null))
			return;

		final int slot = SlotUtil.findBlock();

		if (slot == -1 || PlayerUtil.insideBlock()) {
			return;
		}

		mc.player.inventory.currentItem = slot;
		if (spoof.getValue()) SpoofHandler.startSpoofing(lastSlot);

		RotationHandler.setRotations(new Vector2f(mc.player.rotationYaw, 90), 2, MoveFix.SILENT);

		if (RotationHandler.rotations.y >= 89
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& mc.player.posY == mc.objectMouseOver.getBlockPos().up().getY()) {

			mc.playerController.onPlayerRightClick(mc.player, mc.theWorld, PlayerUtil.getItemStack(),
					mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);

			if (noSwing.getValue()) {
				PacketUtil.send(new C0APacketAnimation());
			} else {
				mc.player.swingItem();
			}
		}
	};

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		final ScaledResolution scaledResolution = new ScaledResolution(mc);

		final String name = "Una ves activado presiona shift";
		mc.fontRendererObj.drawCentered(name, scaledResolution.getScaledWidth() / 2F,
				scaledResolution.getScaledHeight() - 89.5F, new Color(0, 0, 0, 200).hashCode());
		mc.fontRendererObj.drawCentered(name, scaledResolution.getScaledWidth() / 2F,
				scaledResolution.getScaledHeight() - 90, getTheme().getAccentColor().getRGB());
	};
}