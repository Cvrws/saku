package cc.unknown.event.impl.render;

import java.awt.Graphics;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExternalRenderEvent implements Event {
    private final Graphics graphics;
}