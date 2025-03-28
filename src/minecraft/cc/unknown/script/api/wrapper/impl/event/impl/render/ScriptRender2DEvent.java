package cc.unknown.script.api.wrapper.impl.event.impl.render;

import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptRender2DEvent extends ScriptEvent<Render2DEvent> {
    public ScriptRender2DEvent(Render2DEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public float getPartialTicks() {
        return this.wrapped.getPartialTicks();
    }

    public int getScaledWidth() {
        return this.wrapped.getScaledResolution().getScaledWidth();
    }

    public int getScaledHeight() {
        return this.wrapped.getScaledResolution().getScaledHeight();
    }

    public int getScaleFactor() {
        return this.wrapped.getScaledResolution().getScaleFactor();
    }

    @Override
    public String getHandlerName() {
        return "onRender2D";
    }
}
