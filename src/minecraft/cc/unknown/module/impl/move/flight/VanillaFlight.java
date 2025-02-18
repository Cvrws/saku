package cc.unknown.module.impl.move.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Flight;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

public class VanillaFlight extends Mode<Flight> {

    public VanillaFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        final float speed = getParent().speed.getValueToFloat();

        event.setSpeed(speed);
    };

    @EventLink
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        final float speed = getParent().speed.getValueToFloat();

        mc.player.motionY = 0.0D + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);
    };

    @EventLink
    public final Listener<MoveInputEvent> onMove = event -> event.setSneak(false);

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }
}
