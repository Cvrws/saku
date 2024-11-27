package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Name Tags", description = "Renderiza el nombre de los jugadores", category = Category.VISUALS)
public final class NameTags extends Module {

	@EventLink
	public final Listener<RenderLabelEvent> onRender2D = event -> {
        if (event.getTarget() instanceof EntityPlayer && event.getTarget() != mc.player && ((EntityPlayer)event.getTarget()).deathTime == 0) {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            String name = player.getDisplayName().getFormattedText();
            
            event.setCancelled();
            
            renderNewTag(event, player, name);
        }
	};
	
	private void renderNewTag(RenderLabelEvent event, EntityPlayer player, String name) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) event.getX() + 0.0F, (float) event.getY() + player.height + 0.5F, (float) event.getZ());
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        float scale = 0.02666667F;
        GlStateManager.scale(-scale, -scale, scale);
        if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 9.374999F, 0.0F);
        }
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.fontRendererObj.drawWithShadow(name, -mc.fontRendererObj.width(name) / 2, 0, -1);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
	}
}