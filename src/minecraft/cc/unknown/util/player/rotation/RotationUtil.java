package cc.unknown.util.player.rotation;

import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.util.structure.geometry.Vector3d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@UtilityClass
public class RotationUtil implements Accessor {

	public Vector2f calculate(final Vector3d from, final Vector3d to) {
		final Vector3d diff = to.subtract(from);
		final double distance = Math.hypot(diff.getX(), diff.getZ());
		final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (float) (180.0F / Math.PI)) - 90.0F;
		final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * (float) (180.0F / Math.PI)));
		return new Vector2f(yaw, pitch);
	}

	public Vector2f calculate(final Entity entity) {
		return calculate(entity.getCustomPositionVector().add(0,
				Math.max(0, Math.min(mc.player.posY - entity.posY + mc.player.getEyeHeight(),
						(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)),
				0));
	}
	
	public Vec3 getNearestPointOnBox(AxisAlignedBB hitbox, Vec3 playerPos) {
	    double nearestX = MathHelper.clamp_double(playerPos.xCoord, hitbox.minX, hitbox.maxX);
	    double nearestY = MathHelper.clamp_double(playerPos.yCoord, hitbox.minY, hitbox.maxY);
	    double nearestZ = MathHelper.clamp_double(playerPos.zCoord, hitbox.minZ, hitbox.maxZ);
	    
	    return new Vec3(nearestX, nearestY, nearestZ);
	}
	
    public float calculate(final double n, final double n2) {
        return (float) (Math.atan2(n - mc.player.posX, n2 - mc.player.posZ) * 57.295780181884766 * -1.0);
    }
	
    public double distanceFromYaw(final Entity entity) {
        return Math.abs(MathHelper.wrapAngleTo180_double(calculate(entity.posX, entity.posZ) - mc.player.rotationYaw));
    }

	public float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
		final Minecraft mc = Minecraft.getInstance();

		final float yaw = rotations[0];
		final float pitch = rotations[1];

		final float lastYaw = lastRotations[0];
		final float lastPitch = lastRotations[1];

		final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float gcd = f * f * f * 1.2F;

		final float deltaYaw = yaw - lastYaw;
		final float deltaPitch = pitch - lastPitch;

		final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
		final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

		final float fixedYaw = lastYaw + fixedDeltaYaw;
		final float fixedPitch = lastPitch + fixedDeltaPitch;

		return new float[] { fixedYaw, fixedPitch };
	}

	public Vector2f calculate(final Entity entity, final boolean adaptive, final double range) {
	    if (entity == null) {
	        return null;
	    }
	    
	    Vector2f normalRotations = calculate(entity);
	    if (normalRotations == null) {
	        return null;
	    }

	    MovingObjectPosition result = RayCastUtil.rayCast(normalRotations, range);
	    if (result == null) {
	        return normalRotations;
	    }

	    if (!adaptive || result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
	        return normalRotations;
	    }

	    for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25 + Math.random() * 0.1) {
	        for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
	            for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
	                Vector3d customPosition = entity.getCustomPositionVector();
	                if (customPosition == null) {
	                    continue;
	                }

	                Vector2f adaptiveRotations = calculate(customPosition.add(
	                    (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
	                    (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
	                    (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));

	                if (adaptiveRotations == null) {
	                    continue;
	                }

	                MovingObjectPosition adaptiveResult = RayCastUtil.rayCast(adaptiveRotations, range);
	                if (adaptiveResult != null && adaptiveResult.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
	                    return adaptiveRotations;
	                }
	            }
	        }
	    }
	    return normalRotations;
	}

	public Vector2f calculate(final Vec3 to, final EnumFacing enumFacing) {
		return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
	}

	public Vector2f calculate(final Vec3 to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0),
				new Vector3d(to.xCoord, to.yCoord, to.zCoord));
	}

	public Vector2f calculate(final BlockPos to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0),
				new Vector3d(to.getX(), to.getY(), to.getZ()).add(0.5, 0.5, 0.5));
	}

	public Vector2f calculate(final Vector3d to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0), to);
	}

	public Vector2f calculate(final Vector3d position, final EnumFacing enumFacing) {
		double x = position.getX() + 0.5D;
		double y = position.getY() + 0.5D;
		double z = position.getZ() + 0.5D;

		x += (double) enumFacing.getDirectionVec().getX() * 0.5D;
		y += (double) enumFacing.getDirectionVec().getY() * 0.5D;
		z += (double) enumFacing.getDirectionVec().getZ() * 0.5D;
		return calculate(new Vector3d(x, y, z));
	}

	public Vector2f applySensitivityPatch(final Vector2f rotation) {
		final Vector2f previousRotation = mc.player.getPreviousRotation();
		final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F
				+ 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation.x
				+ (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
		final float pitch = previousRotation.y
				+ (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
		return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
	}

	public Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
		final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F
				+ 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation.x
				+ (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
		final float pitch = previousRotation.y
				+ (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
		return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
	}

	public Vector2f relateToPlayerRotation(final Vector2f rotation) {
		final Vector2f previousRotation = mc.player.getPreviousRotation();
		final float yaw = previousRotation.x + MathHelper.wrapAngleTo180_float(rotation.x - previousRotation.x);
		final float pitch = MathHelper.clamp_float(rotation.y, -90, 90);
		return new Vector2f(yaw, pitch);
	}

	public Vector2f resetRotation(final Vector2f rotation) {
		if (rotation == null) {
			return null;
		}

		final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.player.rotationYaw - rotation.x);
		final float pitch = mc.player.rotationPitch;
		return new Vector2f(yaw, pitch);
	}

	public Vector2f move(final Vector2f targetRotation, final double speed) {
		return move(RotationHandler.lastRotations, targetRotation, speed);
	}

	public Vector2f move(final Vector2f lastRotation, final Vector2f targetRotation, double speed) {
		if (speed != 0) {

			double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
			final double deltaPitch = (targetRotation.y - lastRotation.y);

			final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
			final double distributionYaw = Math.abs(deltaYaw / distance);
			final double distributionPitch = Math.abs(deltaPitch / distance);

			final double maxYaw = speed * distributionYaw;
			final double maxPitch = speed * distributionPitch;

			final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
			final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

			return new Vector2f(moveYaw, movePitch);
		}

		return new Vector2f(0, 0);
	}

	public Vector2f smooth(final Vector2f targetRotation, final double speed) {
		return smooth(RotationHandler.lastRotations, targetRotation, speed);
	}

	public Vector2f smooth(final Vector2f lastRotation, final Vector2f targetRotation, final double speed) {
		float yaw = targetRotation.x;
		float pitch = targetRotation.y;
		final float lastYaw = lastRotation.x;
		final float lastPitch = lastRotation.y;

		if (speed != 0) {
			Vector2f move = move(targetRotation, speed);

			yaw = lastYaw + move.x;
			pitch = lastPitch + move.y;

			for (int i = 1; i <= (int) (Minecraft.getDebugFPS() / 20f + Math.random() * 10); ++i) {

				if (Math.abs(move.x) + Math.abs(move.y) > 0.0001) {
					yaw += (Math.random() - 0.5) / 1000;
					pitch -= Math.random() / 200;
				}

				/*
				 * Fixing GCD
				 */
				final Vector2f rotations = new Vector2f(yaw, pitch);
				final Vector2f fixedRotations = applySensitivityPatch(rotations);

				/*
				 * Setting rotations
				 */
				yaw = fixedRotations.x;
				pitch = Math.max(-90, Math.min(90, fixedRotations.y));
			}
		}

		return new Vector2f(yaw, pitch);
	}
	
    public double nearestRotation(final AxisAlignedBB bb) {
        final Vec3 eyes = mc.player.getPositionEyes(1F);

        Vec3 vecRotation3d = null;

        for(double xSearch = 0D; xSearch <= 1D; xSearch += 0.05D) {
            for (double ySearch = 0D; ySearch < 1D; ySearch += 0.05D) {
                for (double zSearch = 0D; zSearch <= 1D; zSearch += 0.05D) {
                    final Vec3 vec3 = new Vec3(
                            bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch,
                            bb.minZ + (bb.maxZ - bb.minZ) * zSearch
                    );
                    final double vecDist = eyes.squareDistanceTo(vec3);

                    if (vecRotation3d == null || eyes.squareDistanceTo(vecRotation3d) > vecDist) {
                        vecRotation3d = vec3;
                    }
                }
            }
        }
        return vecRotation3d.distanceTo(eyes);
    }
    
    public double getDistanceToEntityBoxFromPosition(double posX, double posY, double posZ, Entity entity) {
        Vec3 eyes = mc.player.getPositionEyes(1.0F);
        Vec3 pos = getBestHitVec(entity);
        double xDist = Math.abs(pos.xCoord - posX);
        double yDist = Math.abs(pos.yCoord - posY + (double)mc.player.getEyeHeight());
        double zDist = Math.abs(pos.zCoord - posZ);
        return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
     }
    
    public double getDistanceToEntityBox(Entity entity) {
        Vec3 eyes = mc.player.getPositionEyes(1.0F);
        Vec3 pos = getBestHitVec(entity);
        double xDist = Math.abs(pos.xCoord - eyes.xCoord);
        double yDist = Math.abs(pos.yCoord - eyes.yCoord);
        double zDist = Math.abs(pos.zCoord - eyes.zCoord);
        return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
    }
    
    public Vec3 getBestHitVec(Entity entity) {
        Vec3 positionEyes = mc.player.getPositionEyes(1.0F);
        float f11 = entity.getCollisionBorderSize();
        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand((double)f11, (double)f11, (double)f11);
        double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey - 0.4D, ez);
    }
    
    public float getRotationDifference(final Entity entity,final Entity entity2) {
        float[] target = getRotations(entity.posX,entity.posY + entity.getEyeHeight(),entity.posZ);
        float[] target2 = getRotations(entity2.posX,entity2.posY + entity2.getEyeHeight(),entity2.posZ);
        return (float) Math.hypot(Math.abs(getAngleDifference(target[0], target2[0])), Math.abs(target[1] - target2[1]));
    }
    
    public float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        return getRotations(blockPos, enumFacing, 0.25, 0.25);
    }

    public float[] getRotations(BlockPos blockPos, EnumFacing enumFacing, double xz, double y) {
        double d = blockPos.getX() + 0.5 - mc.player.posX + enumFacing.getFrontOffsetX() * xz;
        double d2 = blockPos.getZ() + 0.5 - mc.player.posZ + enumFacing.getFrontOffsetZ() * xz;
        double d3 = mc.player.posY + mc.player.getEyeHeight() - blockPos.getY() - enumFacing.getFrontOffsetY() * y;
        double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f = (float) (Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapAngleTo180_float(f), f2};
    }

    public float[] getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public float[] getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
    }

    public float[] getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }
    
    public float getAngleDifference(float a, float b) {
        return MathHelper.wrapAngleTo180_float(a - b);
    }
    
    public double isBestTarget(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final double distance = mc.player.getDistanceToEntity(entity);
            final double health = ((EntityLivingBase)entity).getHealth();
            double hurtTime = 10.0;
            if (entity instanceof EntityPlayer) {
                hurtTime = ((EntityPlayer)entity).hurtTime;
            }
            return distance * 2.0 + health + hurtTime * 4.0;
        }
        return 1000.0;
    }
    
    public double isUltimate(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final double distance = mc.player.getDistanceToEntity(entity);
            final double hurtTime = ((EntityLivingBase)entity).hurtTime * 6;
            return hurtTime + distance;
        }
        return 1000.0;
    }
}