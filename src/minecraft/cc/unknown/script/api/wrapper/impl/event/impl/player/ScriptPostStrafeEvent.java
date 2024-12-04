package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PostStrafeEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptPostStrafeEvent extends ScriptEvent<PostStrafeEvent> {

    public ScriptPostStrafeEvent(final PostStrafeEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onPostStrafe";
    }
}
