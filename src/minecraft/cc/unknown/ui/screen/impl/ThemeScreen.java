package cc.unknown.ui.screen.impl;

import java.util.ArrayList;

import cc.unknown.Sakura;
import cc.unknown.ui.components.theme.ThemeComponent;
import cc.unknown.ui.screen.Screen;
import cc.unknown.ui.theme.Themes;
import cc.unknown.util.Accessor;
import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.render.gui.ScrollUtil;
import lombok.Getter;

@Getter
public class ThemeScreen implements Screen, Accessor {
    private final ArrayList<ThemeComponent> allThemes = new ArrayList<>();
    private ArrayList<ThemeComponent> visibleThemes = new ArrayList<>();

    private final ScrollUtil scrollUtil = new ScrollUtil();

    public ThemeScreen() {
        for (Themes themes : Themes.values()) {
            this.allThemes.add(new ThemeComponent(themes));
        }
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        scrollUtil.onRender();

        final double rows = Math.ceil(visibleThemes.size() / 3D);
        scrollUtil.setMax(-57 * Math.max(0, (rows - 3)));

        double padding = 7;
        double scrollX = getClickGUI().getPosition().getX() + getClickGUI().getScale().getX() - 4;
        double scrollY = getClickGUI().getPosition().getY() + padding;

        scrollUtil.renderScrollBar(new Vector2d(scrollX, scrollY), getClickGUI().scale.y - padding * 2);

        final Vector2f position = getClickGUI().getPosition();
        final Vector2f scale = getClickGUI().getScale();
        final double sidebar = getClickGUI().getSidebar().sidebarWidth;
        final double positionY = position.getY() + 24 + scrollUtil.getScroll();

        final double themeWidth = (scale.getX() - sidebar - 29) / 3D;
        final double colorWidth = (scale.getX() - sidebar - 43) / 5D;

        // Draw themes
        for (int i = 0; i < this.visibleThemes.size(); i++) {
            ThemeComponent theme = this.visibleThemes.get(i);

            theme.getXAnimation().run(position.getX() + sidebar + 7 + ((7 + themeWidth) * (i % 3)));
            theme.getYAnimation().run(position.getY() - 35 + Math.floor(i / 3D) * 57 + 60);
        }

        for (ThemeComponent theme : this.allThemes) {
            if (theme.getOpacityAnimation().getValue() > 0) {
                theme.draw(this.scrollUtil.getScroll(), themeWidth);
            }

            theme.getOpacityAnimation().run(this.visibleThemes.contains(theme) ? 255 : 0);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton > 0) return;

        for (ThemeComponent theme : this.visibleThemes) {
            if (GUIUtil.mouseOver(theme.getLastDraw().getX(), theme.getLastDraw().getY(), theme.getLastDraw().getZ(), 50, mouseX, mouseY)) {
                Sakura.instance.getThemeManager().setTheme(theme.getActiveTheme());
            }
        }
    }

    @Override
    public void onInit() {
        this.allThemes.forEach(theme -> theme.getOpacityAnimation().setValue(255));
        this.visibleThemes = new ArrayList<>(this.allThemes);
        this.scrollUtil.reset();
        this.resetAnimations();
    }

    public void resetAnimations() {
        final Vector2f position = getClickGUI().getPosition();
        final Vector2f scale = getClickGUI().getScale();
        final double sidebar = getClickGUI().getSidebar().sidebarWidth;
        final double themeWidth = (scale.getX() - sidebar - 29) / 3D;

        for (int i = 0; i < this.visibleThemes.size(); i++) {
            ThemeComponent theme = this.visibleThemes.get(i);

            theme.getXAnimation().setValue(position.getX() + sidebar + 7 + ((7 + themeWidth) * (i % 3)));
            theme.getYAnimation().setValue(position.getY() - 40 + Math.floor(i / 3D) * 57 + 60);
        }
    }
}