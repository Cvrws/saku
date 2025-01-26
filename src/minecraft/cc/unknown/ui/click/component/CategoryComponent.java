package cc.unknown.ui.click.component;

import static cc.unknown.util.render.animation.Easing.LINEAR;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.click.screen.Screen;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import net.minecraft.client.renderer.GlStateManager;

public final class CategoryComponent implements Accessor {

    private final Animation animation = new Animation(LINEAR, 500);
    public final Category category;
    private long lastTime = 0;
    private double selectorOpacity;

    private float x, y;
    private boolean down;

    public CategoryComponent(final Category category) {
        this.category = category;
    }

    public void render(final double offset, final double sidebarWidth, final double opacity, final Screen selectedScreen) {
        final RiceGui clickGUI = Sakura.instance.getClickGui();

        if (System.currentTimeMillis() - lastTime > 300) lastTime = System.currentTimeMillis();
        final long time = System.currentTimeMillis();

        /* Gets position depending on sidebar animation */
        x = (float) (clickGUI.position.x - (69 - sidebarWidth) - 21);
        y = (float) (clickGUI.position.y + offset) + 16;

        /* Animations */
        animation.setDuration(200);
        animation.animate(selectedScreen.equals(category.getClickGUIScreen()) ? 255 : 0);

        final double spacer = 3;
        final double width = Fonts.MAISON.get(16, Weight.NONE).width(category.getName()) + spacer * 2 + category.getFontRenderer().width(category.getIcon());

        double scale = 0.5;
        GlStateManager.pushMatrix();

        int color = new Color(255, 255, 255, Math.min(selectedScreen.equals(category.getClickGUIScreen()) ? 255 : 200, (int) opacity)).hashCode();

        category.getFontRenderer().draw(category.getIcon(), (float) (x + animation.getValue() / 80f + 3), y, color);

        Fonts.MAISON.get(16, Weight.NONE).draw(category.getName(), (float) (x + animation.getValue() / 80f + 3 + spacer) + Fonts.ICONS_1.get(17).width(category.getIcon()), y, color);

        GlStateManager.popMatrix();

        lastTime = time;
    }

    public void click(final float mouseX, final float mouseY, final int button) {
        final boolean left = button == 0;
        if (GUIUtil.mouseOver(x - 11, y - 5, 70, 22, mouseX, mouseY) && left) {
            this.getClickGUI().switchScreen(this.category);
            down = true;
        }
    }

    public void release() {
        down = false;
    }
}