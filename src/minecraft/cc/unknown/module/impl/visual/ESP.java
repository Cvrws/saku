package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertex3i;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.event.impl.render.UpdatePlayerAnglesEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = {"ESP", "extra sensory perception"}, description = "Renderiza a los jugadores", category = Category.VISUALS)
public final class ESP extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Box"))
			.add(new SubMode("Skeleton"))
			.setDefault("Box");

    public final BooleanValue colorWhite = new BooleanValue("White Color", this, false);
	private final BooleanValue colorName = new BooleanValue("Color based in name color", this, false, () -> colorWhite.getValue());
	private final BooleanValue checkInvis = new BooleanValue("Show Invisibles", this, false);
	private final BooleanValue redDamage = new BooleanValue("Red on Damage", this, false);
	private final BooleanValue renderSelf = new BooleanValue("Render Self", this, false);
	
    public final NumberValue skeletalWidth = new NumberValue("Width", this, 0.5, 0.1, 5, 0.1, () -> !mode.is("Skeleton"));
	
    private final Map<EntityPlayer, float[][]> rotationMap = new HashMap<>();
    private static final float DEGREES_IN_RADIAN = 57.295776f;
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		for (EntityPlayer player : mc.world.playerEntities) {
			String name = player.getName();
    			
			if (PlayerUtil.unusedNames(player)) {
				return;
            }
			
			if (player == mc.player && !renderSelf.getValue()) {
				continue;
			}
			
			int color = 0;
			
			if (colorName.getValue()) {
	        	color = ColorUtil.getColorFromTags(player.getDisplayName().getFormattedText());
			} else if (colorWhite.getValue()) {
				color = new Color(255, 255, 255).getRGB();
	        } else if (FriendUtil.isFriend(name)) {
				color = new Color(0, 255, 0).getRGB();
			} else if (EnemyUtil.isEnemy(name)) {
				color = new Color(255, 0, 0).getRGB();
			} else {
				color = getTheme().getFirstColor().getRGB();
			}
			
			if (redDamage.getValue() && player.hurtTime > 0) {
				color = new Color(255, 0, 0).getRGB();
			}
			
			if (player.deathTime == 0 && (checkInvis.getValue() || !player.isInvisible())) {

				switch (mode.getValue().getName()) {
				case "Box": RenderUtil.drawSimpleBox(player, color, event.getPartialTicks()); break;
				case "Skeleton": 
			        glPushMatrix();
			        setupRenderState(new Color(color));

			        drawSkeleton((EntityPlayer) player, event.getPartialTicks()); 

			        restoreRenderState();
			        glPopMatrix();			        
					break;
				}
            }
    	}
    };
    
    @EventLink
    public final Listener<UpdatePlayerAnglesEvent> onUpdatePlayerAngles = event -> {
		switch (mode.getValue().getName()) {
		case "Skeleton": updatePlayerAngles(event.getEntityPlayer(), event.getModelBiped()); break;
		}
    };
    
    private void setupRenderState(Color color) {
        GL11.glLineWidth(skeletalWidth.getValue().floatValue());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        ColorUtil.glColor(color.getRGB());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
    }

    private void restoreRenderState() {
    	GL11.glDepthMask(true);
    	GL11.glDisable(GL11.GL_BLEND);
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glDisable(GL11.GL_LINE_SMOOTH);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    public void updatePlayerAngles(EntityPlayer entityPlayer, ModelBiped modelBiped) {
        rotationMap.put(entityPlayer, new float[][]{
                {modelBiped.bipedHead.rotateAngleX, modelBiped.bipedHead.rotateAngleY, modelBiped.bipedHead.rotateAngleZ},
                {modelBiped.bipedRightArm.rotateAngleX, modelBiped.bipedRightArm.rotateAngleY, modelBiped.bipedRightArm.rotateAngleZ},
                {modelBiped.bipedLeftArm.rotateAngleX, modelBiped.bipedLeftArm.rotateAngleY, modelBiped.bipedLeftArm.rotateAngleZ},
                {modelBiped.bipedRightLeg.rotateAngleX, modelBiped.bipedRightLeg.rotateAngleY, modelBiped.bipedRightLeg.rotateAngleZ},
                {modelBiped.bipedLeftLeg.rotateAngleX, modelBiped.bipedLeftLeg.rotateAngleY, modelBiped.bipedLeftLeg.rotateAngleZ}
        });
    }

    private void drawSkeleton(EntityPlayer player, float partialTicks) {
        float[][] entPos = rotationMap.get(player);
        if (entPos != null) {
            glPushMatrix();

            float x = (float) (interpolate(player.posX, player.prevPosX, partialTicks) - mc.getRenderManager().renderPosX);
            float y = (float) (interpolate(player.posY, player.prevPosY, partialTicks) - mc.getRenderManager().renderPosY);
            float z = (float) (interpolate(player.posZ, player.prevPosZ, partialTicks) - mc.getRenderManager().renderPosZ);
            glTranslated(x, y, z);

            boolean sneaking = player.isSneaking();

            float rotationYawHead = player.rotationYawHead;
            float renderYawOffset = player.renderYawOffset;
            float prevRenderYawOffset = player.prevRenderYawOffset;

            float xOff = interpolate(renderYawOffset, prevRenderYawOffset, partialTicks);
            float yOff = sneaking ? 0.6F : 0.75F;

            glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            glTranslatef(0.0F, 0.0F, sneaking ? -0.235F : 0.0F);

            // draw limbs with rotation
            drawLimbs(entPos, yOff, sneaking, xOff, rotationYawHead);

            glPopMatrix();
        }
    }

    private void drawLimbs(float[][] entPos, float yOff, boolean sneaking, float xOff, float rotationYawHead) {
        // draw arms
        for (int i = 1; i <= 2; i++) {
            drawArm(entPos[i + 2], i == 1 ? -0.125F : 0.125F, yOff);
        }

        glTranslatef(0.0F, 0.0F, sneaking ? 0.25F : 0.0F);
        glPushMatrix();
        glTranslatef(0.0F, sneaking ? -0.05F : 0.0F, sneaking ? -0.01725F : 0.0F);

        // draw right and left arm
        for (int i = 1; i <= 2; i++) {
            drawLimb(entPos[i], i == 1 ? -0.375F : 0.375F, yOff + 0.55F);
        }

        // handle head position
        glRotatef(xOff - rotationYawHead, 0.0F, 1.0F, 0.0F);
        drawHead(entPos[0], yOff);

        glPopMatrix();

        // draw spine and other body parts
        drawSpine(yOff);
    }

    private void drawArm(float[] rotations, float xOffset, float yOff) {
        glPushMatrix();
        glTranslatef(xOffset, yOff, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, -yOff, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void drawLimb(float[] rotations, float xOffset, float yOff) {
        glPushMatrix();
        glTranslatef(xOffset, yOff, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, -0.5F, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void drawHead(float[] rotations, float yOff) {
        glPushMatrix();
        glTranslatef(0.0F, yOff + 0.55F, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, 0.3F, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void applyRotations(float[] rotations) {
        if (rotations[0] != 0.0F) {
            glRotatef(rotations[0] * DEGREES_IN_RADIAN, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[1] != 0.0F) {
            glRotatef(rotations[1] * DEGREES_IN_RADIAN, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[2] != 0.0F) {
            glRotatef(rotations[2] * DEGREES_IN_RADIAN, 0.0F, 0.0F, 1.0F);
        }
    }

    private void drawSpine(float yOff) {
        glPushMatrix();
        glTranslated(0.0F, yOff, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.125F, 0.0F, 0.0F);
        glVertex3f(0.125F, 0.0F, 0.0F);
        glEnd();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.0F, yOff, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, 0.55F, 0.0F);
        glEnd();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.0F, yOff + 0.55F, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.375F, 0.0F, 0.0F);
        glVertex3f(0.375F, 0.0F, 0.0F);
        glEnd();
        glPopMatrix();
    }
    
    private double interpolate(final double current, final double previous, final double multiplier) {
        return previous + (current - previous) * multiplier;
    }
    
    private float interpolate(final float current, final float previous, final float multiplier) {
        return previous + (current - previous) * multiplier;
    }
}
