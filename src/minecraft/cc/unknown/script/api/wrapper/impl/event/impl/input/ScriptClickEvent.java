package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.ClickEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptClickEvent extends ScriptEvent<ClickEvent> {

    public ScriptClickEvent(final ClickEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onClick";
    }
}
