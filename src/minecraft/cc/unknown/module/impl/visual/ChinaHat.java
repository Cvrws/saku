package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;

@ModuleInfo(aliases = "China Hat", description = "Obtén un sombrero chino :3", category = Category.VISUALS)
public final class ChinaHat extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Umbrella"))
			.add(new SubMode("53XO"))
			.setDefault("53XO");
	
    private final BooleanValue showInFirstPerson = new BooleanValue("Show in First Person", this, true);
    
    public static long lastFrame = 0;
    private int ticks;

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.getValue()) return;

        ticks += .004 * (System.currentTimeMillis() - lastFrame);
        lastFrame = System.currentTimeMillis();

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.player.getEyeHeight() + 0.5 + (mc.player.isSneaking() ? -0.2 : 0);
        final double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        Color c;

        final double rad = 0.65f;

        int q = 64;

        boolean increaseCount = false;

        switch (mode.getValue().getName()) {
            case "53XO":
                q = 8;
                increaseCount = true;
                break;
            case "Umbrella":
                q = 16;
                break;
            case "Normal":
                q = 1024;
                increaseCount = true;
                break;
        }

        final double rotations = ((mc.player.prevRenderYawOffset + (mc.player.renderYawOffset - mc.player.prevRenderYawOffset) * event.getPartialTicks()) / 60) + 20;

        for (float i = 0; i < Math.PI * 2 + (increaseCount ? 0.01 : 0); i += Math.PI * 4 / q) {
            final double vecX = x + rad * Math.cos(i + rotations);
            final double vecZ = z + rad * Math.sin(i + rotations);

            c = ColorUtil.mixColors(getTheme().getFirstColor(), this.getTheme().getSecondColor(), this.getTheme().getBlendFactor(new Vector2d(0, 0)));

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.8f
            );

            GL11.glVertex3d(vecX, y - 0.25, vecZ);

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.8f
            );

            GL11.glVertex3d(x, y, z);

        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

        GL11.glColor3f(255, 255, 255);
    };
}