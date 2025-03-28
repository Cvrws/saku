package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.module.impl.player.Clutch;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
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
		if (isEnabled(Scaffold.class, LegitScaffold.class, Clutch.class)) return;
		
	    if (event.getForward() > 0.8) {
	        mc.player.setSprinting(true);
	    }

	    if (shouldJump()) {
	        event.setJump(true);
	    }
	};
	
	@EventLink
	public final Listener<PreStrafeEvent> onPreStrafe = event -> {
        if(getParent().speed.getValue()) {
            event.setFriction(event.getFriction() * getParent().speedInc.getValue().floatValue());
        }
        
        if(getParent().legitStrafe.getValue() && !mc.player.onGround && (event.getStrafe() != 0 || event.getForward() != 0)) {
            event.setYaw(MoveUtil.getStrafeYaw(event.getForward(), event.getStrafe()));
            event.setForward(1);
            event.setStrafe(0);
        }
        
        if(getParent().fastFall.getValue()) {
            if(mc.player.fallDistance > 1.5) {
                mc.player.motionY *= 1.075;
            }
        }
	};
	
	private boolean shouldJump() {
	    return (mc.player.onGround && MoveUtil.isMoving()) || mc.player.isInWater() || mc.player.isInLava();
	}
}