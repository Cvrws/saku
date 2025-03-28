package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.render.ScriptRender2DEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
@RequiredArgsConstructor
public class Render2DEvent implements Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptRender2DEvent(this);
    }
}
