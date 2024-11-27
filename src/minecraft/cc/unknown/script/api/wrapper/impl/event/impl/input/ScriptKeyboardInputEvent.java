package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptKeyboardInputEvent extends ScriptEvent<KeyboardInputEvent> {

    public ScriptKeyboardInputEvent(final KeyboardInputEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public int getKey() {
        return this.wrapped.getKeyCode();
    }

    @Override
    public String getHandlerName() {
        return "onKeyboardInput";
    }
}
