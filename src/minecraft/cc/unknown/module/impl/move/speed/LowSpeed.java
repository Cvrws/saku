package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

public class LowSpeed extends Mode<Speed> {
	public LowSpeed(String name, Speed parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreStrafeEvent> onPreStrafe = event -> event.setSpeed(0.26f);
	
	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (mc.player.onGround) event.setJump(true);
	};
}