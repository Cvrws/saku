package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptMoveInputEvent extends ScriptEvent<MoveInputEvent> {

    public ScriptMoveInputEvent(final MoveInputEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setForward(final float forward) {
        this.wrapped.setForward(forward);
    }

    public void setStrafe(final float strafe) {
        this.wrapped.setStrafe(strafe);
    }

    public void setJump(final boolean jump) {
        this.wrapped.setJump(jump);
    }

    public void setSneak(final boolean sneak) {
        this.wrapped.setSneak(sneak);
    }

    public void setSneakSlowDownMultiplier(final double sneakSlowDownMultiplier) {
        this.wrapped.setSneakSlowDownMultiplier(sneakSlowDownMultiplier);
    }

    public float getForward() {
        return this.wrapped.getForward();
    }

    public float getStrafe() {
        return this.wrapped.getStrafe();
    }

    public boolean isJump() {
        return this.wrapped.isJump();
    }

    public boolean isSneak() {
        return this.wrapped.isSneak();
    }

    public double getSneakSlowDownMultiplier() {
        return this.wrapped.getSneakSlowDownMultiplier();
    }

    @Override
    public String getHandlerName() {
        return "onMoveInput";
    }
}
