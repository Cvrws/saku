package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptJumpEvent extends CancellableScriptEvent<JumpEvent> {

    public ScriptJumpEvent(final JumpEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setJumpMotion(final float jumpMotion) {
        this.wrapped.setJumpMotion(jumpMotion);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public float getJumpMotion() {
        return this.wrapped.getJumpMotion();
    }

    public float getYaw() {
        return this.wrapped.getYaw();
    }

    @Override
    public String getHandlerName() {
        return "onJump";
    }
}
