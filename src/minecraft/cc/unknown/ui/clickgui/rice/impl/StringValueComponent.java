package cc.unknown.ui.clickgui.rice.impl;

import java.awt.Color;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.clickgui.rice.screen.Colors;
import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.gui.box.TextAlign;
import cc.unknown.util.render.gui.box.TextBox;
import cc.unknown.value.Value;
import cc.unknown.value.impl.StringValue;

public class StringValueComponent extends ValueComponent {

    public final TextBox textBox = new TextBox(new Vector2d(200, 200), Fonts.ROBOTO.get(16, Weight.LIGHT), Color.WHITE, TextAlign.LEFT, "", 20);

    public StringValueComponent(final Value<?> value) {
        super(value);

        final StringValue stringValue = (StringValue) value;
        textBox.setText(stringValue.getValue());
        textBox.setCursor(stringValue.getValue().length());
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        final StringValue stringValue = (StringValue) this.value;

        this.height = 28;

        Fonts.ROBOTO.get(16, Weight.LIGHT).draw(this.value.getName(), this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));

        this.textBox.setColor(ColorUtil.withAlpha(this.textBox.getColor(), opacity));
        this.position = new Vector2d(this.position.x, this.position.y + 14);
        this.textBox.setPosition(this.position);
        this.textBox.setWidth(242.5f - 12);
        this.textBox.draw();
        stringValue.setValue(textBox.getText());
    }

    @Override
    public boolean click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return false;
        }

        textBox.click(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public void released() {
    }

    @Override
    public void key(final char typedChar, final int keyCode) {
        if (this.position == null) {
            return;
        }

        textBox.key(typedChar, keyCode);
    }
}
