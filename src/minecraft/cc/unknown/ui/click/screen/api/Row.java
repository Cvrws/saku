package cc.unknown.ui.click.screen.api;

import static cc.unknown.ui.click.screen.ConfigScreen.PADDING;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.click.screen.ConfigScreen;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.drag.Mouse;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.render.gui.ScrollUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.Getter;

@Getter
public class Row extends ArrayList<Element> implements Accessor {
    private String name;
    private static float SPACER = 10;
    private Vector2f position;
    private ScrollUtil scrollUtil = new ScrollUtil();

    public Row(int initialCapacity, String name) {
        super(initialCapacity);
        this.name = name;
    }

    public Row(String name) {
        this.name = name;
    }

    public Row(Collection<? extends Element> c, String name) {
        super(c);
        this.name = name;
    }

    public void render(Vector2f position) {
        try {
            RiceGui clickGUI = getClickGUI();
            this.position = new Vector2f(position.x, position.y);
            Fonts.MAISON.get(18, Weight.NONE).draw(name, this.position.x, this.position.y, Color.WHITE.getRGB());

            String size = this.size() + "";
            Fonts.MAISON.get(18, Weight.NONE).draw(size, this.position.x + Fonts.MAISON.get(18, Weight.NONE).width(name) + PADDING / 2f, this.position.y, getTheme().getFirstColor().getRGB());

            if (!this.isEmpty()) {
                scrollUtil.onRender(over());
                scrollUtil.setMax(-this.size() * (PADDING + get(0).getScale().x) + getClickGUI().scale.x - getClickGUI().sidebar.sidebarWidth - PADDING);

                if (!scrollUtil.isActive()) {
                    scrollUtil.target = Math.round(scrollUtil.target / (PADDING + get(0).getScale().x)) * (PADDING + get(0).getScale().x);
                }

                this.position.y += PADDING + Fonts.MAISON.get(18, Weight.NONE).height();
                this.position.x += scrollUtil.getScroll();

                for (Element element : this) {
                    if (!((this.position.x > clickGUI.getPosition().x + clickGUI.getScale().x) ||
                            (this.position.x + element.getScale().x < clickGUI.position.x + clickGUI.sidebar.sidebarWidth))) {
                        element.render(this.position);
                    }

                    this.position.x += PADDING + element.getScale().x;
                }
            }

            this.position = new Vector2f(position.x, position.y);
        } catch (ConcurrentModificationException concurrentModificationException) {
            concurrentModificationException.printStackTrace();
        }
    }

    public int getHeight() {
        return (int) ((isEmpty() ? 0 : get(0).getScale().getY()) + PADDING + Fonts.MAISON.get(18, Weight.NONE).height());
    }

    public void onClick(int mouseX, int mouseY, int mouseButton) {
        for (Element element : this) {
            element.onClick(mouseX, mouseY, mouseButton);
        }
    }

    public void onScroll() {
        if (position == null) return;

        if (over()) {
            ConfigScreen.SCROLL = false;
        }
    }

    public boolean over() {
        if (isEmpty()) return false;
        return GUIUtil.mouseOver(getClickGUI().position, getClickGUI().scale, Mouse.getMouse()) && GUIUtil.mouseOver(
                new Vector2f((float) (getClickGUI().position.x + getClickGUI().sidebar.sidebarWidth), position.y),
                new Vector2f((float) (getClickGUI().scale.x - getClickGUI().sidebar.sidebarWidth), get(0).getScale().y + PADDING * 2 + 10),
                Mouse.getMouse());
    }

    public void init() {
        getScrollUtil().setScroll(0);
        getScrollUtil().setTarget(0);
    }
}
