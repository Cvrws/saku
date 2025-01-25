package cc.unknown.module.impl.world;

import com.ibm.icu.impl.duration.impl.Utils;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Bridge Assist", description = ">:3c", category = Category.WORLD)
public class BridgeAssist extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Godbridge"))
            .add(new SubMode("Moonwalk"))
            .add(new SubMode("Breezily"))
            .add(new SubMode("Normal"))
            .setDefault("Godbridge");

    private final NumberValue delay = new NumberValue("Wait Time", this, 70, 0, 200, 1);
    private final BooleanValue holdShift = new BooleanValue("Only work when holding shift", this, false);
    private final NumberValue assistRange = new NumberValue("Assist Range", this, 10, 1, 40, 1);
    private final NumberValue assistSpeed = new NumberValue("Assist Speed", this, 4, 1, 100, 1);

    private boolean waitingForAim;
    private boolean gliding;
    private long startWaitTime;
    
    private final float[] godbridgePos = {75.6f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private final float[] moonwalkPos = {79.6f, -340, -290, -250, -200, -160, -110, -70, -20, 0, 20, 70, 110, 160, 200, 250, 290, 340};
    private final float[] breezilyPos = {79.9f, -360, -270, -180, -90, 0, 90, 180, 270, 360};
    private final float[] normalPos = {78f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private double speedYaw, speedPitch;
    private float targetYaw, targetPitch;

    @Override
    public void onEnable() {
        this.waitingForAim = false;
        this.gliding = false;
        super.onEnable();
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (!isInGame() || !(PlayerUtil.isOverAir() && event.isOnGround())) {
            return;
        }

        if (holdShift.getValue() && !event.isSneaking()) {
            return;
        }

        if (gliding) {
            handleGliding(event);
        } else {
            handleAiming(event);
        }
    };

    private void handleGliding(PreMotionEvent event) {
        float currentYaw = normalizeAngle(event.getYaw());
        float currentPitch = normalizeAngle(event.getPitch());

        double deltaYaw = Math.abs(currentYaw - speedYaw);
        double deltaPitch = Math.abs(currentPitch - speedPitch);

        if (deltaYaw <= speedYaw) {
            event.setYaw(targetYaw);
        }

        if (deltaPitch <= speedPitch) {
            event.setPitch(targetPitch);
        }

        adjustRotation(event, currentYaw, currentPitch);
    }

    private void handleAiming(PreMotionEvent event) {
        if (!waitingForAim) {
            waitingForAim = true;
            startWaitTime = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() - startWaitTime < delay.getValue().intValue()) {
            return;
        }

        float currentYaw = normalizeAngle(event.getYaw());
        float currentPitch = normalizeAngle(event.getPitch());
        float range = assistRange.getValue().floatValue();
        int speed = assistSpeed.getValue().intValue();

        float[] positions = getModePositions(mode.getValue().getName());
        if (positions != null && isInRange(positions[0], currentPitch, range)) {
            for (int i = 1; i < positions.length; i++) {
                if (isInRange(positions[i], currentYaw, range)) {
                    aimAt(positions[0], positions[i], currentYaw, currentPitch, speed);
                    waitingForAim = false;
                    return;
                }
            }
        }
        waitingForAim = false;
    }

    private void adjustRotation(PreMotionEvent event, float yaw, float pitch) {
        if (yaw < targetYaw) {
            event.setYaw(yaw + (float) speedYaw);
        } else if (yaw > targetYaw) {
            event.setYaw(yaw - (float) speedYaw);
        }

        if (pitch > targetPitch) {
            event.setPitch(pitch - (float) speedPitch);
        }

        if (yaw == targetYaw && pitch == targetPitch) {
            gliding = false;
            waitingForAim = false;
        }
    }

    private float normalizeAngle(float angle) {
        return angle % 360;
    }

    private boolean isInRange(float target, float current, float range) {
        return Math.abs(target - current) <= range;
    }

    private float[] getModePositions(String modeName) {
        switch (modeName) {
            case "Godbridge":
                return godbridgePos;
            case "Moonwalk":
                return moonwalkPos;
            case "Breezily":
                return breezilyPos;
            case "Normal":
                return normalPos;
            default:
                return null;
        }
    }

    private void aimAt(float targetPitch, float targetYaw, float currentYaw, float currentPitch, double speed) {
        float[] adjusted = getGCDRotations(new float[]{targetYaw, targetPitch}, new float[]{currentYaw, currentPitch});
        mc.player.rotationYaw = maxAngleChange(currentYaw, adjusted[0], (float) speed);
        mc.player.rotationPitch = maxAngleChange(currentPitch, adjusted[1], (float) speed);
    }

    private float[] getGCDRotations(float[] rotations, float[] prevRots) {
        float yawDiff = rotations[0] - prevRots[0];
        float pitchDiff = rotations[1] - prevRots[1];
        double gcd = getGCD();
        rotations[0] -= yawDiff % gcd;
        rotations[1] -= pitchDiff % gcd;
        return rotations;
    }

    private double getGCD() {
        float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float multiplier = sensitivity * sensitivity * sensitivity * 8.0F;
        return multiplier * 0.15D;
    }

    private float maxAngleChange(float prev, float target, float maxTurn) {
        float diff = MathHelper.wrapAngleTo180_float(target - prev);
        return prev + MathHelper.clamp_float(diff, -maxTurn, maxTurn);
    }
}