package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.ChatInputEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptChatInputEvent extends CancellableScriptEvent<ChatInputEvent> {

    public ScriptChatInputEvent(final ChatInputEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public String getMessage() {
        return this.wrapped.getMessage();
    }

    @Override
    public String getHandlerName() {
        return "onChatInput";
    }
}
