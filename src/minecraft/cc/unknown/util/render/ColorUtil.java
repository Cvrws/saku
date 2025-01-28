package cc.unknown.util.render;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.client.MathUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatFormatting;
import net.minecraft.util.MathHelper;

@UtilityClass
@Getter
public final class ColorUtil {
	
	public ChatFormatting yellow = ChatFormatting.YELLOW;
	public ChatFormatting red = ChatFormatting.RED;
	public ChatFormatting reset = ChatFormatting.RESET;
	public ChatFormatting white = ChatFormatting.RESET;
	public ChatFormatting aqua = ChatFormatting.AQUA;
	public ChatFormatting gray = ChatFormatting.GRAY;
	public ChatFormatting green = ChatFormatting.GREEN;
	public ChatFormatting blue = ChatFormatting.BLUE;
	public ChatFormatting black = ChatFormatting.BLACK;
	public ChatFormatting gold = ChatFormatting.GOLD;
	
	public ChatFormatting darkAqua = ChatFormatting.DARK_AQUA;
	public ChatFormatting darkGray = ChatFormatting.DARK_GRAY;
	public ChatFormatting darkPurple = ChatFormatting.DARK_PURPLE;
	public ChatFormatting darkBlue = ChatFormatting.DARK_BLUE;
	public ChatFormatting darkGreen = ChatFormatting.DARK_GREEN;
	public ChatFormatting darkRed = ChatFormatting.DARK_RED;

	public ChatFormatting pink = ChatFormatting.LIGHT_PURPLE;
	
    private final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)§([0-9A-FK-OR])");
   	public String usu = " ?§r§{0,3}§8§8\\[§r§f§fUsu§r§8§8\\]| ?§8\\[§fUsu§8\\]";
   	public String jup = " ?§r§{0,3}§8§8\\[§r§b§bJup§r§8§8\\]| ?§8\\[§bJup§8\\]";

	public String getPrefix(String rank, ChatFormatting rankColor) {
		return darkGray + "[" + rankColor + rank + darkGray + "] " + rankColor;
	}

    public void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }
    
    public int getAlphaFromColor(int color) {
        return color >> 24 & 0xFF;
    }
    
    public int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }
    
    public Color darker(final Color c, final double FACTOR) {
        return new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
    }

    public int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity);
    }
    
    public int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    public int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }
    
	public void glColor(final float red, final float green, final float blue, final float alpha) {
	    GL11.glColor4f(red, green, blue, alpha);
	}

    public void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public  Color withAlpha(final Color color, final int alpha) {
        if (alpha == color.getAlpha()) return color;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }

    public Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }
    
	public Color getAlphaColor(Color color, int alpha) {
	    int clampedAlpha = MathHelper.clamp_int(alpha, 0, 255);
	    if (color.getAlpha() == clampedAlpha) {
	        return color;
	    }
	    return new Color(color.getRed(), color.getGreen(), color.getBlue(), clampedAlpha);
	}
    
	public Color blend(Color color, Color color1, double d0) {
		float f = (float) d0;
		float f1 = 1.0F - f;
		float[] afloat = new float[3];
		float[] afloat1 = new float[3];
		color.getColorComponents(afloat);
		color1.getColorComponents(afloat1);
		return new Color(afloat[0] * f + afloat1[0] * f1, afloat[1] * f + afloat1[1] * f1,
				afloat[2] * f + afloat1[2] * f1);
	}
	
    public Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5);
    }
	
	public int getTeamColor(EntityPlayer player) {
		String name = player.getDisplayName().getFormattedText();
		name = removeFormatCodes(name);
        if (name.isEmpty() || !name.startsWith("§") || name.charAt(1) == 'f') {
            return -1;
        }
        switch (name.charAt(1)) {
            case '0':
                return -16777216;
            case '1':
                return -16777046;
            case '2':
                return -16733696;
            case '3':
                return -16733526;
            case '4':
                return -5636096;
            case '5':
                return -5635926;
            case '6':
                return -22016;
            case '7':
                return -5592406;
            case '8':
                return -11184811;
            case '9':
                return -11184641;
            case 'a':
                return -11141291;
            case 'b':
                return -11141121;
            case 'c':
                return -43691;
            case 'd':
                return -43521;
            case 'e':
                return -171;
        }
        return -1;
    }
	
    private String removeFormatCodes(String str) {
        return str.replace("§k", "").replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "");
    }
    
    public Color colorFromInt(int color) {
        Color c = new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }
    
    public int getColorFromTags(String displayName) {
        displayName = removeFormatCodes(displayName);
        if (displayName.isEmpty() || !displayName.startsWith("§") || displayName.charAt(1) == 'f') {
            return new Color(255, 255, 255).getRGB();
        }
        return getColorFromCode(displayName).getRGB();
    }
    
    private Color getColorFromCode(String input) {
        Matcher matcher = COLOR_CODE_PATTERN.matcher(input);
        if (matcher.find()) {
            char code = matcher.group(1).charAt(0);
            switch (code) {
                case '0': return new Color(0, 0, 0);
                case '1': return new Color(0, 0, 170);
                case '2': return new Color(0, 170, 0);
                case '3': return new Color(0, 170, 170);
                case '4': return new Color(170, 0, 0);
                case '5': return new Color(170, 0, 170);
                case '6': return new Color(255, 170, 0);
                case '7': return new Color(170, 170, 170);
                case '8': return new Color(85, 85, 85);
                case '9': return new Color(85, 85, 255);
                case 'a': return new Color(85, 255, 85);
                case 'b': return new Color(85, 255, 255);
                case 'c': return new Color(255, 85, 85);
                case 'd': return new Color(255, 85, 255);
                case 'e': return new Color(255, 255, 85);
                case 'f': return new Color(255, 255, 255);
                default: return new Color(255, 255, 255);
            }
        }
        return new Color(255, 255, 255);
    }
}
