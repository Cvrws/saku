package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@ModuleInfo(aliases = "Chest ESP", description = "Renderiza todos tipo de cofre", category = Category.VISUALS)
public final class ChestESP extends Module {

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
			if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
				RenderUtil.drawSimpleItemBox(tileEntity, getTheme().getAccentColor());
			}
		}
	};
}