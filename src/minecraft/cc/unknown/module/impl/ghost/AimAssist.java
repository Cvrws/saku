package cc.unknown.module.impl.ghost;

import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.PreRenderTickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Te ayuda a apuntar", category = Category.GHOST)
public final class AimAssist extends Module {

	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 5, 0.1, 20, 0.1);
	private final BooleanValue vertical = new BooleanValue("Vertical", this, false);
	private final NumberValue verticalSpeed = new NumberValue("Vertical Speed", this, 5, 0.1, 20, 0.1, () -> !vertical.getValue());
	private final NumberValue smoothness = new NumberValue("Smoothness", this, 1.0, 0.1, 5.0, 0.1);
	private final NumberValue fov = new NumberValue("Fov", this, 180, 1, 360, 5);
	private final NumberValue range = new NumberValue("Range", this, 4.5, 1, 8, 0.1);
	private final BooleanValue requiredClick = new BooleanValue("Require Clicking", this, true);
	private final BooleanValue lockTarget = new BooleanValue("Lock Target", this, true);
	private final BooleanValue ignoreFriend = new BooleanValue("Ignore Friends", this, false);
	private final BooleanValue ignoreTeams = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue visibilityCheck = new BooleanValue("Visibility Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !visibilityCheck.getValue());
	private final BooleanValue checkBlock = new BooleanValue("Check Block Break", this, false);
	private final BooleanValue onlyBed = new BooleanValue("Only Bed", this, false, () -> !checkBlock.getValue());
	private final BooleanValue requiredWeapon = new BooleanValue("Require Weapon", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeams.getValue());
	
    private Double yawNoise = null;
    private Double pitchNoise = null;
    private long nextNoiseTime = -1;
    private long nextNoiseEmpty = 200;
    public EntityPlayer target;
	
    @Override
    public void onDisable() {
        yawNoise = pitchNoise = null;
        nextNoiseTime = -1;
        target = null;
    }
    
    @EventLink
    public final Listener<WorldChangeEvent> onWorld = event -> target = null;
	
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (noAim()) return;

        target = getEnemy();
        if (target == null) return;

        double distance = RotationUtil.getDistanceToEntityBox(target);
        AxisAlignedBB hitbox = target.getEntityBoundingBox();

        Vec3 bestHitVec;
        if (distance < 2.5) {
            bestHitVec = RotationUtil.getNearestPointOnBox(hitbox, mc.player.getPositionVector());
        } else {
            bestHitVec = RotationUtil.getBestHitVec(target);
        }

        final float[] rotations = RotationUtil.getRotations(bestHitVec);

        float noisyYaw = rotations[0] + (yawNoise != null ? yawNoise.floatValue() : 0);
        float noisyPitch = rotations[1] + (pitchNoise != null ? pitchNoise.floatValue() : 0);

        float newYaw = move(noisyYaw, event.getYaw(), horizontalSpeed.getValue().doubleValue(), false);
        float newPitch = move(noisyPitch, event.getPitch(), verticalSpeed.getValue().doubleValue(), true);

        if (vertical.getValue()) {
            double randomOscillation = (Math.random() - 0.5) * 2 * 2.5;
            newPitch = (float) MathHelper.clamp_double(newPitch + randomOscillation, -90, 90);
            mc.player.rotationPitch = newPitch;
        }

        mc.player.rotationYaw = newYaw;
    };

    @EventLink
    public final Listener<PreRenderTickEvent> onPreRenderTick = event -> {
        long time = System.currentTimeMillis();

        if (nextNoiseTime == -1 || time >= nextNoiseTime + nextNoiseEmpty) {
            nextNoiseTime = time + (long) (Math.random() * 60 + 80);
            nextNoiseEmpty = (long) (Math.random() * 100) + 180;

            yawNoise = (Math.random() * 2 - 1) * ((Math.random() * 2 - 1) * 0.3 + 0.8);
            pitchNoise = (Math.random() * 2 - 1) * ((Math.random() * 2 - 1) * 0.35 + 0.6);
        } else if (time >= nextNoiseTime) {
            yawNoise = 0D;
            pitchNoise = 0D;
        }
    };

    private EntityPlayer getEnemy() {
        final int fov = this.fov.getValue().intValue();
        final List<EntityPlayer> players = mc.world.playerEntities;
        final Vec3 playerPos = new Vec3(mc.player);

        EntityPlayer target = null;
        double targetFov = Double.MAX_VALUE;
        for (final EntityPlayer player : players) {
            if (player != mc.player && player.deathTime == 0) {
                double dist = RotationUtil.getDistanceToEntityBox(player);
                if (dist > range.getValue().doubleValue()) continue;
        	    if (FriendUtil.isFriend(player) && ignoreFriend.getValue()) continue;
                if (ignoreTeams.getValue() && PlayerUtil.isTeam(player, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) continue;
                if (fov != 360 && !PlayerUtil.fov(fov, player)) continue;
                if (!visibilityCheck.getValue() && RayCastUtil.rayCast(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), dist) != null) continue;
                double curFov = Math.abs(PlayerUtil.getFov(player.posX, player.posZ));
                if (curFov < targetFov) {
                    target = player;
                    targetFov = curFov;
                }
            }
        }
        return target;
    }
    
	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
	    if (requiredWeapon.getValue() && !PlayerUtil.isHoldingWeapon()) return true;
	    if (requiredClick.getValue() && !PlayerUtil.isClicking()) return true;
	    if (mouseOverEntity.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectType.ENTITY)) return true;
	    
	    if (checkBlock.getValue() && mc.objectMouseOver != null) {
	        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	        if (blockPos != null) {
	            Block block = mc.world.getBlockState(blockPos).getBlock();

	            if (block != Blocks.air) {
	                if (onlyBed.getValue()) {
	                    if (block instanceof BlockBed) {
	                        return true;
	                    }
	                } else {
	                    if (!(block instanceof BlockLiquid) && block instanceof Block) {
	                        return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}
    
	private float move(double target, double current, double diff, boolean isPitch) {
	    Vector2f rotation = new Vector2f((float) target, isPitch ? (float) target : mc.player.rotationPitch);
	    Vector2f previousRotation = new Vector2f((float) current, isPitch ? (float) current : mc.player.rotationPitch);
	    Vector2f adjustedRotation = RotationUtil.applySensitivityPatch(rotation, previousRotation);
	    float delta = isPitch ? adjustedRotation.y - previousRotation.y : adjustedRotation.x - previousRotation.x;
	    float fps = Math.max(10, Minecraft.getDebugFPS());
	    float smoothFactor = smoothness.getValue().floatValue() / (fps / 60.0f);
	    return (float) current + Math.signum(delta) * Math.min(Math.abs(delta), (float) diff * smoothFactor);
	}
}
