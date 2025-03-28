package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.potion.Potion;

public class BlocksMCSpeed extends Mode<Speed> {

	private boolean reset;
	private double speed;

	public BlocksMCSpeed(String name, Speed parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		final double base = MoveUtil.getAllowedHorizontalDistance();
		final boolean potionActive = mc.player.isPotionActive(Potion.moveSpeed);

		if (MoveUtil.isMoving()) {
			switch (mc.player.offGroundTicks) {
			case 0:
				mc.player.motionY = MoveUtil.jumpBoostMotion(0.42f);
				speed = base * (potionActive ? 1.4 : 2.15);
				break;

			case 1:
				speed -= 0.8 * (speed - base);
				break;

			default:
				speed -= speed / MoveUtil.BUNNY_FRICTION;
				break;
			}

			reset = false;
		} else if (!reset) {
			speed = 0;

			reset = true;
			speed = MoveUtil.getAllowedHorizontalDistance();
		}

		if (mc.player.isCollidedHorizontally) {
			speed = MoveUtil.getAllowedHorizontalDistance();
		}

		event.setSpeed(Math.max(speed, base), Math.random() / 2000);
	};

	@EventLink
	public final Listener<TeleportEvent> onTeleport = event -> {
		speed = 0;
	};

	@Override
	public void onDisable() {
		speed = 0;
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (!MoveUtil.isMoving()) {
			event.setPosX(event.getPosX() + (Math.random() - 0.5) / 3);
			event.setPosZ(event.getPosZ() + (Math.random() - 0.5) / 3);
		}

	};
}