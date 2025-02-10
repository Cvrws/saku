package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaturalPressEvent extends CancellableEvent {
	private boolean shouldRightClick;
	private int slot;

	public NaturalPressEvent(int slot) {
		this.slot = slot;
	}
}
