package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

public class VulcanSpeed extends Mode<Speed> {

	public VulcanSpeed(String name, Speed parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MoveInputEvent> onInput = event -> event.setJump(false);

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (mc.player == null) {
		    return;
		}
		
		if (mc.player.isInWater() || mc.player.isInLava()) {
		    return;
		}
		
		if (mc.player.onGround && MoveUtil.isMoving()) {
		    if (mc.player.isUsingItem()) {
		    	mc.player.jump();
		    } else {
		    	mc.player.jump();
		    	MoveUtil.strafe(0.4f);
		    }
		}
	};

}