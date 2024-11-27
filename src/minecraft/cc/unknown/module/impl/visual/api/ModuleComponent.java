package cc.unknown.module.impl.visual.api;

import java.awt.Color;

import cc.unknown.module.Module;
import cc.unknown.util.geometry.Vector2d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public final class ModuleComponent {

    public final Module module;
    public Vector2d position = new Vector2d(5000, 0);
    public Vector2d targetPosition = new Vector2d(5000, 0);
    public float nameWidth = 0;
    public Color color = Color.WHITE;
    public String translatedName = "";
    public boolean hidden = false;
    public String displayName = "";
}