package cc.unknown.ui.theme;

import static net.minecraft.util.ChatFormatting.BLUE;
import static net.minecraft.util.ChatFormatting.DARK_AQUA;
import static net.minecraft.util.ChatFormatting.DARK_GREEN;
import static net.minecraft.util.ChatFormatting.DARK_PURPLE;
import static net.minecraft.util.ChatFormatting.GOLD;
import static net.minecraft.util.ChatFormatting.GRAY;
import static net.minecraft.util.ChatFormatting.GREEN;
import static net.minecraft.util.ChatFormatting.LIGHT_PURPLE;
import static net.minecraft.util.ChatFormatting.NONE;
import static net.minecraft.util.ChatFormatting.RED;
import static net.minecraft.util.ChatFormatting.YELLOW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import lombok.Getter;
import net.minecraft.util.ChatFormatting;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum Themes {

	HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), Color.LIGHT_GRAY, ChatFormatting.LIGHT_PURPLE, new Color(255, 128, 255), new Color(50, 100, 200), new Color(50, 200, 255)),
	MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), Color.CYAN, ChatFormatting.BLUE, new Color(50, 100, 200), new Color(128, 50, 255)),
	NEON("Neon", new Color(0, 255, 140), new Color(0, 200, 255), Color.MAGENTA, ChatFormatting.AQUA, new Color(60, 240, 140), new Color(50, 150, 255), new Color(120, 255, 255)),
	SLINKY("Slinky", new Color(255, 165, 128), new Color(255, 0, 255), Color.PINK, ChatFormatting.RED, new Color(255, 128, 128), new Color(255, 0, 255), new Color(200, 100, 200)),
	ASTOLFO("Astolfo", new Color(243, 145, 216), new Color(152, 165, 243), new Color(64, 224, 208), ChatFormatting.LIGHT_PURPLE, new Color(243, 145, 216), new Color(152, 165, 243), new Color(64, 224, 208)),
	PRIMAVERA("Primavera", new Color(0, 206, 209), new Color(255, 255, 224), new Color(211, 211, 211), ChatFormatting.YELLOW, new Color(0, 206, 209), new Color(255, 255, 224), new Color(211, 211, 211)),
	OCEAN("Ocean", new Color(0, 0, 128), new Color(0, 255, 255), new Color(173, 216, 230), ChatFormatting.AQUA, new Color(0, 0, 128), new Color(0, 255, 255), new Color(173, 216, 230)),
	BLAZE("Blaze", new Color(255, 87, 51), new Color(139, 0, 0), new Color(255, 140, 0), ChatFormatting.GOLD, new Color(255, 87, 51), new Color(139, 0, 0), new Color(255, 140, 0)),
	GHOUL("Ghoul", new Color(150, 0, 0), new Color(0, 0, 0), new Color(64, 64, 64), ChatFormatting.DARK_GRAY, new Color(150, 0, 0), new Color(0, 0, 0), new Color(64, 64, 64));
	
    private final String themeName;
    private final Color firstColor;
    private final Color secondColor;
    private final Color thirdColor;
    private final ChatFormatting chatAccentColor;
    private final ArrayList<Color> keyColors;
    private final int round = 4;
    private final float padding = 4.5f;

    Themes(String themeName, Color firstColor, Color secondColor, Color thirdColor, ChatFormatting chatAccentColor, Color... keyColors) {
        this.themeName = themeName;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
    }

    public Color getAccentColor(Vector2d screenCoordinates) {
        double blendFactor = getBlendFactor(screenCoordinates);
        if (blendFactor <= 0.5) {
            return ColorUtil.mixColors(secondColor, firstColor, blendFactor * 2D);
        } else {
            return ColorUtil.mixColors(thirdColor, secondColor, (blendFactor - 0.5) * 2D);
        }
    }

    public Color getAccentColor() {
        return getAccentColor(new Vector2d(0.0, 0.0));
    }

    public double getBlendFactor(Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0D + screenCoordinates.getX() * 0.005D + screenCoordinates.getY() * 0.06D) * 0.5D + 0.5D;
    }
    
    public Color getBackgroundShade() {
        return new Color(0, 0, 0, 110);
    }
}