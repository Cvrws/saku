package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PostUpdateEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptPostUpdateEvent extends ScriptEvent<PostUpdateEvent> {

    public ScriptPostUpdateEvent(final PostUpdateEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onPostUpdate";
    }
}
