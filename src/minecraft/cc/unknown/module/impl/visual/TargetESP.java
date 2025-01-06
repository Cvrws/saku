package cc.unknown.module.impl.visual;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
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
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(aliases = "Target ESP", description = "Dibuja un objeto al apuntar hacia un objetivo específico.", category = Category.VISUALS)
public final class TargetESP extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Ring"))
			.add(new SubMode("Rect"))
			.setDefault("Ring");

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
	            case "Dotted":
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
	            	break;
	            case "Ring":
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
	                break;
	            case "Rect":
	            	RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * 0.25)));
	            	RenderUtil.renderHitbox(new AxisAlignedBB(x - size, y + (double)entity.height + 0.3D, z - size, x + size, y + (double)entity.height + 0.1D, z + size), 7);
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