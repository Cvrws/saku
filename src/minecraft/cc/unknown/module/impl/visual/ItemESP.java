package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Item ESP", description = "Renderiza todos los items", category = Category.VISUALS)
public final class ItemESP extends Module {

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {        
	    Map<Item, Color> colors = new HashMap<>();
	    colors.put(Items.gold_ingot, Color.YELLOW);
	    colors.put(Items.iron_ingot, Color.WHITE);
	    colors.put(Items.emerald, Color.GREEN);
	    colors.put(Items.redstone, Color.RED);
	    colors.put(Items.diamond, Color.CYAN);

	    for (Entity entity : mc.world.loadedEntityList) {
	        if (entity instanceof EntityItem) {
	            EntityItem item = (EntityItem) entity;
	            ItemStack stack = item.getEntityItem();

	            Color color = colors.getOrDefault(stack.getItem(), getTheme().getAccentColor());
	            
	            RenderUtil.drawSimpleItemBox(item, color);
	        }
	    }
	};
}