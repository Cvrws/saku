package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptNaturalPressEvent extends ScriptEvent<NaturalPressEvent> {

    public ScriptNaturalPressEvent(final NaturalPressEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onNatural";
    }
}
