package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptPreStrafeEvent extends CancellableScriptEvent<PreStrafeEvent> {

    public ScriptPreStrafeEvent(final PreStrafeEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setForward(final float forward) {
        this.wrapped.setForward(forward);
    }

    public void setStrafe(final float strafe) {
        this.wrapped.setStrafe(strafe);
    }

    public void setFriction(final float friction) {
        this.wrapped.setFriction(friction);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public float getForward() {
        return this.wrapped.getForward();
    }

    public float getStrafe() {
        return this.wrapped.getStrafe();
    }

    public float getFriction() {
        return this.wrapped.getFriction();
    }

    public float getYaw() {
        return this.wrapped.getYaw();
    }

    public void setSpeed(final double speed, final double motionMultiplier) {
        this.wrapped.setSpeed(speed, motionMultiplier);
    }

    public void setSpeed(final double speed) {
        this.wrapped.setSpeed(speed);
    }

    @Override
    public String getHandlerName() {
        return "onPreStrafe";
    }
}
