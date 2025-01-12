package cc.unknown.util.client;

import org.lwjgl.input.Mouse;

import cc.unknown.util.Accessor;
import cc.unknown.util.structure.geometry.Vector2d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.ScaledResolution;

@UtilityClass
public class MouseUtil implements Accessor {
    public boolean isHovered(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public Vector2d mouse() {
    	ScaledResolution sr = new ScaledResolution(mc);
        final int i1 = sr.getScaledWidth();
        final int j1 = sr.getScaledHeight();
        final int mouseX = Mouse.getX() * i1 / mc.displayWidth;
        final int mouseY = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;

        return new Vector2d(mouseX, mouseY);
    }
}
