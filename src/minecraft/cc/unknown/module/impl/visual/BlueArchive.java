package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Blue Archive", description = "blue archive fan made", category = Category.VISUALS)
public final class BlueArchive extends Module {
	
	private final ModeValue haloType = new ModeValue("Halo Type", this)
			.add(new SubMode("Shiroko"))
			.add(new SubMode("Hoshino"))
			.add(new SubMode("Aris"))
			.add(new SubMode("Yuuka"))
			.add(new SubMode("Natsu"))
			.add(new SubMode("Reisa"))
			.add(new SubMode("Shiroko Terror"))
			.add(new SubMode("None"))
			.setDefault("Shiroko");

	private final BooleanValue showInFirstPerson = new BooleanValue("First Person", this, true, () -> haloType.is("None"));

	private final ModeValue stickerType = new ModeValue("Sticker Type", this)
			.add(new SubMode("Aris"))
			.add(new SubMode("Shiroko"))
			.add(new SubMode("Azusa"))
			.add(new SubMode("Hina Swimsuit"))
			.add(new SubMode("Ui"))
			.add(new SubMode("Hoshino Swimsuit"))
			.add(new SubMode("Mika"))
			.add(new SubMode("Ibuki"))
			.add(new SubMode("None"))
			.setDefault("Aris");
	
    private Animation animation = new Animation(Easing.LINEAR, 2000);
    private ScaledResolution sr = new ScaledResolution(mc);
    private boolean isReversing = true;
    
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        switch (stickerType.getValue().getName()) {
	        case "Aris": {
	            drawImage(new ResourceLocation("sakura/images/aris.png"), 160, 170, 150, 160, 1);
	            break;
	        }
	        case "Shiroko": {
	            drawImage(new ResourceLocation("sakura/images/shiroko.png"), 95, 165, 85, 160, 1);
	            break;
	        }
	        case "Azusa": {
	            drawImage(new ResourceLocation("sakura/images/azusa.png"), 130, 170, 120, 160, 1);
	            break;
	        }
	        case "Hina Swimsuit": {
	            drawImage(new ResourceLocation("sakura/images/hina_swimsuit.png"), 110, 170, 100, 160, 1);
	            break;
	        }
	        case "Ui": {
	            drawImage(new ResourceLocation("sakura/images/ui.png"), 110, 170, 100, 160, 1);
	            break;
	        }
	        case "Hoshino Swimsuit": {
	            drawImage(new ResourceLocation("sakura/images/hoshino_swimsuit.png"), 140, 170, 130, 160, 1);
	            break;
	        }
	        case "Mika": {
	            drawImage(new ResourceLocation("sakura/images/mika.png"), 110, 170, 95, 160, 1);
	            break;
	        }
	        case "Ibuki": {
	            drawImage(new ResourceLocation("sakura/images/ibuki.png"), 110, 18, 100, 170, 1);
	            break;
	        }
	    }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	 if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.getValue()) return;
    	 
	    	 switch (haloType.getValue().getName()) {
	         case "Shiroko": {
	             drawShirokoHalo(event);
	             break;
	         }
	         case "Hoshino": {
	             drawHoshinoHalo(event);
	             break;
	         }
	         case "Aris": {
	             drawArisHalo(event);
	             break;
	         }
	         case "Yuuka": {
	             drawYuukaHalo(event);
	             break;
	         }
	         case "Natsu": {
	             drawNatsuHalo(event);
	             break;
	         }
	         case "Reisa": {
	             drawReisaHalo(event);
	             break;
	         }
	         case "Shiroko Terror": {
	             drawShiroko_TerrorHalo(event);
	             break;
	         }
	     }
    };
    
    public void drawShirokoHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(
                new Color(MathHelper.clamp_int((int) (animation.getValue() * 1800), 0, 255),
                        MathHelper.clamp_int((int) (230 + animation.getValue() * 200), 0, 255), 250, 220)
        );

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        GL11.glLineWidth(2.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.18f,
                    (float) Math.sin(Math.toRadians(i)) * 0.18f
            );
        }
        GL11.glEnd();

        GL11.glTranslated(0.0f, 0.0f, -0.02f);
        GL11.glLineWidth(3.7f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            GL11.glVertex2f(x, y);

            if (i % 90 == 0) {
                float offset = 0.1f;
                float inwardOffset = 0.03f;

                GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
                GL11.glVertex2f(x, y);
                GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
                GL11.glVertex2f(x, y);
            }
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();

        GL11.glPopMatrix();
    }

    public void drawShiroko_TerrorHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(79, 112, 117, 255));

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        GL11.glLineWidth(2.2f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.2f,
                    (float) Math.sin(Math.toRadians(i)) * 0.2f
            );
        }
        GL11.glEnd();

        GL11.glTranslated(0.0f, 0.0f, -0.02f);
        GL11.glLineWidth(5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        for (int i = 0; i < 360; i += 90) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            if (i % 90 == 0) {
                float offset = 0.1f;
                float triangleX = x + (float) Math.cos(angle) * offset;
                float triangleY = y + (float) Math.sin(angle) * offset;

                float rotationAngle = i - 90;
                GL11.glLineWidth(5f * getExtraWidth());
                drawTriangle(triangleX / 1.35f, triangleY / 1.35f, 0.012f, 0.1f, rotationAngle);
            }
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();

        GL11.glPopMatrix();
    }

    public void drawReisaHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();
        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.color(new Color(200, 200, 250, 220));

        GL11.glLineWidth(3.0f * getExtraWidth());
        drawStar(0.0f, 0.0f, 0.3f);

        GL11.glPushMatrix();
        GL11.glRotatef(36, 0F, 0F, 1F);
        drawStar(0.0f, 0.0f, 0.14f);
        GL11.glPopMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();

    }

    public void drawNatsuHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(254, 200, 200, 240));

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        GL11.glLineWidth(3.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.3f,
                    (float) Math.sin(Math.toRadians(i)) * 0.3f
            );
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.15f;
            float y = (float) Math.sin(angle) * 0.15f;

            GL11.glVertex2f(x, y);

            if (i % 90 == 0) {
                float offset = 0.05f;
                float inwardOffset = 0.05f;

                GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
                GL11.glVertex2f(x, y);
                GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
                GL11.glVertex2f(x, y);
            }
        }
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawYuukaHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.color(new Color(80, 150, 180, 250));
        GL11.glLineWidth(2f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.292f,
                    (float) Math.sin(Math.toRadians(i)) * 0.292f
            );
        }
        GL11.glEnd();

        RenderUtil.color(new Color(30, 30, 30, 200));
        GL11.glLineWidth(6f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.3f,
                    (float) Math.sin(Math.toRadians(i)) * 0.3f
            );
        }
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawHoshinoHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();
        float extraHeight = 0.035f;
        float extensionLength = 0.18f;
        float smallExtensionLength = 0.08f;

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(237, MathHelper.clamp_int(110 + (int) (animation.getValue() * 600), 0, 255), 183, 220)); // RGB for pink

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        GL11.glLineWidth(4.0f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.13f,
                    (float) Math.sin(Math.toRadians(i)) * 0.13f
            );
        }
        GL11.glEnd();

        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
        GL11.glLineWidth(2.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.20f;
            float y = (float) Math.sin(angle) * 0.20f;

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
        GL11.glLineWidth(4.0f * getExtraWidth());

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 15; i <= 165; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.27f;
            float y = (float) Math.sin(angle) * 0.27f;
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 195; i <= 345; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.27f;
            float y = (float) Math.sin(angle) * 0.27f;
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        GL11.glLineWidth(4.0f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINES);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(0)) * 0.27f, (float) Math.sin(Math.toRadians(0)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(0)) * (0.27f + extensionLength), (float) Math.sin(Math.toRadians(0)) * (0.27f + extensionLength), 0.0f);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(180)) * 0.27f, (float) Math.sin(Math.toRadians(180)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(180)) * (0.27f + extensionLength), (float) Math.sin(Math.toRadians(180)) * (0.27f + extensionLength), 0.0f);

        GL11.glEnd();

        GL11.glLineWidth(4.0f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINES);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(15)) * 0.268f, (float) Math.sin(Math.toRadians(15)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(15)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(12)) * (0.27f + smallExtensionLength), 0.0f);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(165)) * 0.268f, (float) Math.sin(Math.toRadians(165)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(165)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(168)) * (0.27f + smallExtensionLength), 0.0f);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(195)) * 0.268f, (float) Math.sin(Math.toRadians(195)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(195)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(192)) * (0.27f + smallExtensionLength), 0.0f);

        GL11.glVertex3f((float) Math.cos(Math.toRadians(345)) * 0.268f, (float) Math.sin(Math.toRadians(345)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(345)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(348)) * (0.27f + smallExtensionLength), 0.0f);

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawArisHalo(Render3DEvent event) {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.player.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY + height,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(161, 253, 228, 220));

        float yaw = mc.player.rotationYaw;

        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        drawRectangle(0.20f, 0.02f, 0.26f, 0.26f, 4f, false);
        drawRectangle(0.2f, 0.3f, 0.4f, 0.4f, 6f, false);
        drawRectangle(-0.09f, 0.21f, 0.35f, 0.35f, 5f, false);
        drawRectangle(-0.13f, 0.45f, 0.15f, 0.05f, 4f, false);
        drawRectangle(0.12f, 0.49f, 0.1f, 0f, 6f, false);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public float getExtraWidth() {
        if (mc.gameSettings.thirdPersonView == 0 || Config.zoomMode) {
            return 2;
        }
        return 1;
    }

    private void drawRectangle(float x, float y, float width, float height, float lineWidth, boolean filled) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x - 0.05f, y - 0.15f, 0.0f);

        if (filled) {
            GL11.glLineWidth(lineWidth * getExtraWidth());
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(-width / 2, -height / 2);
            GL11.glVertex2f(width / 2, -height / 2);
            GL11.glVertex2f(width / 2, height / 2);
            GL11.glVertex2f(-width / 2, height / 2);
            GL11.glEnd();
        } else {
            GL11.glLineWidth(lineWidth * getExtraWidth());
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex2f(-width / 2, -height / 2);
            GL11.glVertex2f(width / 2, -height / 2);
            GL11.glVertex2f(width / 2, height / 2);
            GL11.glVertex2f(-width / 2, height / 2);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
    }

    private void drawStar(float x, float y, float radius) {
        final int POINTS = 5;
        final float[] angles = new float[POINTS * 2];

        for (int i = 0; i < POINTS * 2; i++) {
            angles[i] = (float) Math.toRadians(i * 360.0f / (POINTS * 2) - 90.0f);
        }

        float[] vertices = new float[POINTS * 4];
        float innerRadius = radius * 0.6f;

        for (int i = 0; i < POINTS * 2; i++) {
            float angle = angles[i];
            float currentRadius = (i % 2 == 0) ? radius : innerRadius;
            vertices[i * 2] = x + (float) Math.cos(angle) * currentRadius;
            vertices[i * 2 + 1] = y + (float) Math.sin(angle) * currentRadius;
        }

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < POINTS * 2; i++) {
            GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
        }
        GL11.glEnd();
    }

    private void drawTriangle(float x, float y, float base, float height, float rotationAngle) {
        float[] vertices = new float[6];

        vertices[0] = -base / 2;
        vertices[1] = 0;

        vertices[2] = base / 2;
        vertices[3] = 0;

        vertices[4] = 0;
        vertices[5] = height;

        GL11.glPushMatrix();

        GL11.glTranslatef(x, y, 0);

        GL11.glRotatef(rotationAngle, 0, 0, 1);

        for (int i = 0; i < 3; i++) {
            GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
        }
        GL11.glEnd();

        GL11.glPopMatrix();
    }
    
    private void drawImage(ResourceLocation image, int x, int y, int width, int height, double scale) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0,
                (int) Math.round(width * scale), (int) Math.round(height * scale), Math.round(width * scale), Math.round(height * scale));
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }
}