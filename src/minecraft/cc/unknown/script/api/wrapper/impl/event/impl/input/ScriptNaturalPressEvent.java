package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptNaturalPressEvent extends ScriptEvent<NaturalPressEvent> {

    public ScriptNaturalPressEvent(final NaturalPressEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
	public int getSlot() {
		return wrapped.getSlot();
	}

	public void setSlot(int slot) {
		wrapped.setSlot(slot);
	}

	public boolean isShouldRightClick() {
		return wrapped.isShouldRightClick();
	}

	public void setShouldRightClick(boolean shouldRightClick) {
		wrapped.setShouldRightClick(shouldRightClick);
	}

    @Override
    public String getHandlerName() {
        return "onNatural";
    }
}
