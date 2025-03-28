package cc.unknown.ui.menu.saku.api;

import java.awt.Color;

import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class Button extends GuiButton {
	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	double size;
	private Font font;

	public Button(final int button, final int x, final int y, final int width, final int height, final String text) {
		super(button, x, y, width, height, text);
		this.size = 0.0;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = Fonts.MAISON.get(18, Weight.NONE);
	}

	public Button(final int i, final int j, final int k, final String stringParams) {
		this(i, j, k, 200, 20, stringParams);
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
		final boolean isOverButton = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		final int color = isOverButton ? new Color(255, 255, 255).getRGB() : new Color(200, 200, 200).getRGB();
		RenderUtil.roundedRect(x - size, y - size, x + width + size, y + height + size, 6.0, new Color(1, 1, 1, 150).getRGB());
		int textWidth = font.width(text);
		int textHeight = (int) font.height();
		float centeredX = x + (width / 2.0f) - (textWidth / 2.0f);
		float centeredY = y + (height / 1.5f) - (textHeight / 1.5f);
		
		font.drawWithShadow(text, centeredX, centeredY, color);
	}
}