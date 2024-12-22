package cc.unknown.util.render.blur;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class BlurUtil implements Accessor {
    private ShaderGroup blurShader;
    private Framebuffer buffer;
    private int lastScale;
    private int lastScaleWidth;
    private int lastScaleHeight;
    private ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");
    private StopWatch fbTimer = new StopWatch();
    private int fbDelay = 1000;

    public void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            buffer = blurShader.getFbos().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
        blurShader.getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        blurShader.getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(intensity);

        blurShader.getShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        blurShader.getShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }
    
    public void rectBlurry(float x, float y, float x1, float y1){
        RenderUtil.drawRect(
                (int)x,
                (int)y,
                (int)x1,
                (int)y1, ColorUtil.getColor(65, 150));
        blurArea(
                (int)x,
                (int)y,
                (int)x1 - (int)x,
                (int)y1 - (int)y,
                20, 20, 20);
    }

    public void blurArea(int x, int y, int width, int height, float intensity, float blurWidth,
                                float blurHeight) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        if (OpenGlHelper.isFramebufferEnabled()) {

            if(fbTimer.reached(fbDelay))buffer.framebufferClear();

            MultipleGLScissor scissor = new MultipleGLScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
                    (height) * factor);

            setShaderConfigs(intensity, blurWidth, blurHeight);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);

            mc.getFramebuffer().bindFramebuffer(true);

            scissor.destroy();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO,
                    GL11.GL_ONE);
            buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef(factor, factor, 0);

        }
    }

    public void blurArea(int x, int y, int width, int height, float intensity) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        if(fbTimer.reached(fbDelay))buffer.framebufferClear();

        MultipleGLScissor scissor = new MultipleGLScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
                (height) * factor);

        setShaderConfigs(intensity, 1, 0);
        buffer.bindFramebuffer(true);
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        scissor.destroy();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
        GlStateManager.disableBlend();
        GL11.glScalef(factor, factor, 0);
    }

    public void blurAreaBoarder(int x, int y, int width, int height, float intensity, float blurWidth,
                                       float blurHeight) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        MultipleGLScissor scissor = new MultipleGLScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
                (height) * factor);

        setShaderConfigs(intensity, blurWidth, blurHeight);
        buffer.bindFramebuffer(true);
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        scissor.destroy();
    }

    public void blurAreaBoarder(int x, int y, int width, int height, float intensity) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        MultipleGLScissor scissor = new MultipleGLScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
                (height) * factor);
        setShaderConfigs(intensity, 1, 0);
        buffer.bindFramebuffer(true);
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        scissor.destroy();
    }
}