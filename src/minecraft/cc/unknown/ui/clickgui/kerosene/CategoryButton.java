package cc.unknown.ui.clickgui.kerosene;

import java.awt.Color;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.api.Category;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;

public class CategoryButton implements Accessor {
    private Category category;
    private int x, y , width, height;

    public CategoryButton(Category category, int x, int y, int width, int height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(int mouseX, int mouseY) {
        if (category == Category.SEARCH) return;

        Color buttonColor = isMouseOver(mouseX, mouseY) 
            ? new Color(60, 60, 60, 200) 
            : new Color(80, 80, 80, 10);
            
        RenderUtil.drawRect(x, y, x + width + 3, y + height, buttonColor.getRGB());
        Fonts.MONSERAT.get(15, Weight.BOLD)
            .drawCentered(category.name(), x + 24, y + 8, Color.WHITE.getRGB());
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Category getCategory() {
        return category;
    }
}