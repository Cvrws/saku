package cc.unknown.ui.click.component;

import java.awt.Color;

import cc.unknown.ui.click.screen.Colors;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.box.TextAlign;
import cc.unknown.util.render.gui.box.TextBox;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.value.Value;
import cc.unknown.value.impl.TextValue;

public class TextValueComponent extends ValueComponent {

    public final TextBox textBox = new TextBox(new Vector2d(200, 200), Fonts.MAISON.get(16, Weight.NONE), Color.WHITE, TextAlign.LEFT, "", 20);

    public TextValueComponent(final Value<?> value) {
        super(value);

        final TextValue stringValue = (TextValue) value;
        textBox.setText(stringValue.getValue());
        textBox.setCursor(stringValue.getValue().length());
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        final TextValue stringValue = (TextValue) this.value;

        this.height = 28;

        Fonts.MAISON.get(16, Weight.NONE).draw(this.value.getName(), this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));

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
