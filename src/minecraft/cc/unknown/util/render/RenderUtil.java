package cc.unknown.util.render;

import java.awt.Color;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.component.impl.render.ProjectionComponent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.shader.Shaders;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public final class RenderUtil implements Accessor {

    private final Frustum FRUSTUM = new Frustum();
    public final int GENERIC_SCALE = 22;
    
    public void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public void rectangle(final double x, final double y, final double width, final double height, final Color color) {
        start();

        if (color != null) {
            ColorUtil.glColor(color);
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();

        stop();
    }

    public void renderAttack(EntityLivingBase target) {
        Sakura.instance.getEventBus().handle(new AttackEvent(target));

        if (!mc.player.isSwingInProgress || mc.player.swingProgressInt >= mc.player.getArmSwingAnimationEnd() / 2 || mc.player.swingProgressInt < 0) {
            mc.player.swingProgressInt = -1;
            mc.player.isSwingInProgress = true;
        }

        if (mc.player.fallDistance > 0) mc.player.onCriticalHit(target);
    }

    public void rainbowRectangle(final double x, final double y, final double width, final double height) {
        start();

        GL11.glBegin(GL11.GL_QUADS);

        for (double position = x; position <= x + width; position += 0.5) {
            color(Color.getHSBColor((float) ((position - x) / width), 1, 1));

            GL11.glVertex2d(position, y);
            GL11.glVertex2d(position + 0.5f, y);
            GL11.glVertex2d(position + 0.5f, y + height);
            GL11.glVertex2d(position, y + height);
        }

        GL11.glEnd();

        stop();
    }

    public void rectangle(final double x, final double y, final double width, final double height) {
        rectangle(x, y, width, height, null);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height, final Color color) {
        rectangle(x - width / 2, y - height / 2, width, height, color);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height) {
        rectangle(x - width / 2, y - height / 2, width, height, null);
    }

    public void verticalGradient(final double x, final double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        start();
        GlStateManager.alphaFunc(516, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(topColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);

        ColorUtil.glColor(bottomColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }
    
    public void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {

        start();
        if (color != null)
            color(color);
        GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
        	GL11.glVertex2d(firstX, firstY);
        	GL11.glVertex2d(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public void gradientCentered(double x, double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        x -= width / 2;
        y -= height / 2;
        verticalGradient(x, y, width, height, topColor, bottomColor);
    }

    public void horizontalGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        start();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(leftColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);

        ColorUtil.glColor(rightColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }

    public void horizontalCenteredGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        horizontalGradient(x - width / 2, y - height / 2, width, height, leftColor, rightColor);
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height, final Color color) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        color(color);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTexture(imageLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public void image(int textureID, final float x, final float y, final float width, final float height, final Color color) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        color(color);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTextureById(textureID);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height, Color color) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height, color);
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height) {
        image(imageLocation, x, y, width, height, Color.WHITE);
    }

    public void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height);
    }

    public void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                GL11.glVertex2d(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled,
                         final Color color) {
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled) {
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangle(final double x, final double y, final double sideLength, final Color color) {
        polygon(x, y, sideLength, 3, color);
    }

    public void triangle(final double x, final double y, final double sideLength) {
        polygon(x, y, sideLength, 3);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled,
                                 final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangleCentered(double x, double y, final double sideLength, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, color);
    }

    public void triangleCentered(double x, double y, final double sideLength) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3);
    }

    public void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical) {
        Shaders.RGQTest.draw(x, y, width, height, radius, firstColor, secondColor, vertical);
    }

    public void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        Shaders.RGQTest.draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor, secondColor, vertical, leftTop, rightTop, rightBottom, leftBottom);
    }

    public void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, Color thirdColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        Shaders.RTRIGQ.draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor, secondColor, thirdColor, vertical, leftTop, rightTop, rightBottom, leftBottom);
    }

    public void roundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        Shaders.RQ.draw((float) x, (float) y, (float) width, (float) height, (float) radius, color);
    }

    public void roundedRectangle(double x, double y, double width, double height, double radius, Color color, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        Shaders.RQ.draw((float) x, (float) y, (float) width, (float) height, (float) radius, color, leftTop, rightTop, rightBottom, leftBottom);
    }
    
    public void end() {
        GL11.glEnd();
    }

    public void circle(final double x, final double y, final double radius, final Color color) {
        roundedRectangle(x - radius, y - radius, radius * 2, radius * 2, radius, color);
    }

    public void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = mc.scaledResolution;
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public void drawLine(double x, double y, double z, double x1, double y1, double z1, final Color color, final float width) {
        x = x - mc.getRenderManager().renderPosX;
        x1 = x1 - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        y1 = y1 - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;
        z1 = z1 - mc.getRenderManager().renderPosZ;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(width);

        color(color);
        GL11.glBegin(2);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        color(Color.WHITE);
    }

    public boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        return framebuffer;
    }
    
    public void drawRoundedRect2(final double x, final double y, final double width, final double height, final double radius, final int color) {
        drawRoundedRect(x, y, width - x, height - y, radius, color);
    }
    
    public void drawRoundedRect(double x, double y, final double width, final double height, final double radius, final int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x2 = x + width;
        double y2 = y + height;
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        x2 *= 2.0;
        y2 *= 2.0;
        GL11.glDisable(3553);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0), y + radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0), y2 - radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
        }
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius, y2 - radius + Math.cos(i * Math.PI / 180.0) * radius);
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius, y + radius + Math.cos(i * Math.PI / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    
    public void drawSimpleItemBox(final Entity entity, final Color color) {
        GL11.glPushMatrix();

        final RenderManager renderManager = mc.getRenderManager();

        double x = entity.posX - renderManager.viewerPosX;
        double y = entity.posY - renderManager.viewerPosY + entity.height / 2.0D;
        double z = entity.posZ - renderManager.viewerPosZ;

        GL11.glTranslated(x, y, z);

        GL11.glRotated(-renderManager.playerViewY, 0.0D, 1.0D, 0.0D);
        GL11.glRotated(renderManager.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

        final float scale = .5f / 90f;
        GL11.glScalef(-scale, -scale, scale);

        final Color c = color;

        lineNoGl(-50, 0, 50, 0, c);
        lineNoGl(-50, -95, -50, 0, c);
        lineNoGl(-50, -95, 50, -95, c);
        lineNoGl(50, -95, 50, 0, c);

        GL11.glPopMatrix();
    }
    
    public void drawSimpleItemBox(final TileEntity tileEntity, final Color color) {
        BlockPos position = tileEntity.getPos();

        GL11.glPushMatrix();

        final RenderManager renderManager = mc.getRenderManager();

        double x = (position.getX() + 0.5) - renderManager.renderPosX;
        double y = position.getY() - renderManager.renderPosY;
        double z = (position.getZ() + 0.5) - renderManager.renderPosZ;

        GL11.glTranslated(x, y, z);

        GL11.glRotated(-renderManager.playerViewY, 0.0D, 1.0D, 0.0D);
        GL11.glRotated(renderManager.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

        final float scale = 1f / 100f;
        GL11.glScalef(-scale, -scale, scale);

        final Color c = color;

        lineNoGl(-50, 0, 50, 0, c);
        lineNoGl(-50, -95, -50, 0, c);
        lineNoGl(-50, -95, 50, -95, c);
        lineNoGl(50, -95, 50, 0, c);

        GL11.glPopMatrix();
    }
    
    public void drawSimpleLine(EntityPlayer player, float partialTicks, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        mc.entityRenderer.orientCamera(partialTicks);
        final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double y = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks) + 1.62F;
        final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        double renderX = mc.getRenderManager().renderPosX;
        double renderY = mc.getRenderManager().renderPosY;
        double renderZ = mc.getRenderManager().renderPosZ;
        drawLine(renderX, renderY + mc.player.getEyeHeight(), renderZ, x, y, z, color, 1.5F);
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
    
    public void drawSimpleBox(EntityPlayer player, int color, float partialTicks) {
    	double expand = 0.0D;
    	float a = (float) ((color >> 24) & 255) / 255.0F;
        float r = (float) ((color >> 16) & 255) / 255.0F;
        float g = (float) ((color >> 8) & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        
        double x = (player.lastTickPosX + ((player.posX - player.lastTickPosX) * (double) partialTicks)) - mc.getRenderManager().viewerPosX;
        double y = (player.lastTickPosY + ((player.posY - player.lastTickPosY) * (double) partialTicks)) - mc.getRenderManager().viewerPosY;
        double z = (player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * (double) partialTicks)) - mc.getRenderManager().viewerPosZ;
        
        AxisAlignedBB bbox = player.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand);
        AxisAlignedBB axis = new AxisAlignedBB((bbox.minX - player.posX) + x, (bbox.minY - player.posY) + y, (bbox.minZ - player.posZ) + z, (bbox.maxX - player.posX) + x, (bbox.maxY - player.posY) + y,  (bbox.maxZ - player.posZ) + z);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0F);
        GL11.glColor4f(r, g, b, a);

        RenderGlobal.drawSelectionBoundingBox(axis);

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
