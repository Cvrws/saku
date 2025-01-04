package cc.unknown.util.render.drag.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Snap {
    public double position, distance;
    public Orientation orientation;
    public boolean center, right, left;
}
