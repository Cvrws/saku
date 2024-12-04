package cc.unknown.script.api.wrapper.impl.event;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.ScriptWrapper;

public abstract class ScriptEvent<T extends Event> extends ScriptWrapper<T> {

    public ScriptEvent(final T wrappedEvent) {
        super(wrappedEvent);
    }

    public abstract String getHandlerName();
}
