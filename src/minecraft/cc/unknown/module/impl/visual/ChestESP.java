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
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@ModuleInfo(aliases = "Chest ESP", description = "Renderiza todos tipo de cofre", category = Category.VISUALS)
public final class ChestESP extends Module {
		
	public final BooleanValue outline = new BooleanValue("Outline", this, true);
	public final BooleanValue filled = new BooleanValue("Filled", this, false);
	private int color = 0;

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
	    for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
	        if (tileEntity.isInvalid()) continue;

	        Block block = mc.world.getBlockState(tileEntity.getPos()).getBlock();
	        if (block == null) continue;
	        
	        if (tileEntity instanceof TileEntityChest) {
	            color = new Color(255, 255, 0).getRGB();
	        } else if (tileEntity instanceof TileEntityEnderChest) {
	            color = new Color(128, 0, 128).getRGB();
	        } else {
	            continue;
	        }

	        RenderUtil.renderBlock(tileEntity.getPos(), color, outline, filled);
	    }
	};

}