package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

public class LegitSpeed extends Mode<Speed> {
	public LegitSpeed(String name, Speed parent) {
		super(name, parent);
	}
	
	@Override
	public void onEnable() {
		if (mc.player == null) return;
	}

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (event.getForward() > 0.8) {
			mc.player.setSprinting(true);
		}
		
		if (mc.player.onGround && MoveUtil.isMoving()) {
			event.setJump(true);
		}
	};
}