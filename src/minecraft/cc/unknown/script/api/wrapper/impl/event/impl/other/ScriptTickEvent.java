package cc.unknown.script.api.wrapper.impl.event.impl.other;

import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptTickEvent extends ScriptEvent<TickEvent> {

    public ScriptTickEvent(final TickEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onTick";
    }
}
