package cc.unknown.util.render.bluearchive;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

@UtilityClass
public class HaloRenderer implements Accessor {
	
	public void drawArisHalo(Render3DEvent event) {
	    float height = mc.player.height + 0.25f;

	    GL11.glPushMatrix();
	    glTranslated(event, height);
	    setupOpenGL();

	    float yaw = mc.player.rotationYaw;

	    GL11.glRotatef(-yaw, 0F, 1F, 0F);
	    GL11.glRotatef(90, 1F, 0F, 0F);

	    RenderUtil.drawHaloRectangles();

	    resetOpenGL();
	    GL11.glPopMatrix();
	}

	public void drawShirokoHalo(Render3DEvent event) {
        float height = mc.player.height + 0.25f;

        GL11.glPushMatrix();
        glTranslated(event, height);
        setupOpenGL();

        RenderUtil.color(new Color(
            MathHelper.clamp_int(2000 * 1800, 0, 255),
            MathHelper.clamp_int(230 + 2000 * 200, 0, 255),
            250, 220
        ));

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.drawCircle(0.18f, 2.5f);

        GL11.glTranslated(0.0f, 0.0f, -0.02f);
        RenderUtil.drawDecorativeCircle(0.3f, 3.7f);

        resetOpenGL();
        GL11.glPopMatrix();
    }
    
    public void drawReisaHalo(Render3DEvent event) {
        float height = mc.player.height + 0.25f;

        GL11.glPushMatrix();
        glTranslated(event, height);
        setupOpenGL();

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.color(new Color(200, 200, 250, 220));
        GL11.glLineWidth(3.0f * RenderUtil.getExtraWidth());
        RenderUtil.drawStar(0.0f, 0.0f, 0.3f, 0);

        GL11.glPushMatrix();
        GL11.glRotatef(36, 0F, 0F, 1F);
        RenderUtil.drawStar(0.0f, 0.0f, 0.14f, 0);
        GL11.glPopMatrix();

        resetOpenGL();
        GL11.glPopMatrix();
    }

    public void drawNatsuHalo(Render3DEvent event) {
        float height = mc.player.height + 0.25f;

        GL11.glPushMatrix();
        glTranslated(event, height);
        setupOpenGL();

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.color(new Color(254, 200, 200, 240));

        RenderUtil.drawCircle(0.3f, 3.5f);
        RenderUtil.drawCircleWithOffsets(0.15f, 0.05f, 0.05f, 3.5f);

        resetOpenGL();
        GL11.glPopMatrix();
    }
    
    public void drawHoshinoHalo(Render3DEvent event) {
        float height = mc.player.height + 0.25f;
        float extraHeight = 0.035f;
        float extensionLength = 0.18f;
        float smallExtensionLength = 0.08f;

        GL11.glPushMatrix();
        glTranslated(event, height);
        setupOpenGL();

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.drawCircle(0.13f, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
        RenderUtil.drawCircle(0.20f, 2.5f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
        GL11.glTranslated(0.0f, 0.0f, -extraHeight);

        RenderUtil.drawArc(0.27f, 15, 165, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
        RenderUtil.drawArc(0.27f, 195, 345, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));

        RenderUtil.drawLineExtensions(0.27f, extensionLength, new int[]{0, 180});

        RenderUtil.drawLineExtensions(0.27f, smallExtensionLength, new int[]{15, 165, 195, 345});

        resetOpenGL();
        GL11.glPopMatrix();
    }

    private void setupOpenGL() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void resetOpenGL() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
    }
    
    private void glTranslated(Render3DEvent event, float height) {
        GL11.glTranslated(mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX, mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height, mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ);
    }
}
