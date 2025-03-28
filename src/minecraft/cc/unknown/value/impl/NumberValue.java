package cc.unknown.value.impl;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.click.component.NumberValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;
import lombok.Getter;

@Getter
public class NumberValue extends Value<Number> {

    private final Number min;
    private final Number max;
    private final Number decimalPlaces;

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces) {
        super(name, parent, defaultValue);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Mode<?> parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces) {
        super(name, parent, defaultValue);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Mode<?> parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    @Override
    public NumberValueComponent createUIComponent() {
        return new NumberValueComponent(this);
    }
    
    public int getValueToInt() {
    	return getValue().intValue();
    }
    
    public long getValueToLong() {
    	return getValue().longValue();
    }
    
    public float getValueToFloat() {
    	return getValue().floatValue();
    }
    
    public double getValueToDouble() {
    	return getValue().doubleValue();
    }
    
    public short getValueToShort() {
    	return getValue().shortValue();
    }
    
    public byte getValueToByte() {
    	return getValue().byteValue();
    }
}