package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.SlowDownEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptSlowDownEvent extends CancellableScriptEvent<SlowDownEvent> {

    public ScriptSlowDownEvent(final SlowDownEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setStrafeMultiplier(final float strafeMultiplier) {
        this.wrapped.setStrafeMultiplier(strafeMultiplier);
    }

    public void setForwardMultiplier(final float forwardMultiplier) {
        this.wrapped.setStrafeMultiplier(forwardMultiplier);
    }

    public float getStrafeMultiplier() {
        return this.wrapped.getStrafeMultiplier();
    }

    public float getForwardMultiplier() {
        return this.wrapped.getForwardMultiplier();
    }

    @Override
    public String getHandlerName() {
        return "onSlowDown";
    }
}
