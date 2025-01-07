package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;

public class VanillaSpeed extends Mode<Speed> {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    public VanillaSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (MoveUtil.isMoving() && mc.player.onGround) {
            this.mc.player.jump();
        }

        event.setSpeed(speed.getValue().floatValue());
    };
}