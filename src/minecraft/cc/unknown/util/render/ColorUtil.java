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
	
	public int getTeamColor(EntityPlayer player) {
		Scoreboard scoreboard = player.getWorldScoreboard();
		ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getName());

		if (playerTeam != null) {
			String color = playerTeam.getColorPrefix();
			if (color.length() < 2) {
				return Color.WHITE.getRGB();
			}
			char colorChar = color.charAt(1);
			if (colorChar == '4' || colorChar == 'c') {
				return Color.RED.getRGB();
			}
			if (colorChar == '6' || colorChar == 'e') {
				return Color.YELLOW.getRGB();
			}
			if (colorChar == '2' || colorChar == 'a') {
				return Color.GREEN.getRGB();
			}
			if (colorChar == 'b' || colorChar == '3') {
				return Color.CYAN.getRGB();
			}
			if (colorChar == '9' || colorChar == '1') {
				return Color.BLUE.getRGB();
			}
			if (colorChar == 'd' || colorChar == '5') {
				return Color.MAGENTA.getRGB();
			}
			if (colorChar == 'f' || colorChar == '7' || colorChar == '8' || colorChar == '0') {
				return Color.WHITE.getRGB();
			}
		}
		return Color.WHITE.getRGB();
    }
}
