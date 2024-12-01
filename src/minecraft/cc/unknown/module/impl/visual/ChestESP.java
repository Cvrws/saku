package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@ModuleInfo(aliases = "Chest ESP", description = "Renderiza todos tipo de cofre", category = Category.VISUALS)
public final class ChestESP extends Module {
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
			int color = 0;
			
			if (tileEntity instanceof TileEntityChest) {
				color = new Color(255, 255, 0).getRGB();
				RenderUtil.drawSimpleItemBox(tileEntity, new Color(color));
			} else if (tileEntity instanceof TileEntityEnderChest) {
				color = new Color(128, 0, 128).getRGB();
				RenderUtil.drawSimpleItemBox(tileEntity, new Color(color));
			}
		}
	};
}