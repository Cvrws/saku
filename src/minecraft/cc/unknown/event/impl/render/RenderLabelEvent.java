package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class RenderLabelEvent extends CancellableEvent {
    private Entity target;
    private double x;
    private double y;
    private double z;
}
