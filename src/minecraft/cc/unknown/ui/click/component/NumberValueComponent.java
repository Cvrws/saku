package cc.unknown.ui.click.component;

import cc.unknown.ui.click.screen.Colors;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.render.gui.box.TextAlign;
import cc.unknown.util.render.gui.box.TextBox;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.value.Value;
import cc.unknown.value.impl.NumberValue;

public class NumberValueComponent extends ValueComponent implements Accessor {

    private final static double SLIDER_WIDTH = 100;
    private final double grabberWidth = 5;
    private final StopWatch stopWatch = new StopWatch();
    public boolean grabbed;
    private double percentage;
    private double selector;
    private double renderPercentage;
    private boolean mouseOver;
    private float hoverTime;
    public final TextBox valueDisplay = new TextBox(
    	    new Vector2d(0, 0),
    	    Fonts.MAISON.get(16, Weight.NONE),
    	    Colors.SECONDARY_TEXT.get(),
    	    TextAlign.LEFT,
    	    String.format("%.2f", ((NumberValue) value).getDefaultValue().floatValue()),
    	    45,
    	    "1234567890."
    	);

    public NumberValueComponent(final Value<?> value) {
        super(value);

        this.updateSliders();
    }

    public void updateSliders() {
        final NumberValue numberValue = (NumberValue) value;
        this.percentage = Math.min(Math.max(0, (-numberValue.getMin().doubleValue() + numberValue.getValue().doubleValue()) / (-numberValue.getMin().doubleValue() + numberValue.getMax().doubleValue())), 1);
    }

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;

        // Cast
        final NumberValue numberValue = (NumberValue) this.value;

        String value = String.valueOf(numberValue.getValue().doubleValue());
        final float valueWidth = Fonts.MAISON.get(16, Weight.NONE).width(this.value.getName()) + 7;

        if (value.endsWith(".0")) {
            value = value.replace(".0", "");
        }

        //Used to determine if the mouse is over the slider
        this.mouseOver = GUIUtil.mouseOver(this.position.x + valueWidth - 5, this.position.y - 3.5F, SLIDER_WIDTH + 10, this.height, mouseX, mouseY);
        if (this.mouseOver) {
            hoverTime = Math.min(1, hoverTime + stopWatch.getElapsedTime() / 200.0F);
        } else {
            hoverTime = Math.max(0, hoverTime - stopWatch.getElapsedTime() / 200.0F);
        }

        // Draws name
        Fonts.MAISON.get(16, Weight.NONE).draw(this.value.getName(), this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));

        // Draws value
        this.valueDisplay.setPosition(new Vector2d(this.position.x + valueWidth + 105, this.position.y));
        if (!this.valueDisplay.isSelected()) this.valueDisplay.setText(value);
        this.valueDisplay.setWidth(20);
        this.valueDisplay.setColor(ColorUtil.withAlpha(this.valueDisplay.getColor(), opacity));
        this.valueDisplay.draw();

        // Draws background
        RenderUtil.roundedRectangle(this.position.x + valueWidth, this.position.y + 1.5F, SLIDER_WIDTH, 2, 1, Colors.BACKGROUND.getWithAlpha(opacity));

        selector = this.position.x + valueWidth;

        if (getClickGUI().animationTime < 0.8) grabbed = false;

        if (grabbed) {
            percentage = mouseX - selector;
            percentage /= SLIDER_WIDTH;
            percentage = Math.max(Math.min(percentage, 1), 0);

            numberValue.setValue((numberValue.getMin().doubleValue() + (numberValue.getMax().doubleValue() - numberValue.getMin().doubleValue()) * percentage));
            numberValue.setValue(MathUtil.roundWithSteps(numberValue.getValue().doubleValue(), numberValue.getDecimalPlaces().floatValue()));
        }

        //Animations
        final int speed = 30;
        for (int i = 0; i <= stopWatch.getElapsedTime(); i++) {
            renderPercentage = (renderPercentage * (speed - 1) + percentage) / speed;
        }

        final double positionX = selector + renderPercentage * 100;
        RenderUtil.roundedRectangle(positionX - grabberWidth / 2.0F, this.position.y, grabberWidth, grabberWidth, grabberWidth / 2.0F, ColorUtil.withAlpha(getTheme().getFirstColor(), opacity));
        stopWatch.reset();
    }

    @Override
    public boolean click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return false;
        }

        final boolean left = mouseButton == 0;

        if (left) {
            if (this.mouseOver) {
                grabbed = true;
                return true;
            }
        }

        valueDisplay.click(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public void released() {
        grabbed = false;
    }

    @Override
    public void key(final char typedChar, final int keyCode) {
        // Return
        if (keyCode == 28) {
            final NumberValue numberValue = (NumberValue) this.value;
            if (valueDisplay.getText().isEmpty()) {
                numberValue.setValue(numberValue.getDefaultValue());
            } else {
                double value = Double.parseDouble(valueDisplay.getText());
                numberValue.setValue(value);
            }

            valueDisplay.setSelected(false);
            this.updateSliders();
            return;
        }

        valueDisplay.key(typedChar, keyCode);
    }
}
