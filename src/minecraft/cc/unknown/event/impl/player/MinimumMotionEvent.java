package cc.unknown.event.impl.player;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MinimumMotionEvent extends CancellableEvent {
    private double minimumMotion;
}