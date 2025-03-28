package cc.unknown.ui.click.component;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.module.api.Category;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.click.screen.Colors;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.GUIUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SidebarComponent implements Accessor {

    private final List<CategoryComponent> categories;
    /* Information */
    public double sidebarWidth = 100;
    private double opacity, fadeOpacity;
    private boolean hovering;
    private long lastTime = 0;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO, 300);
    private Animation dropShadowAnimation = new Animation(Easing.LINEAR, 300);

    public SidebarComponent() {
        categories = Arrays.stream(Category.values())
                .map(CategoryComponent::new)
                .collect(Collectors.toList());
    }

    public void renderSidebar(final float mouseX, final float mouseY) {
        /* ClickGUI */
        final RiceGui clickGUI = Sakura.instance.getClickGui();

        /* Animations */
        final long time = System.currentTimeMillis();

        if (lastTime == 0) lastTime = time;

        final boolean hoverCategory = clickGUI.selectedScreen.hideSideBar();

        if ((hovering = (!Mouse.isButtonDown(0) || hovering) && GUIUtil.mouseOver(clickGUI.position.x - 200, clickGUI.position.y, hovering ? 310 : 210, clickGUI.scale.y, mouseX, mouseY) || !hoverCategory)) {
            opacity = Math.min(opacity + (time - lastTime) * 2, 255);
        } else {
            opacity = Math.max(opacity - (time - lastTime) * 1.5f, 0);
        }

        if (GUIUtil.mouseOver(clickGUI.position.x, clickGUI.position.y, fadeOpacity > 0 ? 70 : 10, clickGUI.scale.y, mouseX, mouseY) && hoverCategory) {
            fadeOpacity = Math.min(fadeOpacity + (time - lastTime) * 2, 255);
        } else {
            fadeOpacity = Math.max(fadeOpacity - (time - lastTime), 0);
        }

        /* Sidebar background */
        lastTime = time;

        /* Renders all categories */
        double offsetTop = -15;

        for (final CategoryComponent category : categories) {
            category.render((offsetTop += 19.2), sidebarWidth + animation.getValue(), (int) opacity, clickGUI.selectedScreen);
        }
    }

    public void clickSidebar(final float mouseX, final float mouseY, final int button) {
        if (opacity > 0) {
            for (final CategoryComponent category : categories) {
                category.click(mouseX, mouseY, button);
            }
        }
    }

    public void release() {
        if (opacity > 0) {
            for (final CategoryComponent category : categories) {
                category.release();
            }
        }
    }
}
