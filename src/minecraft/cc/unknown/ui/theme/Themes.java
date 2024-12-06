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

import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.render.ColorUtil;
import lombok.Getter;
import net.minecraft.util.ChatFormatting;

@Getter
public enum Themes {

    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95), DARK_PURPLE, new Color(128, 50, 255), new Color(255, 50, 50)),
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200), ChatFormatting.AQUA, new Color(50, 200, 255)),
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255), YELLOW, new Color(255, 255, 50)),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(128, 255, 50)),
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115), DARK_AQUA, new Color(255, 128, 255), new Color(100, 100, 110)),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), LIGHT_PURPLE, new Color(255, 128, 255), new Color(128, 50, 255)),
    CANDY_CANE("Candy Cane", new Color(255, 0, 0), new Color(255, 255, 255), RED, new Color(255, 50, 50)),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), RED, new Color(255, 50, 50), new Color(128, 50, 255), new Color(255, 128, 255)),
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255), new Color(64, 255, 64), RED, new Color(255, 50, 50), new Color(128, 255, 50)),
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151), DARK_AQUA, new Color(255, 128, 255), new Color(255, 128, 50), new Color(50, 100, 200)),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(255, 50, 50), new Color(255, 128, 255)),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83), DARK_PURPLE, new Color(128, 50, 255), new Color(255, 128, 255)),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(128, 255, 50)),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), GREEN, new Color(128, 255, 50), new Color(50, 128, 50)),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), RED, new Color(255, 50, 50), new Color(255, 128, 50)),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), LIGHT_PURPLE, new Color(255, 128, 255), new Color(50, 100, 200), new Color(50, 200, 255)),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), BLUE, new Color(50, 100, 200), new Color(128, 50, 255)),
    MAY("May", new Color(238, 79, 238), new Color(253, 219, 245), LIGHT_PURPLE, new Color(255, 128, 255), new Color(128, 50, 255)),
    NETFLIX("Netflix", new Color(142, 14, 0), new Color(31, 28, 24), ChatFormatting.DARK_RED),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), GOLD, new Color(255, 128, 50), new Color(255, 255, 50)),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), LIGHT_PURPLE, new Color(255, 128, 255)),
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169), new Color(227, 139, 42), GOLD, new Color(255, 128, 50)),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), RED, new Color(255, 50, 50)),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(18, 232, 232), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(100, 100, 110)),
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68), BLUE, new Color(50, 100, 200), new Color(100, 100, 110)),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(122, 44, 77), RED, new Color(255, 128, 255), new Color(128, 50, 255), new Color(255, 50, 50)),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), YELLOW, new Color(255, 255, 50), new Color(255, 128, 50)),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(50, 100, 200)),
    LEGACY("Legacy", new Color(0x70CEFF), new Color(0x70CEFF), ChatFormatting.AQUA, new Color(50, 200, 255), new Color(50, 100, 200)),
    WINTER("Winter", Color.WHITE, Color.WHITE, GRAY, new Color(100, 100, 110), new Color(100, 100, 110)),
    PEONY("Peony", new Color(226, 208, 249), new Color(207, 171, 255), DARK_AQUA, new Color(255, 128, 255), new Color(100, 100, 110)),
    SHADOW("Shadow", new Color(97, 131, 255), new Color(206, 212, 255), ChatFormatting.AQUA, new Color(50, 200, 255)),
    WOOD("Wood", new Color(79, 109, 81), new Color(170, 139, 87), new Color(240, 235, 206), DARK_GREEN, new Color(50, 128, 50)),
    CREIDA("Creida", new Color(0xff4e5270).brighter().brighter(), new Color(0xff4e5270).darker(), NONE, new Color(100, 100, 110)),
    CREIDA_TWO("Creida Two", new Color(0xff9ACAEB), new Color(0xff7FBBE6).darker(), NONE, new Color(100, 100, 110)),
    GOTHIC("Gothic", new Color(31, 30, 30), new Color(196, 190, 190), NONE, new Color(100, 100, 110)),
    SEN("Rue", new Color(234, 118, 176), new Color(31, 30, 30), DARK_PURPLE, new Color(255, 128, 255)),
    PURPLE("Purple", new Color(0x524391), new Color(0x524391).brighter(), NONE);

    private final String themeName;
    private Color firstColor = null, secondColor = null, thirdColor = null;
    private Function<Vector2d, Color> custom;
    private final ChatFormatting chatAccentColor;
    private final ArrayList<Color> keyColors;
    private final boolean triColor;

    Themes(String themeName, Color firstColor, Color secondColor, ChatFormatting chatAccentColor, Color... keyColors) {
        this.themeName = themeName;
        this.firstColor = this.thirdColor = firstColor;
        this.secondColor = secondColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
        this.triColor = false;
    }

    Themes(String themeName, Color firstColor, Color secondColor, Color thirdColor, ChatFormatting chatAccentColor, Color... keyColors) {
        this.themeName = themeName;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
        this.triColor = true;
    }

    Themes(String themeName, Function<Vector2d, Color> custom, ChatFormatting chatAccentColor, Color... keyColors) {
        this.themeName = themeName;
        this.custom = custom;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
        this.triColor = true;
    }

    public Color getFirstColor() {
        return custom == null ? firstColor : getAccentColor(new Vector2d(0, 0));
    }

    public Color getSecondColor() {
        return custom == null ? secondColor : getAccentColor(new Vector2d(0, 50));
    }

    public Color getThirdColor() {
        return custom == null ? thirdColor : getAccentColor(new Vector2d(0, 100));
    }

    public Color getAccentColor(Vector2d screenCoordinates) {

        if (this.custom != null) {
            return custom.apply(screenCoordinates);
        }

        if (this.triColor) {
            double blendFactor = this.getBlendFactor(screenCoordinates);

            if (blendFactor <= 0.5) return ColorUtil.mixColors(getSecondColor(), getFirstColor(), blendFactor * 2D);
            else return ColorUtil.mixColors(getThirdColor(), getSecondColor(), (blendFactor - 0.5) * 2D);
        }

        return ColorUtil.mixColors(getFirstColor(), getSecondColor(), getBlendFactor(screenCoordinates));
    }

    public Color getAccentColor() {
        return getAccentColor(new Vector2d(0.0, 0.0));
    }

    @Deprecated
    public int getRound() {
        return 4;
    }

    public float getPadding() {
        return 4.5f;
    }

    public Color getDropShadow() {
        return new Color(0, 0, 0, 190);
    }

    public double getBlendFactor(Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0D + screenCoordinates.getX() * 0.005D + screenCoordinates.getY() * 0.06D) * 0.5D + 0.5D;
    }

    static Color backgroundShade = new Color(0, 0, 0, 110);

	public boolean isTriColor() {
		return triColor;
	}

	public static Color getBackgroundShade() {
		return backgroundShade;
	}

	public void setFirstColor(Color firstColor) {
		this.firstColor = firstColor;
	}

	public void setSecondColor(Color secondColor) {
		this.secondColor = secondColor;
	}

	public void setThirdColor(Color thirdColor) {
		this.thirdColor = thirdColor;
	}

	public void setCustom(Function<Vector2d, Color> custom) {
		this.custom = custom;
	}

	public static void setBackgroundShade(Color backgroundShade) {
		Themes.backgroundShade = backgroundShade;
	}
}