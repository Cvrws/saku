package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;

public class NaturalPressEvent extends CancellableEvent {
	private boolean shouldRightClick;
	private int slot;

	public NaturalPressEvent(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return this.slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public boolean isShouldRightClick() {
		return this.shouldRightClick;
	}

	public void setShouldRightClick(boolean shouldRightClick) {
		this.shouldRightClick = shouldRightClick;
	}
}
