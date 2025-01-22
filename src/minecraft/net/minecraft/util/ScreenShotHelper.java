package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.src.Config;

public class ScreenShotHelper implements Runnable {
	private static final Logger logger = LogManager.getLogger();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static IntBuffer pixelBuffer;

	File gameDirectory;
	int width, height;
	Framebuffer buffer;
	private static int[] pixelValues;
	GuiIngame guiIngame;

	public ScreenShotHelper(final GuiIngame gui, final File gameDirectory, final int width, final int height, final Framebuffer buffer) {
		this.guiIngame = gui;
		this.gameDirectory = gameDirectory;
		this.width = width;
		this.height = height;
		this.buffer = buffer;
	}

	public static IChatComponent saveScreenshot(final File gameDirectory, final int width, final int height, final Framebuffer buffer) {
		return saveScreenshot(gameDirectory, (String) null, width, height, buffer);
	}

	public static IChatComponent saveScreenshot(final File gameDirectory, final String screenshotName, int width, int height, final Framebuffer buffer) {
		try {
			final File file1 = new File(gameDirectory, "screenshots");
			file1.mkdir();
			final Minecraft minecraft = Minecraft.getInstance();
			final int i = Config.getGameSettings().guiScale;
			final ScaledResolution scaledresolution = new ScaledResolution(minecraft);
			final int j = scaledresolution.getScaleFactor();
			final int k = Config.getScreenshotSize();
			final boolean flag = OpenGlHelper.isFramebufferEnabled() && k > 1;
			
			if (flag) {
				Config.getGameSettings().guiScale = j * k;
				resize(width * k, height * k);
				GlStateManager.pushMatrix();
				GlStateManager.clear(16640);
				minecraft.getFramebuffer().bindFramebuffer(true);
				minecraft.entityRenderer.updateCameraAndRender(Config.renderPartialTicks, System.nanoTime());
			}
			
			if (OpenGlHelper.isFramebufferEnabled()) {
				width = buffer.framebufferTextureWidth;
				height = buffer.framebufferTextureHeight;
			}
			
			final int l = width * height;
			if (pixelBuffer == null || pixelBuffer.capacity() < l) {
				pixelBuffer = BufferUtils.createIntBuffer(l);
				pixelValues = new int[l];
			}
			
			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			
			pixelBuffer.clear();
			
			if (OpenGlHelper.isFramebufferEnabled()) {
				GlStateManager.bindTexture(buffer.framebufferTexture);
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
			} else
				GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
			
			pixelBuffer.get(pixelValues);
			TextureUtil.processPixelValues(pixelValues, width, height);
			BufferedImage bufferedimage = null;
			
			if (OpenGlHelper.isFramebufferEnabled()) {
				bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
				final int i1 = buffer.framebufferTextureHeight - buffer.framebufferHeight;
				for (int j1 = i1; j1 < buffer.framebufferTextureHeight; ++j1)
					for (int k1 = 0; k1 < buffer.framebufferWidth; ++k1)
						bufferedimage.setRGB(k1, j1 - i1, pixelValues[j1 * buffer.framebufferTextureWidth + k1]);
			} else {
				bufferedimage = new BufferedImage(width, height, 1);
				bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
			}
			
			if (flag) {
				minecraft.getFramebuffer().unbindFramebuffer();
				Config.getGameSettings().guiScale = Config.getGameSettings().guiScale / k;
				resize(width / k, height / k);
				GlStateManager.popMatrix();
			}
			
			File file2;
			
			if (screenshotName == null)
				file2 = getTimestampedPNGFileForDirectory(file1);
			else
				file2 = new File(file1, screenshotName);
			
			file2 = file2.getCanonicalFile();
			ImageIO.write(bufferedimage, "png", file2);
			final IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
			ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
			ichatcomponent.getChatStyle().setUnderlined(true);
			return new ChatComponentTranslation("screenshot.success", ichatcomponent);
		} catch (final Exception exception) {
			logger.warn("Couldn\'t save screenshot", exception);
			return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
		}
	}

	private static File getTimestampedPNGFileForDirectory(final File gameDirectory) {
		final String s = dateFormat.format(new Date()).toString();
		int i = 1;
		while (true) {
			final File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
			if (!file1.exists())
				return file1;
			++i;
		}
	}

	private static void resize(final int p_resize_0_, final int p_resize_1_) {
		final Minecraft mc = Minecraft.getInstance();
		mc.displayWidth = Math.max(1, p_resize_0_);
		mc.displayHeight = Math.max(1, p_resize_1_);
		
		if (mc.currentScreen != null) {
			final ScaledResolution scaledresolution = new ScaledResolution(mc);
			mc.currentScreen.onResize(mc, scaledresolution.getScaledWidth(),
					scaledresolution.getScaledHeight());
		}
		updateFramebufferSize();
	}

	private static void updateFramebufferSize() {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
		if (minecraft.entityRenderer != null)
			minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
	}

	@Override
	public void run() {
		this.guiIngame.getChatGUI().printChatMessage(saveScreenshot(gameDirectory, width, height, buffer));
	}
}