package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MouseEvent extends CancellableEvent {

	private final int code;
}
