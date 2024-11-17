package cc.unknown.event.impl.input;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MouseEvent implements Event {

	private final int code;
}
