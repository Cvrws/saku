package cc.unknown.module.impl.movement;

import java.awt.Color;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.PushOutOfBlockEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "No Clip", description = "Permite atravesar bloques normalmente sólidos", category = Category.MOVEMENT)
public class NoClip extends Module {

	private int lastSlot;
	
	@Override
	public void onEnable() {
		lastSlot = -1;
	}

	@Override
	public void onDisable() {
		mc.player.noClip = false;
    	mc.player.inventory.currentItem = lastSlot;
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

		if (getModule(Scaffold.class).isEnabled() || (getModule(KillAura.class).isEnabled() && getModule(KillAura.class).target != null)) return;

        final int slot = SlotUtil.findBlock();
        
        if (slot == -1 || PlayerUtil.insideBlock()) {
            return;
        }
        
        mc.player.inventory.currentItem = slot;

        RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw, 90), 2, MovementFix.SILENT);

        if (RotationComponent.rotations.y >= 89 && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.player.posY == mc.objectMouseOver.getBlockPos().up().getY()) {

            mc.playerController.onPlayerRightClick(mc.player, mc.theWorld, PlayerUtil.getItemStack(),  mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);

            mc.player.swingItem();
        }
	};
	
	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
        final ScaledResolution scaledResolution = /*mc.scaledResolution*/ new ScaledResolution(mc);

        final String name = "Una ves activado presiona shift";
        mc.fontRendererObj.drawCentered(name, scaledResolution.getScaledWidth() / 2F, scaledResolution.getScaledHeight() - 89.5F, new Color(0, 0, 0, 200).hashCode());
        mc.fontRendererObj.drawCentered(name, scaledResolution.getScaledWidth() / 2F, scaledResolution.getScaledHeight() - 90, getTheme().getAccentColor().getRGB());
	};
}