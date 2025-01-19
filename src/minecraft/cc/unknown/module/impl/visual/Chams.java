package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.PostRenderLivingEntityEvent;
import cc.unknown.event.impl.render.PreRenderLivingEntityEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Chams", description = "Renderiza a los jugadores detras de paredes", category = Category.VISUALS)
public final class Chams extends Module {
	
	@EventLink
	public final Listener<PreRenderLivingEntityEvent> onPreRenderLiving = event -> {
		render(1);
	};
	
    @EventLink
    public final Listener<PostRenderLivingEntityEvent> onPostRenderLiving = event -> {
    	render(2);
    };
    
    private void render(int pre) {
		for (EntityPlayer player : mc.world.playerEntities) {
			if (player == mc.player || player.isDead || player == null) {
				continue;
			}
			
			switch (pre) {
			case 1:
	            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
	            GL11.glPolygonOffset(1.0F, -1000000F);
	            break;
			case 2:
	            GL11.glPolygonOffset(1.0F, 1000000F);
	            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			}
		}
    }
}
