package cc.unknown.value.impl;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.click.component.TextValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

public class TextValue extends Value<String> {

    public TextValue(final String name, final Module parent, final String defaultValue) {
        super(name, parent, defaultValue);
    }

    public TextValue(final String name, final Mode<?> parent, final String defaultValue) {
        super(name, parent, defaultValue);
    }

    public TextValue(final String name, final Module parent, final String defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    public TextValue(final String name, final Mode<?> parent, final String defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    @Override
    public TextValueComponent createUIComponent() {
        return new TextValueComponent(this);
    }
}