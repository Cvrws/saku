package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Pointer ESP", description = "Señala a tus objectivos", category = Category.VISUALS)
public final class PointerESP extends Module {

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int size = 100;
        double xOffset = scaledResolution.getScaledWidth() / 2F - 50.2;
        double yOffset = scaledResolution.getScaledHeight() / 2F - 49.5;
        double playerOffsetX = mc.player.posX;
        double playerOffSetZ = mc.player.posZ;

        for (Entity entity : mc.world.loadedEntityList) {
            if(entity instanceof EntityPlayer && entity != mc.player) {
                double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks()) - playerOffsetX) * 0.2);
                double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks()) - playerOffSetZ) * 0.2);
                double cos = Math.cos(mc.player.rotationYaw * (Math.PI * 2 / 360));
                double sin = Math.sin(mc.player.rotationYaw * (Math.PI * 2 / 360));
                double rotY = -(pos2 * cos - pos1 * sin);
                double rotX = -(pos1 * cos + pos2 * sin);
                double var7 = -rotX;
                double var9 = -rotY;
                Color color = getTheme().getAccentColor();
                if(MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2F - 4) {
                    double angle = (Math.atan2(rotY, rotX) * 180 / Math.PI);
                    double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                    double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                    GL11.glPushMatrix();
                    GL11.glTranslated(x,y,0);
                    GL11.glRotatef((float) angle, 0, 0, 1);
                    GL11.glScaled(1.5, 1, 1);
                    RenderUtil.drawTriangle(0F, 0F, 2.2F, 3F, color);
                    RenderUtil.drawTriangle(0F, 0F, 1.5F, 3F, color);
                    RenderUtil.drawTriangle(0F, 0F, 1.0F, 3F, color);
                    RenderUtil.drawTriangle(0F, 0F, 0.5F, 3F, color);
                    RenderUtil.drawTriangle(0F, 0F, 2.2F, 3F, color);
                    GL11.glPopMatrix();
                }
            }
        }
	};
}
