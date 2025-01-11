package cc.unknown.util.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.client.MathUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;

@UtilityClass
public final class ColorUtil {

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
    
    @UtilityClass
    public class ColorInterpolator {

        public double interpolate(double oldValue, double newValue, double interpolationValue) {
            return oldValue + (newValue - oldValue) * interpolationValue;
        }

        public int interpolateInt(int oldValue, int newValue, double interpolationValue) {
            return (int) interpolate((double) oldValue, (double) newValue, interpolationValue);
        }

        public Color interpolateColorC(Color color1, Color color2, float amount) {
            amount = Math.max(0f, Math.min(1f, amount));
            return new Color(
                interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount)
            );
        }

        public float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
            return (float) interpolate((double) oldValue, (double) newValue, interpolationValue);
        }

        public Color interpolateColorHue(Color color1, Color color2, float amount) {
            amount = Math.max(0f, Math.min(1f, amount));
            float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
            float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

            Color resultColor = Color.getHSBColor(
                interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount),
                interpolateFloat(color1HSB[2], color2HSB[2], amount)
            );

            return reAlpha(
                resultColor,
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount) / 255f
            );
        }

        public int interpolateColor(int color1, int color2, float amount) {
            amount = (float) Math.max(0.0, Math.min(1.0, amount));
            Color cColor1 = new Color(color1, true);
            Color cColor2 = new Color(color2, true);
            return interpolateColorC(cColor1, cColor2, amount).getRGB();
        }

        public Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
            int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
            angle = (angle >= 180 ? 360 - angle : angle) * 2;

            return trueColor
                ? interpolateColorHue(start, end, angle / 360f)
                : interpolateColorC(start, end, angle / 360f);
        }

        private Color reAlpha(Color color, float alpha) {
            return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                Math.round(alpha * 255)
            );
        }
    }
}
