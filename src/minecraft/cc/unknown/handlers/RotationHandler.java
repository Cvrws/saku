package cc.unknown.handlers;

import java.util.function.Function;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.LookEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MathHelper;

public final class RotationHandler implements Accessor {
	@Setter @Getter
	private static boolean active, smoothed;
	public static Vector2f rotations, lastRotations = new Vector2f(0, 0), targetRotations, lastServerRotations;
	private static double rotationSpeed;
	@Setter
	private static MoveFix correctMovement;
	private static Function<Vector2f, Boolean> raycast;
	private static float randomAngle;
	private static final Vector2f offset = new Vector2f(0, 0);

	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MoveFix correctMovement) {
		setRotations(rotations, rotationSpeed, correctMovement, null);
	}

	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MoveFix correctMovement, final Function<Vector2f, Boolean> raycast) {
		RotationHandler.targetRotations = rotations;
		RotationHandler.rotationSpeed = rotationSpeed * 36;
		RotationHandler.correctMovement = correctMovement;
		RotationHandler.raycast = raycast;
		active = true;

		smooth();
	}

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (!active || rotations == null || lastRotations == null || targetRotations == null
				|| lastServerRotations == null) {
			rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.player.rotationYaw,
					mc.player.rotationPitch);
		}

		if (active) {
			smooth();
		}
	};

	@EventLink(value = Priority.LOW)
	public final Listener<MoveInputEvent> onMove = event -> {
		if (active && correctMovement == MoveFix.SILENT && rotations != null) {
			final float yaw = rotations.x;
			MoveUtil.fixMovement(event, yaw);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<LookEvent> onLook = event -> {
		if (active && rotations != null) {
			event.setRotation(rotations);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (active && (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT)
				&& rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<JumpEvent> onJump = event -> {
		if (active && (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT) && rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (active && rotations != null) {
			final float yaw = rotations.x;
			final float pitch = rotations.y;

			event.setYaw(yaw);
			event.setPitch(pitch);

			mc.player.rotationYawHead = yaw;
			mc.player.renderPitchHead = pitch;

			lastServerRotations = new Vector2f(yaw, pitch);

			if (Math.abs((rotations.x - mc.player.rotationYaw) % 360) < 1
					&& Math.abs((rotations.y - mc.player.rotationPitch)) < 1) {
				active = false;

				this.correctDisabledRotations();
			}

			lastRotations = rotations;
		} else {
			lastRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		}

		targetRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		smoothed = false;

	};

	private void correctDisabledRotations() {
		final Vector2f rotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		final Vector2f fixedRotations = RotationUtil.resetRotation(RotationUtil.applySensitivityPatch(rotations, lastRotations));

		mc.player.rotationYaw = fixedRotations.x;
		mc.player.rotationPitch = fixedRotations.y;
	}

    public static void smooth() {
        if (!smoothed) {
            float targetYaw = targetRotations.x;
            float targetPitch = targetRotations.y;

            // Randomisation
            if (raycast != null && (Math.abs(targetYaw - rotations.x) > 5 || Math.abs(targetPitch - rotations.y) > 5)) {
                final Vector2f trueTargetRotations = new Vector2f(targetRotations.getX(), targetRotations.getY());

                double speed = (Math.random() * Math.random() * Math.random()) * 20;
                randomAngle += (float) ((20 + (float) (Math.random() - 0.5) * (Math.random() * Math.random() * Math.random() * 360)) * (mc.player.ticksExisted / 10 % 2 == 0 ? -1 : 1));

                offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
                offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

                targetYaw += offset.getX();
                targetPitch += offset.getY();

                if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
                    randomAngle = (float) Math.toDegrees(Math.atan2(trueTargetRotations.getX() - targetYaw, targetPitch - trueTargetRotations.getY())) - 180;

                    targetYaw -= offset.getX();
                    targetPitch -= offset.getY();

                    offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
                    offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

                    targetYaw = targetYaw + offset.getX();
                    targetPitch = targetPitch + offset.getY();
                }

                if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
                    offset.setX(0);
                    offset.setY(0);

                    targetYaw = (float) (targetRotations.x + Math.random() * 2);
                    targetPitch = (float) (targetRotations.y + Math.random() * 2);
                }
            }

            rotations = RotationUtil.smooth(new Vector2f(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT) {
                mc.player.movementYaw = rotations.x;
            }

            mc.player.velocityYaw = rotations.x;
        }

        smoothed = true;
        mc.entityRenderer.getMouseOver(1);
    }
}