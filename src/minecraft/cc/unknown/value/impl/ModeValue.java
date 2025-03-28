package cc.unknown.value.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

public class ModeValue extends ListValue<Mode<?>> {

    private final List<Mode<?>> modes = new ArrayList<>();

    public ModeValue(final String name, final Module parent) {
        super(name, parent);
    }

    public ModeValue(final String name, final Mode<?> parent) {
        super(name, parent);
    }

    public ModeValue(final String name, final Module parent, final BooleanSupplier hideIf) {
        super(name, parent, hideIf);
    }

    public ModeValue(final String name, final Mode<?> parent, final BooleanSupplier hideIf) {
        super(name, parent, hideIf);
    }

    public void update(final Mode<?> value) {

        if (this.getParent() != null && (!(this.getParent() instanceof Module) || ((Module) this.getParent()).isEnabled())) {
            getValue().unregister();
            setValue(value);
            getValue().register();
        } else {
            setValue(value);
        }
    }

    public ModeValue add(final Mode<?>... modes) {
        if (modes == null) {
            return this;
        }

        this.modes.addAll(Arrays.asList(modes));
        return this;
    }

    public ModeValue setDefault(final String name) {
        setValue(modes.stream()
                .filter(mode -> mode.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(modes.get(0))
        );

        setDefaultValue(getValue());

        modes.forEach(mode -> mode.getValues().forEach(value -> value.setInternalHideIf(() -> mode != this.getValue())));

        return this;
    }

    public void setValue(final String name) {
        setValue(modes.stream()
                .filter(mode -> mode.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(modes.get(0))
        );
    }

    @Override
    public List<Value<?>> getSubValues() {
        ArrayList<Value<?>> allValues = new ArrayList<>();

        for (Mode<?> mode : getModes()) {
            allValues.addAll(mode.getValues());
        }

        return allValues;
    }

	public List<Mode<?>> getModes() {
		return modes;
	}
	
	public String getMode() {
	    return getValue().getName().toLowerCase();
	}
}