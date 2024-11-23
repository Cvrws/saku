package cc.unknown.value;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.toggle.Toggleable;
import lombok.Getter;

@Getter
public abstract class Mode<T> implements Accessor, Toggleable {
    private final String name;
    private final T parent;
    private final List<Value<?>> values = new ArrayList<>();

    public Mode(String name, T parent) {
		this.name = name;
		this.parent = parent;
	}

	public void register() {
        Sakura.instance.getEventBus().register(this);
        this.onEnable();
    }

    public void unregister() {
        Sakura.instance.getEventBus().unregister(this);
        this.onDisable();
    }

    @Override
    public void toggle() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}