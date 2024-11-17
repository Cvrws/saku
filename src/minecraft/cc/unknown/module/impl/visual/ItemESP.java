package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

@ModuleInfo(aliases = "Item ESP", description = "Renders all items", category = Category.VISUALS)
public final class ItemESP extends Module {

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {        
        for (Entity item : mc.world.loadedEntityList) {
            if (item instanceof EntityItem) {
            	RenderUtil.drawSimpleItemBox(item, getTheme().getAccentColor());
            }
        }
	};
}