package de.florianmichael.viamcp.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.util.Collections;
import java.util.List;

public class AsyncVersionSlider extends GuiButton {
	private float dragValue = (float) ViaLoadingBase.PROTOCOLS.indexOf(ViaLoadingBase.getInstance().getTargetVersion()) / (ViaLoadingBase.PROTOCOLS.size() - 1);

	private final List<ProtocolVersion> values;
	private float sliderValue;
	public boolean dragging;

	public AsyncVersionSlider(int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
		this.values = ViaLoadingBase.PROTOCOLS;
		Collections.reverse(values);
		this.sliderValue = dragValue;
		this.displayString = values.get((int) Math.ceil(this.sliderValue * (values.size() - 1))).getName();
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this
	 * button and 2 if it IS hovering over this button.
	 */
	@Override
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.dragging) {
				this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
				this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
				this.dragValue = sliderValue;

				// Ceil index to show correctly display string (26.999998 => 27)
				int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
				this.displayString = values.get(selectedProtocolIndex).getName();
				ViaLoadingBase.getInstance().reload(values.get(selectedProtocolIndex));
			}

			mc.getTextureManager().bindTexture(buttonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)),
					this.yPosition, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4,
					this.yPosition, 196, 66, 4, 20);
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
			this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
			this.dragValue = sliderValue;

			int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
			this.displayString = values.get(selectedProtocolIndex).getName();
			ViaLoadingBase.getInstance().reload(values.get(selectedProtocolIndex));
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
	}

	public void setVersion(int protocol) {
		this.dragValue = (float) ViaLoadingBase.PROTOCOLS.indexOf(ProtocolVersion.getProtocol(protocol)) / (ViaLoadingBase.PROTOCOLS.size() - 1);
		this.sliderValue = this.dragValue;

		int selectedProtocolIndex = (int) Math.ceil(this.sliderValue * (values.size() - 1));
		this.displayString = values.get(selectedProtocolIndex).getName();
	}
}