package cc.unknown.module.impl.ghost;

import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.PreRenderTickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.structure.geometry.Doble;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Te ayuda a apuntar", category = Category.GHOST)
public final class AimAssist extends Module {

	private final NumberValue horizontalSpeed = new NumberValue("Horizontal Speed", this, 5, 1, 20, 0.1);
	private final BooleanValue vertical = new BooleanValue("Vertical", this, false);
	private final NumberValue verticalSpeed = new NumberValue("Vertical Speed", this, 5, 1, 20, 0.1, () -> !vertical.getValue());
	private final NumberValue fov = new NumberValue("Fov", this, 180, 1, 360, 5);
	private final NumberValue range = new NumberValue("Range", this, 4.5, 1, 8, 0.1);
	private final BooleanValue requiredClick = new BooleanValue("Require Clicking", this, true);
	private final BooleanValue randomization = new BooleanValue("Randomization", this, false);
	private final BooleanValue lockTarget = new BooleanValue("Lock Target", this, true);
	private final BooleanValue ignoreTeams = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue visibilityCheck = new BooleanValue("Visibility Check", this, true);
	private final BooleanValue checkBlock = new BooleanValue("Check Block", this, false);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !visibilityCheck.getValue());
	private final BooleanValue requiredWeapon = new BooleanValue("Require Weapon", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeams.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeams.getValue());
	
    private double yawNoise = 0;
    private double pitchNoise = 0;
    private long nextNoiseRefreshTime = -1;
    private long nextNoiseEmptyTime = 200;
    public EntityPlayer target;
	
    @Override
    public void onDisable() {
        yawNoise = pitchNoise = 0;
        nextNoiseRefreshTime = -1;
    }
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (noAim()) return;

        target = getEnemy();
        if (target == null) return;
        final boolean onTarget = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit == target;

        double deltaYaw = randomization.getValue() ? yawNoise : 0;
        double deltaPitch = randomization.getValue() ? pitchNoise : 0;
        
        double hSpeed = horizontalSpeed.getValue().doubleValue();
        double vSpeed = verticalSpeed.getValue().doubleValue();

        if (onTarget) {
            if (lockTarget.getValue()) {
                hSpeed *= 0.85;
                vSpeed *= 0.85;
            } else {
                hSpeed = 0;
                vSpeed = 0;
            }
        }

        final Doble<Doble<Float, Float>, Doble<Float, Float>> rotation = getRotation(target.getEntityBoundingBox());
        final Doble<Float, Float> yaw = rotation.getFirst();
        final Doble<Float, Float> pitch = rotation.getSecond();

        boolean move = false;

        final float curYaw = event.getYaw();
        final float curPitch = event.getPitch();
        if (yaw.getFirst() > curYaw) {
            move = true;
            final float after = rotMove(yaw.getFirst(), curYaw, (float) hSpeed);
            deltaYaw += after - curYaw;
        } else if (yaw.getSecond() < curYaw) {
            move = true;
            final float after = rotMove(yaw.getSecond(), curYaw, (float) hSpeed);
            deltaYaw += after - curYaw;
        }
        
        if (vertical.getValue()) {
            if (pitch.getFirst() > curPitch) {
                move = true;
                final float after = rotMove(pitch.getFirst(), curPitch, (float) vSpeed);
                deltaPitch += after - curPitch;
            } else if (pitch.getSecond() < curPitch) {
                move = true;
                final float after = rotMove(pitch.getSecond(), curPitch, (float) vSpeed);
                deltaPitch += after - curPitch;
            }
        }

        if (move) {
            deltaYaw += (Math.random() - 0.5) * Math.min(0.8, deltaPitch / 10.0);
            deltaPitch += (Math.random() - 0.5) * Math.min(0.8, deltaYaw / 10.0);
        }

        mc.player.rotationYaw += (float) deltaYaw;
        mc.player.rotationPitch += (float) deltaPitch;
	};
	
	@EventLink
	public final Listener<PreRenderTickEvent> onPreRenderTick = event -> {
        long time = System.currentTimeMillis();
        if (nextNoiseRefreshTime == -1 || time >= nextNoiseRefreshTime + nextNoiseEmptyTime) {
            nextNoiseRefreshTime = (long) (time + Math.random() * 60 + 80);
            nextNoiseEmptyTime = (long) (Math.random() * 100 + 180);
            yawNoise = (Math.random() - 0.5) * 2 * ((Math.random() - 0.5) * 0.3 + 0.8);
            pitchNoise = (Math.random() - 0.5) * 2 * ((Math.random() - 0.5) * 0.35 + 0.6);
        } else if (time >= nextNoiseRefreshTime) {
            yawNoise = 0d;
            pitchNoise = 0d;
        }
	};

    private @Nullable EntityPlayer getEnemy() {
        final int fov = this.fov.getValue().intValue();
        final List<EntityPlayer> players = mc.world.playerEntities;
        final Vec3 playerPos = new Vec3(mc.player);

        EntityPlayer target = null;
        double targetFov = Double.MAX_VALUE;
        for (final EntityPlayer entityPlayer : players) {
            if (entityPlayer != mc.player && entityPlayer.deathTime == 0) {
                double dist = playerPos.distanceTo(entityPlayer);
                if (FriendUtil.isFriend(entityPlayer)) continue;
                if (ignoreTeams.getValue() && PlayerUtil.isTeam(entityPlayer, scoreboardCheckTeam.getValue(), checkArmorColor.getValue())) continue;
                if (dist > range.getValue().doubleValue()) continue;
                if (fov != 360 && !PlayerUtil.fov(fov, entityPlayer)) continue;
                if (!visibilityCheck.getValue() && RayCastUtil.rayCast(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), dist) != null) continue;
                double curFov = Math.abs(PlayerUtil.getFov(entityPlayer.posX, entityPlayer.posZ));
                if (curFov < targetFov) {
                    target = entityPlayer;
                    targetFov = curFov;
                }
            }
        }
        return target;
    }
    
	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) {
	        return true;
	    }

	    if (requiredWeapon.getValue() && !PlayerUtil.isHoldingWeapon()) {
	        return true;
	    }

	    if (requiredClick.getValue() && !PlayerUtil.isClicking()) {
	        return true;
	    }

	    if (mouseOverEntity.getValue() 
	        && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectType.ENTITY)) {
	        return true;
	    }
	    
	    if (checkBlock.getValue() && mc.playerController.isHittingBlock) {
	    	return true;
	    }

	    return false;
	}
    
    private @NotNull Doble<Doble<Float, Float>, Doble<Float, Float>> getRotation(@NotNull AxisAlignedBB boundingBox) {
        AxisAlignedBB box = boundingBox.expand(-0.05, -0.9, -0.1).offset(0, 0.405, 0);

        float yaw1 = getYaw(mc.player, new Vec3(box.minX, 0, box.minZ));
        float yaw2 = getYaw(mc.player, new Vec3(box.maxX, 0, box.maxZ));

        float pitch1 = getPitch(mc.player, new Vec3(0, box.minY, 0));
        float pitch2 = getPitch(mc.player, new Vec3(0, box.maxY, 0));

        return new Doble<>(
                sortYaw(yaw1, yaw2),
                new Doble<>(Math.min(pitch1, pitch2), Math.max(pitch1, pitch2))
        );
    }

    private float getYaw(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        return from.rotationYaw +
                MathHelper.wrapAngleTo180_float(
                        (float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX)) - 100f - from.rotationYaw
                );
    }


    private float getPitch(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        double diffX = pos.xCoord - from.posX;
        double diffY = pos.yCoord - (from.posY + from.getEyeHeight());
        double diffZ = pos.zCoord - from.posZ;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return from.rotationPitch + MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - from.rotationPitch);
    }

    private float rotMove(double target, double current, double diff) {
        return rotMove(target, current, diff, 0.8F);
    }

    private float rotMove(double target, double current, double diff, Float gcd) {
        float delta;
        if ((float) target > (float) current) {
            float dist1 = (float) target - (float) current;
            float dist2 = (float) current + 360 - (float) target;
            if (dist1 > dist2) {
                delta = -(float) current - 360 + (float) target;
            } else {
                delta = dist1;
            }
        } else if ((float) target < (float) current) {
            float dist1 = (float) current - (float) target;
            float dist2 = (float) target + 360 - (float) current;
            if (dist1 > dist2) {
                delta = (float) current + 360 + (float) target;
            } else {
                delta = -dist1;
            }
        } else {
            return (float) current;
        }

        delta = normalize(delta, -180, 180);
        if (gcd != null)
            delta = (float) (Math.round(delta / gcd) * gcd);

        if (Math.abs(delta) < 0.1 * Math.random() + 0.1) {
            return (float) current;
        } else if (Math.abs(delta) <= (float) diff) {
            return (float) current + delta;
        } else {
            if (delta < 0) {
                return (float) current - (float) diff;
            } else if (delta > 0) {
                return (float) current + (float) diff;
            } else {
                return (float) current;
            }
        }
    }

    private float normalize(float yaw, float min, float max) {
        yaw %= 360.0F;
        if (yaw >= max) {
            yaw -= 360.0F;
        }
        if (yaw < min) {
            yaw += 360.0F;
        }

        return yaw;
    }
    
    @Contract("_, _ -> new")
    private @NotNull Doble<Float, Float> sortYaw(final float yaw1, final float yaw2) {
        final float fixedYaw1 = fixYaw(yaw1);
        final float fixedYaw2 = fixYaw(yaw2);

        if (fixedYaw1 < fixedYaw2) {
            return new Doble<>(yaw1, yaw2);
        } else {
            return new Doble<>(yaw2, yaw1);
        }
    }

    private float fixYaw(float yaw) {
        while (yaw < 0) {
            yaw += 360;
        }
        while (yaw > 360) {
            yaw -= 360;
        }
        return yaw;
    }
}