package cc.unknown.ui.clickgui.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Button {
    private float x, y, width, height;

    public abstract void drawPanel(int mouseX, int mouseY);

    public abstract void mouseAction(int mouseX, int mouseY, boolean click, int button);

    public boolean isHovered(final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
    }
}