package cc.unknown.module.impl.visual;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.RenderItemEvent;
import cc.unknown.event.impl.render.SwingAnimationEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Animations", description = "Ajusta las animaciones de los items", category = Category.VISUALS)
public final class Animations extends Module {

    private final ModeValue blockAnimation = new ModeValue("Block Animation", this)
            .add(new SubMode("None"))
            .add(new SubMode("1.7"))
            .add(new SubMode("Sunny"))
            .add(new SubMode("Lucid"))
            .add(new SubMode("Astro"))
            .add(new SubMode("Smooth"))
            .add(new SubMode("Spin"))
            .add(new SubMode("Swong"))
            .add(new SubMode("Stella"))
            .add(new SubMode("Flup"))
            .add(new SubMode("Noov"))
            .add(new SubMode("Komorebi"))
            .add(new SubMode("Rhys"))
            .add(new SubMode("Swing"))
            .add(new SubMode("?"))
            .add(new SubMode("Stab"))
            .add(new SubMode("Tap"))
            .setDefault("1.7");

    private final BooleanValue onlyWhenBlocking = new BooleanValue("Update Position Only When Blocking", this, true);
    public final NumberValue swingSpeed = new NumberValue("Swing Speed", this, 1, -200, 50, 1);

    private final NumberValue x = new NumberValue("X", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final NumberValue y = new NumberValue("Y", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final NumberValue z = new NumberValue("Z", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final NumberValue scale = new NumberValue("Scale", this, 1, 0.1, 2, 0.1);
    private final BooleanValue alwaysShow = new BooleanValue("Always Show", this, false);

    @EventLink
    public final Listener<RenderItemEvent> onRenderItem = event -> {
        if (event.getItemToRender().getItem() instanceof ItemMap) {
            return;
        }

        if (!onlyWhenBlocking.getValue())

            GlStateManager.translate(x.getValue().floatValue(), y.getValue().floatValue(), z.getValue().floatValue());

        double var7 = 0;

        Number scaleValue = scale.getValue();
        double scaleDouble = scaleValue.doubleValue();
        var7 = scaleDouble;

        final EnumAction itemAction = event.getEnumAction();
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final float animationProgression = alwaysShow.getValue() && event.isUseItem() ? 0.0F : event.getAnimationProgression();
        final float swingProgress = event.getSwingProgress();
        final float convertedProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2);

        if (event.isUseItem() && itemAction == EnumAction.BLOCK) {

            if (onlyWhenBlocking.getValue())
                GlStateManager.translate(x.getValue().floatValue(), y.getValue().floatValue(), z.getValue().floatValue());

            switch (blockAnimation.getValue().getName()) {

                case "None": {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "1.7": {

                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Sunny": {
                    GlStateManager.translate(.05f, -.05f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression + 0.15f, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();

                    GlStateManager.translate(-0.5f, 0.2f, 0.0f);

                    break;
                }

                case "Lucid": {
                    itemRenderer.transformFirstPersonItem(animationProgression - 0.1F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Astro": {
                    GlStateManager.translate(.0f, .03f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F / 2.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress / 2.0F, -0.0F);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Tap":
                    GL11.glTranslatef(0, 0.3f, 0);
                    float smooth = (swingProgress * 0.8f - (swingProgress * swingProgress) * 0.8f);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(smooth * -90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.37F, 0.37F, 0.37F);
                    itemRenderer.blockTransformation();
                    break;

                case "Slide":
                    GL11.glTranslatef(0, 0.3f, 0);
                    float smooth2 = (swingProgress * 0.8f - (swingProgress * swingProgress) * 0.8f);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, itemRenderer.equippedProgress * 0.3 * -0.6F, 0.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 2 + smooth2 * 0.5f, smooth2 * 3);
                    GlStateManager.rotate(0f, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.37F, 0.37F, 0.37F);
                    itemRenderer.blockTransformation();
                    break;

                case "Smooth": {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    final float y = -convertedProgress * 2.0F;
                    GlStateManager.translate(0.0F, y / 10.0F + 0.1F, 0.0F);
                    GlStateManager.rotate(y * 10.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(250, 0.2F, 1.0F, -0.6F);
                    GlStateManager.rotate(-10.0F, 1.0F, 0.5F, 1.0F);
                    GlStateManager.rotate(-y * 20.0F, 1.0F, 0.5F, 1.0F);

                    break;
                }

                case "Stab": {
                    final float spin = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2);

                    GlStateManager.translate(0.6f, 0.3f, -0.6f + -spin * 0.7);
                    GlStateManager.rotate(6090, 0.0f, 0.0f, 0.1f);
                    GlStateManager.rotate(6085, 0.0f, 0.1f, 0.0f);
                    GlStateManager.rotate(6110, 0.1f, 0.0f, 0.0f);
                    itemRenderer.transformFirstPersonItem(0.0F, 0.0f);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();
                    break;
                }

                case "Spin": {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0, 0.2F, -1);
                    GlStateManager.rotate(-59, -1, 0, 3);
                    GlStateManager.rotate(-(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                    GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                    break;
                }

                case "Swong": {
                    GlStateManager.translate(.0f, .1f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress, -0.0F);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Stella": {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F - 0.18F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    final float swing = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                    GL11.glRotatef(-swing * 80.0f / 5.0f, swing / 3.0f, -0.0f, 9.0f);
                    GL11.glRotatef(-swing * 40.0f, 8.0f, swing / 9.0f, -0.1f);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Flup": {
                    GlStateManager.translate(.0f, .1f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();
                    GlStateManager.translate(-0.05F, 0.2F, 0.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F, 1.0F, -0.4F, -0.0F);

                    break;
                }

                case "Noov": {
                    itemRenderer.transformFirstPersonItem(animationProgression / 1.5F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();
                    GlStateManager.translate(-0.05F, 0.3F, 0.3F);
                    GlStateManager.rotate(-convertedProgress * 140.0F, 8.0F, 0.0F, 8.0F);
                    GlStateManager.rotate(convertedProgress * 180.0F, 8.0F, 0.0F, 8.0F);

                    break;
                }

                case "Komorebi": {
                    itemRenderer.transformFirstPersonItem(-0.25F, 1.0F + convertedProgress / 10.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glRotated(-convertedProgress * 25.0F, 1.0F, 0.0F, 0.0F);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Rhys": {
                    GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                    GlStateManager.translate(0.0F, 0, 0.0F);
                    GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);

                    final float racism = MathHelper.sin(swingProgress * swingProgress / 64 * (float) Math.PI * 2);

                    GlStateManager.rotate(racism * -5.0F, 0.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(convertedProgress * -12.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(convertedProgress * -65.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();

                    break;
                }

                case "Swing": {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.blockTransformation();
                    GlStateManager.translate(-0.3F, -0.1F, -0.0F);

                    break;
                }

                case "?": {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glTranslatef(-0.35F, 0.1F, 0.0F);
                    GL11.glTranslatef(-0.05F, -0.1F, 0.1F);

                    itemRenderer.blockTransformation();

                    break;
                }
            }

            event.setCancelled();

        }
    };

    @EventLink
    public final Listener<SwingAnimationEvent> onSwingAnimation = event -> {
        int swingAnimationEnd = event.getAnimationEnd();

        swingAnimationEnd *= (-swingSpeed.getValue().floatValue() / 100f) + 1f;

        event.setAnimationEnd(swingAnimationEnd);
    };
}

