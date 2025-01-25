package cc.unknown.module.impl.world;

import java.util.HashMap;
import java.util.Map;

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
	private final float[] godbridgePos = { 75.6f, -315, -225, -135, -45, 0, 45, 135, 225, 315 };
	private final float[] moonwalkPos = { 79.6f, -340, -290, -250, -200, -160, -110, -70, -20, 0, 20, 70, 110, 160, 200, 250, 290, 340 };
	private final float[] breezilyPos = { 79.9f, -360, -270, -180, -90, 0, 90, 180, 270, 360 };
	private final float[] normalPos = { 78f, -315, -225, -135, -45, 0, 45, 135, 225, 315 };
	private float speedYaw, speedPitch;
	private float waitingForYaw, waitingForPitch;

	@Override
	public void onEnable() {
		waitingForAim = false;
		gliding = false;
		super.onEnable();
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (!isInGame()) {
			return;
		}

		if (!(PlayerUtil.isOverAir() && mc.player.onGround)) {
			return;
		}

		if (holdShift.getValue()) {
			if (!mc.player.isSneaking()) {
				return;
			}
		}

		if (gliding) {
		    float actualYaw = mc.player.rotationYaw;
		    float actualPitch = mc.player.rotationPitch;

		    float modifiedYaw = actualYaw % 360;
		    float modifiedPitch = actualPitch % 360;

		    float targetYaw = waitingForYaw;
		    float targetPitch = waitingForPitch;

		    float currentYaw = modifiedYaw - speedYaw;
		    float currentYaw2 = modifiedYaw + speedYaw;
		    float currentPitch = modifiedPitch - speedPitch;
		    float currentPitch2 = modifiedPitch + speedPitch;

		    currentYaw = Math.abs(currentYaw);
		    currentYaw2 = Math.abs(currentYaw2);
		    currentPitch = Math.abs(currentPitch);
		    currentPitch2 = Math.abs(currentPitch2);

		    if (speedYaw > currentYaw || speedYaw > currentYaw2) {
		        mc.player.rotationYaw = targetYaw;
		    }
		    if (speedPitch > currentPitch || speedPitch > currentPitch2) {
		        mc.player.rotationPitch = targetPitch;
		    }

		    mc.player.rotationYaw = adjustRotation(mc.player.rotationYaw, targetYaw, speedYaw);
		    mc.player.rotationPitch = adjustRotation(mc.player.rotationPitch, targetPitch, speedPitch);

		    if (mc.player.rotationYaw == targetYaw && mc.player.rotationPitch == targetPitch) {
		        gliding = false;
		        waitingForAim = false;
		    }
		}

		if (!waitingForAim) {
			waitingForAim = true;
			startWaitTime = System.currentTimeMillis();
			return;
		}

		if (System.currentTimeMillis() - startWaitTime < delay.getValue().intValue())
			return;

		float fuckedYaw = mc.player.rotationYaw;
		float fuckedPitch = mc.player.rotationPitch;

		float yaw = fuckedYaw - ((int) fuckedYaw / 360) * 360;
		float pitch = fuckedPitch - ((int) fuckedPitch / 360) * 360;
		float speed = assistSpeed.getValue().floatValue();
		float range = assistRange.getValue().floatValue();

		Map<String, float[]> modePositions = new HashMap<>();
		modePositions.put("Godbridge", godbridgePos);
		modePositions.put("Moonwalk", moonwalkPos);
		modePositions.put("Breezily", breezilyPos);
		modePositions.put("Normal", normalPos);

		float[] positions = modePositions.get(mode.getValue().getName());

		if (positions != null && positions.length > 1) {
		    if (positions[0] >= (pitch - range) && positions[0] <= (pitch + range)) {
		        for (int k = 1; k < positions.length; k++) {
		            if (positions[k] >= (yaw - range) && positions[k] <= (yaw + range)) {
		                aimAt(positions[0], positions[k], fuckedYaw, fuckedPitch, speed);
		                waitingForAim = false;
		                return;
		            }
		        }
		    }
		}
		waitingForAim = false;
	};

	public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch, float speed) {
		float[] gcd = getGCDRotations(new float[] { yaw, pitch + ((int) fuckedPitch / 360) * 360 }, new float[] { mc.player.prevRotationYaw, mc.player.prevRotationPitch });
		float cappedYaw = maxAngleChange(mc.player.prevRotationYaw, gcd[0], speed);
		float cappedPitch = maxAngleChange(mc.player.prevRotationPitch, gcd[1], speed);
		mc.player.rotationPitch = cappedPitch;
		mc.player.rotationYaw = cappedYaw;
	}

	private double getGCD() {
		final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float pow = sens * sens * sens * 8.0F;
		return pow * 0.15D;
	}

	private float[] getGCDRotations(final float[] rotations, final float[] prevRots) {
		final float yawDif = rotations[0] - prevRots[0];
		final float pitchDif = rotations[1] - prevRots[1];
		final double gcd = getGCD();

		rotations[0] -= yawDif % gcd;
		rotations[1] -= pitchDif % gcd;
		return rotations;
	}

	private float maxAngleChange(final float prev, final float now, final float maxTurn) {
		float dif = MathHelper.wrapAngleTo180_float(now - prev);
		if (dif > maxTurn)
			dif = maxTurn;
		if (dif < -maxTurn)
			dif = -maxTurn;
		return prev + dif;
	}
	
	private float adjustRotation(float currentRotation, double targetRotation, float speed) {
	    if (currentRotation < targetRotation) {
	        return currentRotation + speed;
	    } else if (currentRotation > targetRotation) {
	        return currentRotation - speed;
	    }
	    return currentRotation;
	}
}