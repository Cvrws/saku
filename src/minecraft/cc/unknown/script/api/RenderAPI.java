package cc.unknown.script.api;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import javax.script.ScriptException;

import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.client.gui.Gui;
import cc.unknown.Sakura;
import cc.unknown.script.api.wrapper.impl.ScriptItemStack;
import cc.unknown.script.api.wrapper.impl.ScriptMCFontRenderer;
import cc.unknown.script.api.wrapper.impl.ScriptSakuraFontRenderer;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.impl.sakura.FontRenderer;

public class RenderAPI extends API implements Accessor {

    public ScriptVector3d getCameraPosition() {
        return new ScriptVector3d(MC.getRenderManager().renderPosX, MC.getRenderManager().renderPosY, MC.getRenderManager().renderPosZ);
    }

    public static Color intArrayToColor(int[] color) {
        int[] clamped = Arrays.stream(color).map(x -> Math.min(x, 255)).toArray();
        return new Color(clamped[0], clamped[1], clamped[2], clamped.length >= 4 ? clamped[3] : 255);
    }
    
    public void rectangle(final double x, final double y, final double width, final double height, final int[] color) {
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), intArrayToColor(color).getRGB());
    }

    public void roundedRectangle(final double x, final double y, final double width, final double height, final double radius, final int[] color) {
        RenderUtil.roundedRectangle(x, y, width, height, radius, intArrayToColor(color));
    }

    public void drawLine3D(double x, double y, double z, double x1, double y1, double z1, final int[] color, final float width) {
        RenderUtil.drawLine(x, y, z, x1, y1, z1, intArrayToColor(color), width);
    }

    public void drawLine3D(ScriptVector3d from, ScriptVector3d to, final int[] color, final float width) {
        drawLine3D(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), color, width);
    }

    public ScriptMCFontRenderer getMinecraftFontRenderer() {
        return new ScriptMCFontRenderer(MC.fontRendererObj);
    }

    public float getEyeHeight() {
        return MC.player.getEyeHeight();
    }

    public int[] getThemeColor() {
        int[] f = new int[4];
        Color c = Sakura.instance.getThemeManager().getTheme().getFirstColor();
        f[0] = c.getRed();
        f[1] = c.getGreen();
        f[2] = c.getBlue();
        f[3] = c.getAlpha();
        return f;
    }

    public int[] getBackgroundShade() {
        Color color = getTheme().getBackgroundShade();
        return new int[]{color.getRed(), color.getBlue(), color.getGreen(), color.getAlpha()};
    }
}
