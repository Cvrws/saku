package cc.unknown.module.impl.ghost;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Te ayuda a apuntar", category = Category.GHOST)
public final class AimAssist extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Advanced"))
			.setDefault("Advanced");
	
	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 45.0, 5.0, 100.0, 1.0);
	private final NumberValue horizontalCompl = new NumberValue("Horizontal Complement", this, 35.0, 2.0, 97.0, 1.0);
	
	private BooleanValue vertical = new BooleanValue("Vertical", this, false);
	private NumberValue verticalSpeed = new NumberValue("Vertical Aim Speed", this, 10, 1, 15, 1, () -> !vertical.getValue());
	private NumberValue verticalCompl = new NumberValue("Vertical Complement", this, 5, 1, 10, 1, () -> !vertical.getValue());
	private BooleanValue verticalRandom = new BooleanValue("Vertical Random", this, false, () -> !vertical.getValue());
	private NumberValue verticalRandomization = new NumberValue("Vertical Randomization", this, 1.2, 0.1, 5, 0.01, () -> !verticalRandom.getValue());

	private final ModeValue randomMode = new ModeValue("Speed Randomization", this)
			.add(new SubMode("Thread Local Random"))
			.add(new SubMode("Random Secure"))
			.add(new SubMode("Gaussian"))
			.setDefault("Thread Local Random");
	
	private final NumberValue maxAngle = new NumberValue("Max Angle", this, 180, 1, 180, 1);
	private final NumberValue distance = new NumberValue("Distance", this, 4, 1, 8, 0.1);
	private final BooleanValue clickAim = new BooleanValue("Require Clicking", this, true);
	private final BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friends", this, false);
	private final BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue aimAtInvisibleEnemies = new BooleanValue("Target invisibles", this, false);
	private final BooleanValue lineOfSightCheck = new BooleanValue("Visibility Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !lineOfSightCheck.getValue());
	private final BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Check Block Break", this, false);
	private final BooleanValue weaponOnly = new BooleanValue("Weapons Only", this, false);
	private final BooleanValue targetIndicator = new BooleanValue("Target Indicator", this, false);
	public EntityPlayer target;
	private double animation;
	private boolean direction;
	private Random random = new Random();
	private StopWatch stopWatch = new StopWatch();
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (noAim()) {
			return;
		}

		target = getEnemy();
		if (target == null) return;

        double yawSpeed = this.horizontalSpeed.getValue().doubleValue();
        double yawCompl = this.horizontalCompl.getValue().doubleValue();
        double yawOffset = MathUtil.nextSecureDouble(yawSpeed, yawCompl) / 180;
        
        double yawFov = PlayerUtil.fovFromTarget(target);
        double pitchEntity = PlayerUtil.pitchFromTarget(target, 0);
        float yaw = getSpeedRandomize(randomMode.getValue().getName(), yawFov, yawOffset, yawSpeed, yawCompl);
		double verticalRandomOffset = ThreadLocalRandom.current().nextDouble(verticalCompl.getValue().doubleValue() - 1.47328, verticalCompl.getValue().doubleValue() + 2.48293) / 100;
		float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(verticalSpeed.getValue().doubleValue() - 4.723847, verticalSpeed.getValue().doubleValue()))));
        
        if (onTarget(target)) {
            if (isYawFov(yawFov)) {
                mc.player.rotationYaw += yaw;
                mc.player.setAngles(Math.abs(yaw / 50), 0.0f);
            }
            
			if (vertical.getValue()) {
				float pitchChange = random.nextBoolean() ? -nextFloat(0F, verticalRandomization.getValue().floatValue()) : nextFloat(0F, verticalRandomization.getValue().floatValue());
				float pitchAdjustment = (float) (verticalRandom.getValue() ? pitchChange : resultVertical);
				float newPitch = mc.player.rotationPitch + pitchAdjustment;
				mc.player.rotationPitch += pitchAdjustment;
				mc.player.rotationPitch = newPitch >= 90f ? newPitch - 360f : newPitch <= -90f ? newPitch + 360f : newPitch;
			}
        } else {
        	if (isYawFov(yawFov)) {
            	mc.player.rotationYaw += yaw;
            	mc.player.setAngles(Math.abs(yaw / 50), 0.0f);
        	}
        	
			if (vertical.getValue()) {
				float pitchChange = random.nextBoolean() ? -nextFloat(0F, verticalRandomization.getValue().floatValue()) : nextFloat(0F, verticalRandomization.getValue().floatValue());
				float pitchAdjustment = (float) (verticalRandom.getValue() ? pitchChange : resultVertical);
				float newPitch = mc.player.rotationPitch + pitchAdjustment;
				mc.player.rotationPitch += pitchAdjustment;
				mc.player.rotationPitch = newPitch >= 90f ? newPitch - 360f : newPitch <= -90f ? newPitch + 360f : newPitch;
			}
        }
	};
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (target != null && targetIndicator.getValue()) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            GlStateManager.disableCull();
            GL11.glShadeModel(GL11.GL_SMOOTH);

            double x = target.prevPosX + (target.posX - target.prevPosX) * event.getPartialTicks() - mc.getRenderManager().viewerPosX;
            double y = target.prevPosY + (target.posY - target.prevPosY) * event.getPartialTicks() - mc.getRenderManager().viewerPosY;
            double z = target.prevPosZ + (target.posZ - target.prevPosZ) * event.getPartialTicks() - mc.getRenderManager().viewerPosZ;
            double size = (double)(target.width / 2.0F);
            
            animation += direction ? -0.02D : 0.02D;
            if (Math.abs(animation) > target.height / 2.0F) {
                direction = !direction;
            }
            
            GL11.glPointSize(10.0F);
            GL11.glTranslated(x, y, z);
            GL11.glRotatef((mc.player.ticksExisted + event.getPartialTicks()) * 8.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(-x, -y, -z);
            
            GL11.glBegin(GL11.GL_POINTS);
            for (double angle = 0.0D; angle <= 360.0D; angle += 40.0D) {
            	double offsetX = Math.sin(angle * Math.PI / 180.0D) * target.width;
            	double offsetZ = Math.cos(angle * Math.PI / 180.0D) * target.width;
            	double pointY = y + animation + target.height / 2.0F;
            	
            	RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * 0.25)));
            	GL11.glVertex3d(x + offsetX, pointY, z + offsetZ);
            }
            GL11.glEnd();
            	
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.enableCull();
            GL11.glPopMatrix();
		}
	};
	
	@Override
	public void onDisable() {
		target = null;
	}

	public EntityPlayer getEnemy() {
	    int fov = maxAngle.getValue().intValue();
	    final Vec3 playerPos = new Vec3(mc.player);
	
	    target = null;
	    double targetFov = 180;
	    EntityPlayer potentialTarget = null;
	    int validTargets = 0;
	
	    for (final EntityPlayer player : mc.theWorld.playerEntities) {
	        if (player != mc.player && player.deathTime == 0 && player.isEntityAlive()) {
	            if (getInstance().getEnemyManager().isEnemy(player)) continue;
	            if (player.getName().contains("[NPC]")) continue;
	            if (player.getName().contains("MEJORAS")) continue;
	            if (player.getName().contains("CLICK DERECHO")) continue;
	            if (aimAtInvisibleEnemies.getValue() && player.isInvisible()) continue;
	            if (getInstance().getFriendManager().isFriend(player) && ignoreFriendlyEntities.getValue()) continue;
	            if (ignoreTeammates.getValue() && PlayerUtil.isTeam(player, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) continue;
	            if (playerPos.distanceTo(player) > distance.getValue().doubleValue()) continue;
	            if (lineOfSightCheck.getValue() && !mc.player.canEntityBeSeen(player)) continue;
	            if (fov != 180 && !PlayerUtil.fov(player, fov)) continue;
	            double fov2 = Math.abs(PlayerUtil.getFov(player.posX, player.posZ));
	            if (fov2 < targetFov) {
	                potentialTarget = player;
	                targetFov = fov2;
	            }
	            validTargets++;
	        }
	    }
	
	    if (validTargets == 1) {
	        //maxAngle.setValue(180);
	    }
	
	    target = potentialTarget;
	    return target;
	}

	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
	    if (weaponOnly.getValue() && !PlayerUtil.isHoldingWeapon()) return true;
	    if (clickAim.getValue() && !PlayerUtil.isClicking()) return true;
	    if (mouseOverEntity.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)) return true;
	    if (disableAimWhileBreakingBlock.getValue() && mc.objectMouseOver != null) {
	        BlockPos p = mc.objectMouseOver.getBlockPos();
	        if (p != null) {
	            Block bl = mc.theWorld.getBlockState(p).getBlock();
	            if (bl != Blocks.air && !(bl instanceof BlockLiquid) && (bl instanceof Block || bl instanceof BlockBed)) {
	                return true;
	            }
	        }
	    }
        
	    return false;
	}

	private boolean onTarget(EntityPlayer target) {
		return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
				&& mc.objectMouseOver.typeOfHit != MovingObjectType.BLOCK
				&& mc.objectMouseOver.entityHit == target;
	}

	public float getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
	    double randomComplement;
	    float result;

	    switch (mode) {
	        case "Thread Local Random":
	            randomComplement = MathUtil.nextDouble(complement - 1.47328, complement + 2.48293) / 100;
	            result = (float) (-(fov * offset + fov / (101.0 - MathUtil.nextDouble(speed - 4.723847, speed))));
	            return (float) (randomComplement + result);

	        case "Random Secure":
	            randomComplement = MathUtil.nextSecureDouble(complement - 1.47328, complement + 2.48293) / 100;
	            result = (float) (-(fov * offset + fov / (101.0 - MathUtil.nextSecureDouble(speed - 4.723847, speed))));
	            return (float) (randomComplement + result);

	        case "Gaussian":
	            randomComplement = complement + new Random().nextGaussian() * 0.5;
	            randomComplement /= 100;
	            result = (float) (-(fov * offset + fov / (101.0 - (speed + new Random().nextGaussian() * 0.3))));
	            return (float) (randomComplement + result);

	        default:
	            throw new IllegalArgumentException("Unknown mode: " + mode);
	    }
	}

	private boolean isYawFov(double fov) {
		return fov > 1.0D || fov < -1.0D;
	}
}
