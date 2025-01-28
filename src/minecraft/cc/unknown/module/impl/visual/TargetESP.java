package cc.unknown.module.impl.visual;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.impl.duration.impl.Utils;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(aliases = "Target ESP", description = "Dibuja un objeto al apuntar hacia un objetivo específico.", category = Category.VISUALS)
public final class TargetESP extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Ring"))
			.add(new SubMode("Rect"))
			.add(new SubMode("Vape"))
			.add(new SubMode("Dotted"))
			.setDefault("Vape");
	
	private final DescValue normal = new DescValue("Normal Color", this, () -> !mode.is("Vape"));
	private final NumberValue normalRed = new NumberValue("Normal Red", this, 100, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue normalGreen = new NumberValue("Normal Green", this, 100, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue normalBlue = new NumberValue("Normal Blue", this, 190, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue normalAlpha = new NumberValue("Normal Alpha", this, 100, 0, 255, 1, () -> !mode.is("Vape"));
	
	private final DescValue hit = new DescValue("Hit Color", this, () -> !mode.is("Vape"));
	private final NumberValue hitRed = new NumberValue("Hit Red", this, 255, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue hitGreen = new NumberValue("Hit Green", this, 0, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue hitBlue = new NumberValue("Hit Blue", this, 0, 0, 255, 1, () -> !mode.is("Vape"));
	private final NumberValue hitAlpha = new NumberValue("Hit Alpha", this, 100, 0, 255, 1, () -> !mode.is("Vape"));

	private double animation;
	private boolean direction;

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		KillAura killAura = getModule(KillAura.class);
		AimAssist aimAssist = getModule(AimAssist.class);

	    if ((killAura != null && killAura.isEnabled() && killAura.target != null) || (aimAssist != null && aimAssist.isEnabled() && aimAssist.target != null)) {

	        EntityLivingBase entity = killAura != null && killAura.target != null ? killAura.target : aimAssist.target;

	        if (entity != null) {
	            GL11.glPushMatrix();
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glDisable(GL11.GL_DEPTH_TEST);
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GL11.glDepthMask(false);
	            GlStateManager.disableCull();
	            GL11.glShadeModel(GL11.GL_SMOOTH);

	            double x = entity.prevPosX + (entity.posX - entity.prevPosX) * event.getPartialTicks() - mc.getRenderManager().viewerPosX;
	            double y = entity.prevPosY + (entity.posY - entity.prevPosY) * event.getPartialTicks() - mc.getRenderManager().viewerPosY;
	            double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * event.getPartialTicks() - mc.getRenderManager().viewerPosZ;
	            double size = (double)(entity.width / 2.0F);
	            
	            animation += direction ? -0.02D : 0.02D;
	            if (Math.abs(animation) > entity.height / 2.0F) {
	                direction = !direction;
	            }
	            
	            switch (mode.getValue().getName()) {
	            case "Vape":
	                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
	                GL11.glPushMatrix();

	                mc.entityRenderer.disableLightmap();
	                GL11.glDisable(GL11.GL_TEXTURE_2D);
	                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	                GL11.glEnable(GL11.GL_LINE_SMOOTH);
	                GL11.glEnable(GL11.GL_BLEND);

	                GL11.glDisable(GL11.GL_DEPTH_TEST);
	                GL11.glDisable(GL11.GL_CULL_FACE);
	                GL11.glShadeModel(GL11.GL_SMOOTH);
	                mc.entityRenderer.disableLightmap();

	                double radius = entity.width;
	                double eased = entity.height - 0.2;

	                for (int segments = 0; segments < 360; segments += 5) {
	                    double x1 = x - Math.sin(segments * Math.PI / 180F) * radius;
	                    double z1 = z + Math.cos(segments * Math.PI / 180F) * radius;
	                    double x2 = x - Math.sin((segments - 5) * Math.PI / 180F) * radius;
	                    double z2 = z + Math.cos((segments - 5) * Math.PI / 180F) * radius;

	                    GL11.glBegin(GL11.GL_QUADS);
	                    if (entity.hurtTime > 0) {
	                        GL11.glColor4f(
	                            hitRed.getValue().intValue() / 255f,
	                            hitGreen.getValue().intValue() / 255f,
	                            hitBlue.getValue().intValue() / 255f,
	                            hitAlpha.getValue().intValue() / 255f
	                        );
	                    } else {
	                        GL11.glColor4f(
	                            normalRed.getValue().intValue() / 255f,
	                            normalGreen.getValue().intValue() / 255f,
	                            normalBlue.getValue().intValue() / 255f,
	                            normalAlpha.getValue().intValue() / 255f
	                        );
	                    }
	                    GL11.glVertex3d(x1, y, z1);
	                    GL11.glVertex3d(x2, y, z2);

	                    if (entity.hurtTime > 0) {
	                        GL11.glColor4f(
	                            hitRed.getValue().intValue() / 255f,
	                            hitGreen.getValue().intValue() / 255f,
	                            hitBlue.getValue().intValue() / 255f,
	                            0
	                        );
	                    } else {
	                        GL11.glColor4f(
	                            normalRed.getValue().intValue() / 255f,
	                            normalGreen.getValue().intValue() / 255f,
	                            normalBlue.getValue().intValue() / 255f,
	                            0
	                        );
	                    }
	                    GL11.glVertex3d(x2, y + eased, z2);
	                    GL11.glVertex3d(x1, y + eased, z1);
	                    GL11.glEnd();

	                    GL11.glBegin(GL11.GL_LINE_LOOP);
	                    GL11.glVertex3d(x2, y + eased, z2);
	                    GL11.glVertex3d(x1, y + eased, z1);
	                    GL11.glEnd();
	                }

	                GL11.glPopMatrix();
	                GL11.glPopAttrib();
	                break;
	            case "Dotted":
	                GL11.glPushMatrix();
	                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

	                GL11.glPointSize(10.0F);
	                GL11.glTranslated(x, y, z);
	                GL11.glRotatef((mc.player.ticksExisted + event.getPartialTicks()) * 8.0F, 0.0F, 1.0F, 0.0F);
	                GL11.glTranslated(-x, -y, -z);

	                GL11.glBegin(GL11.GL_POINTS);
	                for (double angle = 0.0D; angle <= 360.0D; angle += 40.0D) {
	                    double offsetX = Math.sin(angle * Math.PI / 180.0D) * entity.width;
	                    double offsetZ = Math.cos(angle * Math.PI / 180.0D) * entity.width;
	                    double pointY = y + animation + entity.height / 2.0F;

	                    RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * 0.25)));
	                    GL11.glVertex3d(x + offsetX, pointY, z + offsetZ);
	                }
	                GL11.glEnd();

	                GL11.glPopAttrib();
	                GL11.glPopMatrix();
	                break;
	            case "Ring":
	                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
	                GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
	                for (double angle = 0.0D; angle <= 360.0D; angle += 10.0D) {
	                    double radians = Math.toRadians(angle);
	                    double offsetX = x + Math.sin(radians) * entity.width;
	                    double offsetZ = z + Math.cos(radians) * entity.width;
	                    double topY = y + animation + entity.height / 2.0F;
	                    double bottomY = topY - animation / entity.height;

	                    RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * 0.25)));
	                    GL11.glVertex3d(offsetX, topY, offsetZ);
	                    GL11.glVertex3d(offsetX, bottomY, offsetZ);
	                }
	                GL11.glEnd();
	                GL11.glPopAttrib();
	                break;
	            case "Rect":
	                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
	                RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * 0.25)));
	                RenderUtil.renderHitbox(new AxisAlignedBB(x - size, y + (double) entity.height + 0.3D, z - size, x + size, y + (double) entity.height + 0.1D, z + size), 7);
	                GL11.glPopAttrib();
	                break;
	            }

	            GL11.glShadeModel(GL11.GL_FLAT);
	            GL11.glEnable(GL11.GL_DEPTH_TEST);
	            GL11.glDisable(GL11.GL_BLEND);
	            GL11.glDepthMask(true);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GlStateManager.enableCull();
	            GL11.glPopMatrix();
	        }
	    }
	};
}