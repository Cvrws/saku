package cc.unknown.ui.click.component;

import java.awt.Color;
import java.util.ArrayList;

import cc.unknown.module.Module;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.click.screen.Colors;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleComponent implements Accessor {
    public Module module;
    public Vector2f scale = getClickGUI().getModuleDefaultScale();
    public boolean expanded;
    public ArrayList<ValueComponent> valueList = new ArrayList<>();
    public Vector2d position;
    public double opacity;
    public StopWatch stopwatch = new StopWatch();
    public Animation hoverAnimation = new Animation(Easing.LINEAR, 50);
    public Animation opening = new Animation(Easing.LINEAR, 200);
    public Animation settingOpacity = new Animation(Easing.LINEAR, 5000);
    public boolean mouseDown;

    public ModuleComponent(final Module module) {
        this.module = module;

        opening.setValue(scale.y);
        settingOpacity.setValue(0);

        module.getAllValues().forEach(value -> {
            ValueComponent component = value.createUIComponent();
            if (component != null) valueList.add(component);
        });
    }

    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;

        /* Allows for settings to be drawn */
        float defaultHeight = 38;
        float height = defaultHeight;

        boolean render = !(position == null || position.y + scale.y < getClickGUI().position.y || position.y > getClickGUI().position.y + getClickGUI().scale.y);

        handle:
        {
            if (!render) break handle;

            final RiceGui clickGUI = this.getClickGUI();

            // MAISON module background
            RenderUtil.roundedRectangle(position.x - 28, position.y, scale.x, scale.y, 6, Colors.OVERLAY.get());
            final Color fontColor = Colors.TEXT.getWithAlpha(module.isEnabled() ? 255 : 200);

            // Hover animation
            final boolean overModule = GUIUtil.mouseOver(position.x - 28, position.y, scale.x, this.scale.y, mouseX, mouseY);

            hoverAnimation.animate(overModule ? mouseDown ? 35 : 20 : 0);

            // Draw the module's category if the user is searching
            /*if (clickGUI.getRenderedScreen() instanceof HomeScreen) {
                Fonts.MAISON.get(15, Weight.NONE).draw("(" + module.getModuleInfo().category().getName() + ")", (float) (position.getX() + Fonts.MAISON.get(20, Weight.NONE).width(this.module.getName()) + 10F), (float) position.getY() + 10, ColorUtil.withAlpha(fontColor, 64).hashCode());
            }*/

            // Draw module name
            Fonts.MAISON.get(20, Weight.NONE).draw(this.module.getName(), (float) position.x - 20f, (float) position.y + 8, module.isEnabled() ? getTheme().getAccentColor(new Vector2d(0, position.y / 5)).getRGB() : fontColor.getRGB());

            // Draw description
            Fonts.MAISON.get(15, Weight.NONE).draw(module.getModuleInfo().description(), (float) position.x - 20f,  (float) position.y + 25, ColorUtil.withAlpha(fontColor, 70).hashCode());

            scale = new Vector2f(getClickGUI().moduleDefaultScale.x, height);
        }

        if (!opening.isFinished() || expanded) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                if (valueComponent.getValue() != null && valueComponent.getValue().getHideIf() != null && valueComponent.getValue().getHideIf().getAsBoolean()) {
                    continue;
                }

                if (valueComponent.getValue().getInternalHideIf() != null && valueComponent.getValue().getInternalHideIf().getAsBoolean()) {
                    continue;
                }

                valueComponent.setOpacity(valueComponent.position == null ? 0 : valueComponent.position.y < position.y + opening.getValue() + 15 ? (int) settingOpacity.getValue() : 0);
                valueComponent.setOpacity(valueComponent.getValue().getHideIf() == null ? valueComponent.getOpacity() : Math.max(valueComponent.getOpacity() - 40, 0));

                if (render) {
                    valueComponent.draw(new Vector2d(position.x - 20f + (valueComponent.getValue().getHideIf() == null ? 0 : 10) + (valueComponent.getValue().getInternalHideIf() == null ? 0 : 10), (float) (position.y + height + 1)), mouseX, mouseY, partialTicks);
                }

                height = (float) (height + valueComponent.getHeight());
            }

            // This makes the expanded category look better
            height -= 1;
        }

        opening.setDuration(Math.min((long) height * 3, 450));
        opening.setEasing(Easing.EASE_OUT_EXPO);
        opening.animate(expanded ? height : defaultHeight);
        scale.y = (float) opening.getValue();

        settingOpacity.setDuration(expanded ? opening.getDuration() / 2 : opening.getDuration() / 3);
        settingOpacity.animate(expanded ? 255 : 0);

    }

    public void key(final char typedChar, final int keyCode) {
        if (position == null || position.y + scale.y < getClickGUI().position.y || position.y > getClickGUI().position.y + getClickGUI().scale.y)
            return;

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                valueComponent.key(typedChar, keyCode);
            }
        }
    }

    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        /* Allows the module to be toggled */
        if (position == null || position.y + scale.y < getClickGUI().position.y || position.y > getClickGUI().position.y + getClickGUI().scale.y)
            return;

        final Vector2f clickGUIPosition = getClickGUI().position;
        final Vector2f clickGUIScale = getClickGUI().scale;

        final boolean left = mouseButton == 0;
        final boolean right = mouseButton == 1;
        final boolean overClickGUI = GUIUtil.mouseOver(clickGUIPosition.x, clickGUIPosition.y, clickGUIScale.x, clickGUIScale.y, mouseX, mouseY);
        final boolean overModule = GUIUtil.mouseOver(position.x - 26, position.y, scale.x, getClickGUI().moduleDefaultScale.getY() - 3, mouseX, mouseY);

        if (overModule && getClickGUI().overlayPresent == null) {
            mouseDown = true;

            double valueSize = 0;
            for (ValueComponent valueComponent : valueList) valueSize += valueComponent.getHeight();

            if (left) {
                if (overClickGUI) {
                    this.module.toggle();
                }
            } else if (right && this.module.getValues().size() != 0 && valueSize != 0) {
                this.expanded = !this.expanded;

                for (final ValueComponent valueComponent : this.getValueList()) {
                    if (valueComponent instanceof BoundsNumberValueComponent) {
                        final BoundsNumberValueComponent boundsNumberValueComponent = ((BoundsNumberValueComponent) valueComponent);
                        boundsNumberValueComponent.grabbed1 = boundsNumberValueComponent.grabbed2 = false;
                    } else if (valueComponent instanceof NumberValueComponent) {
                        ((NumberValueComponent) valueComponent).grabbed = false;
                    }
                }
            }
        }

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                if (valueComponent.getValue() != null && valueComponent.getValue().getHideIf() != null && valueComponent.getValue().getHideIf().getAsBoolean()) {
                    continue;
                }

                if (valueComponent.click(mouseX, mouseY, mouseButton)) {
                    break;
                }
            }
        }
    }

    public void released() {
        mouseDown = false;

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                valueComponent.released();
            }
        }
    }
}