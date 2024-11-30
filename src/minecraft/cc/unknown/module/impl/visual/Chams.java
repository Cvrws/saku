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

@ModuleInfo(aliases = "Chams", description = "Renderiza a los jugadores detras de bloques o paredes", category = Category.VISUALS)
public final class Chams extends Module {
	
	@EventLink
	public final Listener<PreRenderLivingEntityEvent> onPreRenderLiving = event -> {
        if (event.getEntity() instanceof EntityPlayer && event.getEntity() != mc.player) {
        	GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        	GL11.glPolygonOffset(1.0F, -2000000.0F);
        }
	};
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
		for (EntityPlayer player : mc.world.playerEntities) {
			if (player == mc.player || player.isDead || player == null) {
				continue;
			}
			
			final float partialTicks = mc.timer.renderPartialTicks;
			final double renderPosX = mc.getRenderManager().renderPosX;
			final double renderPosY = mc.getRenderManager().renderPosY;
			final double renderPosZ = mc.getRenderManager().renderPosZ;
			final Render<EntityPlayer> render = mc.getRenderManager().getEntityRenderObject(player);
			if (render == null) continue;
	
			Color color = new Color(0);
			if (color.getAlpha() <= 0) continue;
	
			double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - renderPosX;
			double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks - renderPosY;
			double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - renderPosZ;
			float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
	

			GL11.glColor4f(1.0f, 1.0f, 1.0f, color.getAlpha());

			render.doRender(player, x, y, z, yaw, partialTicks);
	
			player.hide();
		}
	
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();
    };
	
    @EventLink
    public final Listener<PostRenderLivingEntityEvent> onPostRenderLiving = event -> {
    	if (event.getEntity() instanceof EntityPlayer) {
    		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    		GL11.glPolygonOffset(1.0F, -2000000.0F);
    		RenderHelper.disableStandardItemLighting();
    		mc.entityRenderer.disableLightmap();
        }
    };
}
