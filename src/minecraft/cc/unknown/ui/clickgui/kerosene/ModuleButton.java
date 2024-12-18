package cc.unknown.ui.clickgui.kerosene;

import java.awt.Color;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.util.render.font.Font;

public class ModuleButton {
    private Module module;
    private int x, y, width, height;
    private Font font;

    public ModuleButton(Module module, int x, int y, int width, int height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = Fonts.ROBOTO.get(18, Weight.LIGHT);
    }

    public void draw(int mouseX, int mouseY) {
        String moduleName = module.getName();

        this.width = (int) font.width(moduleName) + 10;

        int textY = y + (height - (int)font.height()) / 2;

        Color buttonColor = module.isEnabled() ? new Color(0, 255, 0, 150) : new Color(255, 0, 0, 150);
      //  drawRect(x, y, x + width, y + height, buttonColor.getRGB());


        int textColor = isMouseOver(mouseX, mouseY) ? Color.GRAY.getRGB() :
                (module.isEnabled() ? new Color(60, 180, 255).getRGB() : Color.WHITE.getRGB());



        font.drawWithShadow(moduleName, x + 5, textY, textColor);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public Module getModule() {
        return module;
    }
}