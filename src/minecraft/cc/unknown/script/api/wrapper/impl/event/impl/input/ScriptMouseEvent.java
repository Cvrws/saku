package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.MouseEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptMouseEvent extends ScriptEvent<MouseEvent> {

    public ScriptMouseEvent(final MouseEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public int getMouseCode() {
    	return this.wrapped.getCode();
    }

    @Override
    public String getHandlerName() {
        return "onMouse";
    }
}
