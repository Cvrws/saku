package cc.unknown.util.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.interfaces.Shaders;
import cc.unknown.util.render.shader.bloom.GaussianFilter;
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
import net.minecraft.client.renderer.texture.TextureUtil;
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
	private final Map<Integer, Integer> shadowCache = new HashMap<>();
	public final int GENERIC_SCALE = 22;
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;
    
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
	
    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
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

		if (!mc.player.isSwingInProgress || mc.player.swingProgressInt >= mc.player.getArmSwingAnimationEnd() / 2
				|| mc.player.swingProgressInt < 0) {
			mc.player.swingProgressInt = -1;
			mc.player.isSwingInProgress = true;
		}

		if (mc.player.fallDistance > 0)
			mc.player.onCriticalHit(target);
	}

	public void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY,
			final Color color) {

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

	public void image(final ResourceLocation imageLocation, final int x, final int y, final int width,
			final int height) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		color(Color.WHITE);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		mc.getTextureManager().bindTexture(imageLocation);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		GlStateManager.resetColor();
		GlStateManager.disableBlend();
	}

	public void color(Color color) {
		if (color == null)
			color = Color.white;
		GL11.glColor4d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
	}

	public void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius,
			Color firstColor, Color secondColor, Color thirdColor) {
		Shaders.RTRIGQ.draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor,
				secondColor, thirdColor, false, true, true, false, false);
	}

	public void roundedRectangle(double x, double y, double width, double height, double radius, Color color) {
		Shaders.RQ.draw((float) x, (float) y, (float) width, (float) height, (float) radius, color);
	}

	public void end() {
		GL11.glEnd();
	}

	public void circle(final double x, final double y, final double radius, final Color color) {
		roundedRectangle(x - radius, y - radius, radius * 2, radius * 2, radius, color);
	}

	public void scissor(double x, double y, double width, double height) {
		final ScaledResolution sr = new ScaledResolution(mc);
		final double scale = sr.getScaleFactor();

		y = sr.getScaledHeight() - y;

		x *= scale;
		y *= scale;
		width *= scale;
		height *= scale;

		GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
	}

	public void drawLine(double x, double y, double z, double x1, double y1, double z1, final Color color,
			final float width) {
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

	public void roundedRect(final double x, final double y, final double width, final double height,
			final double radius, final int color) {
		drawRoundedRect(x, y, width - x, height - y, radius, color);
	}

	private void drawRoundedRect(double x, double y, final double width, final double height, final double radius,
			final int color) {
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
			GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0),
					y + radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
		}
		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0),
					y2 - radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
		}
		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius,
					y2 - radius + Math.cos(i * Math.PI / 180.0) * radius);
		}
		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius,
					y + radius + Math.cos(i * Math.PI / 180.0) * radius);
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

	public void drawChestBox(final TileEntity tileEntity, final Color color) {
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
		float alpha = (float) ((color >> 24) & 255) / 255.0F;
		float red = (float) ((color >> 16) & 255) / 255.0F;
		float green = (float) ((color >> 8) & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;

		double x = (player.lastTickPosX + ((player.posX - player.lastTickPosX) * (double) partialTicks))
				- mc.getRenderManager().viewerPosX;
		double y = (player.lastTickPosY + ((player.posY - player.lastTickPosY) * (double) partialTicks))
				- mc.getRenderManager().viewerPosY;
		double z = (player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * (double) partialTicks))
				- mc.getRenderManager().viewerPosZ;

		AxisAlignedBB bbox = player.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand);
		AxisAlignedBB axis = new AxisAlignedBB((bbox.minX - player.posX) + x, (bbox.minY - player.posY) + y,
				(bbox.minZ - player.posZ) + z, (bbox.maxX - player.posX) + x, (bbox.maxY - player.posY) + y,
				(bbox.maxZ - player.posZ) + z);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glLineWidth(1.0F);
		GL11.glColor4f(red, green, blue, alpha);

		RenderGlobal.drawSelectionBoundingBox(axis);

		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public void drawShadedString(final String text, final int x, final int y, final int color) {
		final String unformattedText = text.replaceAll("(?i)§[\\da-f]", "");
		mc.fontRendererObj.draw(unformattedText, x + 1, y, 0);
		mc.fontRendererObj.draw(unformattedText, x - 1, y, 0);
		mc.fontRendererObj.draw(unformattedText, x, y + 1, 0);
		mc.fontRendererObj.draw(unformattedText, x, y - 1, 0);
		mc.fontRendererObj.draw(text, x, y, color);
	}

	public void renderHitbox(AxisAlignedBB bb, int type) {
		double[][] vertices = {
				{ bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.minZ, bb.minX, bb.minY,
						bb.minZ },
				{ bb.minX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.minZ, bb.minX, bb.maxY,
						bb.minZ },
				{ bb.minX, bb.minY, bb.minZ, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.maxY,
						bb.minZ },
				{ bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY,
						bb.minZ },
				{ bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, bb.minX, bb.maxY,
						bb.minZ },
				{ bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, bb.minX, bb.maxY,
						bb.maxZ } };

		for (double[] face : vertices) {
			GL11.glBegin(type);
			for (int i = 0; i < face.length; i += 3) {
				GL11.glVertex3d(face[i], face[i + 1], face[i + 2]);
			}
			GL11.glEnd();
		}
	}

	public void drawOutline(float x, float y, float x2, float y2, float lineWidth, int color) {
		float f5 = (float) ((color >> 24) & 255) / 255.0F;
		float f6 = (float) ((color >> 16) & 255) / 255.0F;
		float f7 = (float) ((color >> 8) & 255) / 255.0F;
		float f8 = (float) (color & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glPushMatrix();
		GL11.glColor4f(f6, f7, f8, f5);
		GL11.glLineWidth(lineWidth);
		GL11.glBegin(1);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y2);
		GL11.glVertex2d(x2, y2);
		GL11.glVertex2d(x2, y);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x2, y);
		GL11.glVertex2d(x, y2);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	public void drawRect(double left, double top, double right, double bottom, int color) {
		double j;
		if (left < right) {
			j = left;
			left = right;
			right = j;
		}

		if (top < bottom) {
			j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0).endVertex();
		worldrenderer.pos(right, bottom, 0.0).endVertex();
		worldrenderer.pos(right, top, 0.0).endVertex();
		worldrenderer.pos(left, top, 0.0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, Color color) {
		drawBloomShadow(x, y, width, height, blurRadius, 0, color);
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, int roundRadius,
			Color color) {
		width = width + blurRadius * 2;
		height = height + blurRadius * 2;
		x -= blurRadius + 0.75f;
		y -= blurRadius + 0.75f;

		int identifier = Arrays.deepHashCode(new Object[] { width, height, blurRadius, roundRadius });
		if (!shadowCache.containsKey(identifier)) {
			if (width <= 0)
				width = 1;
			if (height <= 0)
				height = 1;
			BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = original.getGraphics();
			g.setColor(new Color(-1));
			g.fillRoundRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2),
					roundRadius, roundRadius);
			g.dispose();
			GaussianFilter op = new GaussianFilter(blurRadius);
			BufferedImage blurred = op.filter(original, null);
			shadowCache.put(identifier,
					TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false));
		}
		drawImage(shadowCache.get(identifier), x, y, width, height, color.getRGB());
	}

	public void drawImage(int image, float x, float y, float width, float height, int color) {
		GL11.glPushMatrix();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(image);

		ColorUtil.glColor(color);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);

		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + height);

		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + width, y + height);

		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + width, y);
		GL11.glEnd();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.resetColor();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	public void drawSelectionBoundingBox(final AxisAlignedBB bb, int color) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GL11.glDisable(2929);
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();
		drawFilledBoundingBox(bb, new Color(color, true));

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GL11.glEnable(2929);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void drawFilledBoundingBox(final AxisAlignedBB bb, Color color) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
	}

	public void renderBox(int x, int y, int z, double x2, double y2, double z2, int color, boolean outline,
			boolean shade) {
		double xPos = x - mc.getRenderManager().viewerPosX;
		double yPos = y - mc.getRenderManager().viewerPosY;
		double zPos = z - mc.getRenderManager().viewerPosZ;
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
		drawAxisAlignedBB(axisAlignedBB, shade, outline, color);
	}

	public void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean outline, int color) {
		drawAxisAlignedBB(axisAlignedBB, outline, true, color);
	}

	public void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean filled, boolean outline, int color) {
		drawSelectionBoundingBox(axisAlignedBB, outline, filled, color);
	}

	public void drawSelectionBoundingBox(final AxisAlignedBB bb, final boolean outline, final boolean filled,
			int color) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GL11.glDisable(2929);
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();

		if (outline) {
			drawOutlineBoundingBox(bb, new Color(color, true));
		}

		if (filled) {
			drawFilledBoundingBox(bb, new Color(color, true));
		}

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GL11.glEnable(2929);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void drawOutlineBoundingBox(final AxisAlignedBB bb, Color color) {
		RenderGlobal.func_181563_a(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
    public void drawTriangle(float cx, float cy, float r, float n, Color color){
        GL11.glPushMatrix();
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        r *= 2.0;
        double x = r;
        double y = 0.0;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GlStateManager.color(0,0,0);
        GlStateManager.resetColor();
        ColorUtil.glColor(color);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < n) {
            GL11.glVertex2d(x + cx, y + cy);
            double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
    
    public Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }
    
    public void bindTexture(int texture) {
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }
    
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float) (limit * .01));
    }
}
