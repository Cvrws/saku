package cc.unknown.module.impl.visual.cosmetics;

import java.util.function.Consumer;

import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.util.render.bluearchive.HaloRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlueArchive {
    SHIROKO("Shiroko", HaloRenderer::drawShirokoHalo),
    HOSHINO("Hoshino", HaloRenderer::drawHoshinoHalo),
    ARIS("Aris", HaloRenderer::drawArisHalo),
    NATSU("Natsu", HaloRenderer::drawNatsuHalo),
    REISA("Reisa", HaloRenderer::drawReisaHalo),
    NONE("None", event -> {});

    private final String name;
    private final Consumer<Render3DEvent> renderFunction;

    public void render(Render3DEvent event) {
        renderFunction.accept(event);
    }

    @Override
    public String toString() {
        return name;
    }
}
