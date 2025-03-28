package cc.unknown.util.player;

import java.util.Arrays;

import cc.unknown.Sakura;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.MoveEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@UtilityClass
public class MoveUtil implements Accessor {

    public final double WALK_SPEED = 0.221;
    public final double MOD_SPRINTING = 1.3F;
    public final double MOD_SNEAK = 0.3F;
    public final double MOD_WEB = 0.105 / WALK_SPEED;
    public final double JUMP_HEIGHT = 0.42F;
    public final double BUNNY_FRICTION = 159.9F;

    public final double MOD_SWIM = 0.115F / WALK_SPEED;
    
    public final double[] MOD_DEPTH_STRIDER = {
            1.0F,
            0.1645F / MOD_SWIM / WALK_SPEED,
            0.1995F / MOD_SWIM / WALK_SPEED,
            1.0F / MOD_SWIM,
    };

    public final double HEAD_HITTER_MOTION = -0.0784000015258789;

    public boolean isMoving() {
        return mc.player != null && mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
    }
    
    public boolean isMoving(EntityLivingBase player) {
        return player != null && (player.moveForward != 0F || player.moveStrafing != 0F);
    }
    
    public boolean isMovingStraight() {
        float direction = getRawDirection() + 180;
        float movingYaw = Math.round(direction / 45) * 45;
        return movingYaw % 90 == 0f;
    }
    
    public double getBaseMoveSpeed(EntityPlayer player) {
        double baseSpeed = 0.2873;
        if (player.isPotionActive(Potion.moveSpeed)) {
            int amplifier = player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public float getRawDirection() {
        return getRawDirectionRotation(mc.player.rotationYaw, mc.player.moveStrafing, mc.player.moveForward);
    }
    
    public float getRawDirectionRotation(float yaw, float pStrafe, float pForward) {
        float rotationYaw = yaw;

        if (pForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (pForward < 0F)
            forward = -0.5F;
        else if (pForward > 0F)
            forward = 0.5F;

        if (pStrafe > 0F)
            rotationYaw -= 90F * forward;

        if (pStrafe < 0F)
            rotationYaw += 90F * forward;

        return rotationYaw;
    }

    public boolean enoughMovementForSprinting() {
        return Math.abs(mc.player.moveForward) >= 0.8F || Math.abs(mc.player.moveStrafing) >= 0.8F;
    }

    public boolean canSprint(final boolean legit) {
        return (legit ? mc.player.moveForward >= 0.8F
                && !mc.player.isCollidedHorizontally
                && (mc.player.getFoodStats().getFoodLevel() > 6 || mc.player.capabilities.allowFlying)
                && !mc.player.isPotionActive(Potion.blindness)
                && !mc.player.isUsingItem()
                && !mc.player.isSneaking()
                : enoughMovementForSprinting());
    }
    
    public double getDirectionWrappedTo90() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.moveForward < 0F && mc.player.moveStrafing == 0F) rotationYaw += 180F;

        final float forward = 1F;

        if (mc.player.moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (mc.player.moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public double movementDelta() {
        return Math.hypot(mc.player.posX - mc.player.prevPosX, mc.player.posZ - mc.player.prevPosZ);
    }

    public double speedPotionAmp(final double amp) {
        return mc.player.isPotionActive(Potion.moveSpeed) ? ((mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * amp) : 0;
    }

    public double jumpMotion() {
        return jumpBoostMotion(JUMP_HEIGHT);
    }

    public double jumpBoostMotion(final double motionY) {
        if (mc.player.isPotionActive(Potion.jump)) {
            return motionY + (mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        return motionY;
    }

    public int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.player);
    }

    public float fallDistanceForDamage() {
        float fallDistanceReq = 3;

        if (mc.player.isPotionActive(Potion.jump)) {
            int amplifier = mc.player.getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistanceReq += (float) (amplifier + 1);
        }

        return fallDistanceReq;
    }

    public double roundToGround(final double posY) {
        return Math.round(posY / 0.015625) * 0.015625;
    }

    public double predictedMotion(final double motion) {
        return (motion - 0.08) * 0.98F;
    }

    public void forward(final double speed) {
        final double yaw = direction();

        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }

    public double predictedMotion(final double motion, final int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    public double getbaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public double getAllowedHorizontalDistance() {
        double horizontalDistance;
        boolean useBaseModifiers = false;

        if (mc.player.isInWeb) {
            horizontalDistance = MOD_WEB * WALK_SPEED;
        } else if (PlayerUtil.inLiquid()) {
            horizontalDistance = MOD_SWIM * WALK_SPEED;

            final int depthStriderLevel = depthStriderLevel();
            if (depthStriderLevel > 0) {
                horizontalDistance *= MOD_DEPTH_STRIDER[depthStriderLevel];
                useBaseModifiers = true;
            }

        } else if (mc.player.isSneaking()) {
            horizontalDistance = MOD_SNEAK * WALK_SPEED;
        } else {
            horizontalDistance = WALK_SPEED;
            useBaseModifiers = true;
        }

        if (useBaseModifiers) {
            if (canSprint(false)) {
                horizontalDistance *= MOD_SPRINTING;
            }

            final Scaffold scaffold = Sakura.instance.getModuleManager().get(Scaffold.class);

            if (mc.player.isPotionActive(Potion.moveSpeed) && mc.player.getActivePotionEffect(Potion.moveSpeed).duration > 0) {
                horizontalDistance *= 1 + (0.2 * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.player.isPotionActive(Potion.moveSlowdown)) {
                horizontalDistance = 0.29;
            }
        }

        return horizontalDistance;
    }

    public void jumpRandom(final double motion) {
        mc.player.motionY = motion + (Math.random() / 500);
    }

    public void strafe() {
        strafe(speed(), mc.player);
    }
    
    public double strafe(EntityPlayer player) {
        return Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
    }

    public void strafe(Entity entity) {
        strafe(speed(), entity);
    }

    public void strafe(final double speed) {
        strafe(speed, mc.player);
    }

    public void strafe(final double speed, Entity entity) {
        if (!isMoving()) {
            return;
        }

        final double yaw = direction();
        entity.motionX = -MathHelper.sin((float) yaw) * speed;
        entity.motionZ = MathHelper.cos((float) yaw) * speed;
    }

    public void strafe(final double speed, float yaw) {
        if (!isMoving()) {
            return;
        }

        yaw = (float) Math.toRadians(yaw);
        mc.player.motionX = -MathHelper.sin(yaw) * speed;
        mc.player.motionZ = MathHelper.cos(yaw) * speed;
    }

    public void stop() {
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
    }
    
    public void keybindStop() {
    	mc.gameSettings.keyBindForward.pressed = false;
    	mc.gameSettings.keyBindBack.pressed = false;
    	mc.gameSettings.keyBindLeft.pressed = false;
    	mc.gameSettings.keyBindRight.pressed = false;
    }

    public double direction() {
        float rotationYaw = mc.player.movementYaw;

        if (mc.player.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 90 * forward;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 90 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public double direction(MoveInputEvent moveInputEvent) {
        float rotationYaw = mc.player.movementYaw;

        if (moveInputEvent.getForward() < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (moveInputEvent.getForward() < 0) {
            forward = -0.5F;
        } else if (moveInputEvent.getForward() > 0) {
            forward = 0.5F;
        }

        if (moveInputEvent.getStrafe() > 0) {
            rotationYaw -= 70 * forward;
        }

        if (moveInputEvent.getStrafe() < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public double direction(float inputForward, float inputStrafe) {
        float rotationYaw = mc.player.movementYaw;

        if (inputForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (inputForward < 0) {
            forward = -0.5F;
        } else if (inputForward > 0) {
            forward = 0.5F;
        }

        if (inputStrafe > 0) {
            rotationYaw -= 70 * forward;
        }

        if (inputStrafe < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public double wrappedDirection() {
        float rotationYaw = mc.player.movementYaw;

        if (mc.player.moveForward < 0 && mc.player.moveStrafing == 0) {
            rotationYaw += 180;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 90;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 90;
        }

        return Math.toRadians(rotationYaw);
    }

    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public double speed() {
        return Math.hypot(mc.player.motionX, mc.player.motionZ);
    }

    public void setSpeedMoveEvent(final MoveEvent moveEvent, final double moveSpeed) {
        setSpeedMoveEvent(moveEvent, moveSpeed, mc.player.movementYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward);
    }

    public void setSpeedMoveEvent(final MoveEvent moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45.0F : 45.0F);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45.0F : -45.0F);
            }

            strafe = 0.0D;

            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }

        final double mx = Math.cos(Math.toRadians((yaw + 180.0F)));
        final double mz = Math.sin(Math.toRadians((yaw + 180.0F)));
        moveEvent.setPosX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setPosZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public void fixMovement(final MoveInputEvent event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.player.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public double getMCFriction() {
        float f = 0.91F;

        if (mc.player.onGround) {
            f = mc.world.getBlockState(new BlockPos(MathHelper.floor_double(mc.player.posX), MathHelper.floor_double(mc.player.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.player.posZ))).getBlock().slipperiness * 0.91F;
        }

        return f;
    }

    public double[] moveFlying(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        float friction = 0.02f;
        final float playerWalkSpeed = mc.player.getAIMoveSpeed();

        if (onGround) {
            final float f4 = 0.6f * 0.91f;
            final float f = 0.16277136F / (f4 * f4 * f4);
            friction = playerWalkSpeed / 2.0f * f;
        }

        if (sprinting) {
            friction = (float) ((double) friction + ((onGround) ? (playerWalkSpeed / 2.0f) : 0.02f) * 0.3D);
        }

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            final float f1 = MathHelper.sin(yaw * (float) Math.PI * 2 / 180.0F);
            final float f2 = MathHelper.cos(yaw * (float) Math.PI * 2 / 180.0F);

            final double motionX = (strafe * f2 - forward * f1);
            final double motionZ = (forward * f2 + strafe * f1);

            return new double[]{motionX, motionZ};
        }

        return null;
    }

    public Vector2d moveFlyingVec(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        double[] values = moveFlying(strafe, forward, onGround, yaw, sprinting);
        if (values == null) return null;
        return new Vector2d(values[0], values[1]);
    }

    public Double moveFlyingSpeed(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        double[] speed = moveFlying(strafe, forward, onGround, yaw, sprinting);

        if (speed == null) return null;

        return Math.hypot(speed[0], speed[1]);
    }

    public Double moveFlyingSpeed(final boolean sprinting) {
        double[] speed = moveFlying(0.98f, 0.98f, mc.player.onGround, 180, sprinting);

        if (speed == null) return null;

        return Math.hypot(speed[0], speed[1]);
    }

    public void partialStrafeMax(double maxStrafe) {
        partialStrafeMax(maxStrafe, mc.player);
    }

    public void partialStrafeMax(double maxStrafe, Entity entity) {
        double motionX = entity.motionX;
        double motionZ = entity.motionZ;

        strafe(entity);

        entity.motionX = motionX + Math.max(-maxStrafe, Math.min(maxStrafe, entity.motionX - motionX));
        entity.motionZ = motionZ + Math.max(-maxStrafe, Math.min(maxStrafe, entity.motionZ - motionZ));
    }

    public void partialStrafePercent(double percentage) {
        percentage /= 100;
        percentage = Math.min(1, Math.max(0, percentage));

        double motionX = mc.player.motionX;
        double motionZ = mc.player.motionZ;

        strafe();

        mc.player.motionX = motionX + (mc.player.motionX - motionX) * percentage;
        mc.player.motionZ = motionZ + (mc.player.motionZ - motionZ) * percentage;
    }

    public double moveMaxFlying(final boolean onGround) {
        float friction = 0.02f;
        final float playerWalkSpeed = mc.player.getAIMoveSpeed() / 2;
        float strafe = 0.98f;
        float forward = 0.98f;
        float yaw = 180;

        if (onGround) {
            final float f4 = 0.6f * 0.91f;
            final float f = 0.16277136F / (f4 * f4 * f4);
            friction = playerWalkSpeed * f;
        }

        friction = (float) ((double) friction + ((onGround) ? (playerWalkSpeed) : 0.02f) * 0.3D);

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            final float f1 = MathHelper.sin(yaw * (float) Math.PI * 2 / 180.0F);
            final float f2 = MathHelper.cos(yaw * (float) Math.PI * 2 / 180.0F);

            final double motionX = (strafe * f2 - forward * f1);
            final double motionZ = (forward * f2 + strafe * f1);

            return Math.hypot(motionX, motionZ);
        }

        return 0;
    }

    public float simulationStrafeAngle(float currentMoveYaw, float maxAngle) {
        float workingYaw;
        float target = (float) Math.toDegrees(direction());

        if (Math.abs(currentMoveYaw - target) <= maxAngle) {
            currentMoveYaw = target;
        } else if (currentMoveYaw > target) {
            currentMoveYaw -= maxAngle;
        } else {
            currentMoveYaw += maxAngle;
        }

        workingYaw = currentMoveYaw;

        strafe(speed(), workingYaw);

        return workingYaw;
    }

    public float simulationStrafe(float currentMoveYaw) {
        double moveFlying = 0.02599999835384377;
        double friction = 0.9100000262260437;

        double lastDeltaX = mc.player.lastMotionX * friction;
        double lastDeltaZ = mc.player.lastMotionZ * friction;

        float workingYaw = currentMoveYaw;

        float target = (float) Math.toDegrees(direction());

        for (int i = 0; i <= 360; i++) {

            strafe(speed(), currentMoveYaw);

            double deltaX = Math.abs(mc.player.motionX);
            double deltaZ = Math.abs(mc.player.motionZ);

            double minDeltaX = lastDeltaX - moveFlying;
            double minDeltaZ = lastDeltaZ - moveFlying;

            if (currentMoveYaw == target || (deltaX < minDeltaX || deltaZ < minDeltaZ)) {
                break;
            }

            workingYaw = currentMoveYaw;

            if (Math.abs(currentMoveYaw - target) <= 1) {
                currentMoveYaw = target;
            } else if (currentMoveYaw > target) {
                currentMoveYaw -= 1;
            } else {
                currentMoveYaw += 1;
            }
        }

        strafe(speed(), workingYaw);

        return workingYaw;
    }

    public void preventDiagonalSpeed() {
        KeyBinding[] gameSettings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};

        final int[] down = {0};

        Arrays.stream(gameSettings).forEach(keyBinding -> down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0));

        boolean active = down[0] == 1;

        if (active) return;

        final double groundIncrease = (0.1299999676734952 - 0.12739998266255503) + 1E-7 - 1E-8;
        final double airIncrease = (0.025999999334873708 - 0.025479999685988748) - 1E-8;
        final double increase = mc.player.onGround ? groundIncrease : airIncrease;

        moveFlying(-increase);
    }

    public void useDiagonalSpeed() {
        KeyBinding[] gameSettings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};

        final int[] down = {0};

        Arrays.stream(gameSettings).forEach(keyBinding -> {
            down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0);
        });

        boolean active = down[0] == 1;

        if (!active) return;

        final double groundIncrease = (0.1299999676734952 - 0.12739998266255503) + 1E-7 - 1E-8;
        final double airIncrease = (0.025999999334873708 - 0.025479999685988748) - 1E-8;
        final double increase = mc.player.onGround ? groundIncrease : airIncrease;

        moveFlying(increase);
    }
    
    public float getStrafeYaw(float forward, float strafe) {
        float yaw = mc.player.rotationYaw;

        if((forward == 0) && (strafe == 0))
            return yaw;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafe > 0.0f)
            yaw -= strafingYaw;
        else if (strafe < 0.0f)
            yaw += strafingYaw;

        return yaw;
    }
    
    public Vec3 getPredictedPos(float forward, float strafe) {
        strafe *= 0.98F;
        forward *= 0.98F;
        float f4 = 0.91F;
        double motionX = mc.player.motionX;
        double motionZ = mc.player.motionZ;
        double motionY = mc.player.motionY;
        boolean isSprinting = mc.player.isSprinting();

        if (mc.player.isJumping && mc.player.onGround) {
            motionY = mc.player.getJumpUpwardsMotion();
            if (mc.player.isPotionActive(Potion.jump)) {
                motionY += (float)(mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            if (isSprinting) {
                float f = mc.player.rotationYaw * (float) (Math.PI / 180.0);
                motionX -= MathHelper.sin(f) * 0.2F;
                motionZ += MathHelper.cos(f) * 0.2F;
            }
        }

        if (mc.player.onGround) {
            f4 = mc.player
                    .worldObj
                    .getBlockState(
                            new BlockPos(
                                    MathHelper.floor_double(mc.player.posX),
                                    MathHelper.floor_double(mc.player.getEntityBoundingBox().minY) - 1,
                                    MathHelper.floor_double(mc.player.posZ)
                            )
                    )
                    .getBlock()
                    .slipperiness
                    * 0.91F;
        }

        float f3 = 0.16277136F / (f4 * f4 * f4);
        float friction;
        if (mc.player.onGround) {
            friction = mc.player.getAIMoveSpeed() * f3;
            if (mc.player == mc.player
                    && mc.player.isSprinting()) {
                friction = 0.12999998F;
            }
        } else {
            friction = mc.player.jumpMovementFactor;
        }

        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            forward *= f;
            float f1 = MathHelper.sin(mc.player.rotationYaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(mc.player.rotationYaw * (float) Math.PI / 180.0F);
            motionX += strafe * f2 - forward * f1;
            motionZ += forward * f2 + strafe * f1;
        }

        f4 = 0.91F;
        if (mc.player.onGround) {
            f4 = mc.player
                    .worldObj
                    .getBlockState(
                            new BlockPos(
                                    MathHelper.floor_double(mc.player.posX),
                                    MathHelper.floor_double(mc.player.getEntityBoundingBox().minY) - 1,
                                    MathHelper.floor_double(mc.player.posZ)
                            )
                    )
                    .getBlock()
                    .slipperiness
                    * 0.91F;
        }

        motionY *= 0.98F;
        motionX *= f4;
        motionZ *= f4;
        return new Vec3(motionX, motionY, motionZ);
    }

    public void moveFlying(double increase) {
        if (!isMoving()) return;
        final double yaw = direction();
        mc.player.motionX += -MathHelper.sin((float) yaw) * increase;
        mc.player.motionZ += MathHelper.cos((float) yaw) * increase;
    }
}
