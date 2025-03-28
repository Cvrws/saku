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
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.TextValue;

public class DescValueComponent extends ValueComponent {

    public DescValueComponent(final Value<?> value) {
        super(value);

        final DescValue stringValue = (DescValue) value;
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        this.height = 14;

        Fonts.MAISON.get(14, Weight.NONE).draw(this.value.getName(), this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));
    }

    @Override
    public boolean click(final int mouseX, final int mouseY, final int mouseButton) {
        return false;
    }

    @Override
    public void released() {
    }

    @Override
    public void key(final char typedChar, final int keyCode) {

    }
}
