package cc.unknown.util.render.drag.api;

import java.util.ArrayList;

import cc.unknown.module.Module;
import cc.unknown.ui.click.component.BooleanValueComponent;
import cc.unknown.ui.click.component.BoundsNumberValueComponent;
import cc.unknown.ui.click.component.DescValueComponent;
import cc.unknown.ui.click.component.ListValueComponent;
import cc.unknown.ui.click.component.ModeValueComponent;
import cc.unknown.ui.click.component.NumberValueComponent;
import cc.unknown.ui.click.component.PositionValueComponent;
import cc.unknown.ui.click.component.TextValueComponent;
import cc.unknown.ui.click.component.ValueComponent;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.value.Value;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.TextValue;
import net.minecraft.client.renderer.GlStateManager;

public class SettingsRenderer implements Accessor {
    public Module module;
    public DragValue positionValue;
    public Animation animation = new Animation(Easing.EASE_OUT_ELASTIC, 300);
    public boolean close;
    public ArrayList<ValueComponent> valueList = new ArrayList<>();

    public SettingsRenderer(Module module, DragValue positionValue) {
        this.module = module;
        this.positionValue = positionValue;

        for (final Value<?> value : module.getAllValues()) {
            if (value instanceof ModeValue) {
                valueList.add(new ModeValueComponent(value));
            } else if (value instanceof BooleanValue) {
                valueList.add(new BooleanValueComponent(value));
            } else if (value instanceof TextValue) {
                valueList.add(new TextValueComponent(value));
            } else if (value instanceof DescValue) {
            	valueList.add(new DescValueComponent(value));
            } else if (value instanceof NumberValue) {
                valueList.add(new NumberValueComponent(value));
            } else if (value instanceof BoundsNumberValue) {
                valueList.add(new BoundsNumberValueComponent(value));
            } else if (value instanceof DragValue) {
                valueList.add(new PositionValueComponent(value));
            } else if (value instanceof ListValue<?>) {
                valueList.add(new ListValueComponent(value));
            }
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        double width = 100, height = 100;
        animation.setDuration(300);
        animation.setEasing(close ? Easing.EASE_IN_EXPO : Easing.EASE_OUT_EXPO);
        animation.animate(close ? 0 : 1);
        double scale = animation.getValue();

        if (scale <= 0.0001) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((positionValue.position.x) * (1 - scale), (positionValue.position.y + positionValue.scale.y / 2) * (1 - scale), 0);
        GlStateManager.scale(scale, scale, 1);

        RenderUtil.roundedRectangle(positionValue.position.x - width - 10, positionValue.position.y + positionValue.scale.y / 2 - height / 2,
                    width, height, getTheme().getRound(), ColorUtil.withAlpha(getTheme().getBackgroundShade(), (int) (animation.getValue() * getTheme().getBackgroundShade().getAlpha())));

        for (final ValueComponent valueComponent : valueList) {
        	if (valueComponent.getValue() != null && valueComponent.getValue().getHideIf() != null && valueComponent.getValue().getHideIf().getAsBoolean()) {
        		continue;
        	}
        }

        GlStateManager.popMatrix();
    }

    public void close() {
        close = true;
    }
}
