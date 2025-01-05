package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimerManipulationEvent implements Event {
    private long time;
}