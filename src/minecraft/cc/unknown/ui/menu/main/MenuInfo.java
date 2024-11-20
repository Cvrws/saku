package cc.unknown.ui.menu.main;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.impl.visual.RichPresence;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class MenuInfo extends GuiScreen {
    private Font fontRenderer;

    public MenuInfo() {
        fontRenderer = Fonts.MAIN.get(18, Weight.LIGHT);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
        BackgroundUtil.renderBackground(this);

        ScaledResolution sr = mc.scaledResolution;
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        int padding = 5;
        int infoWidth = screenWidth / 2;
        int infoY = screenHeight / 8;

        int totalTextHeight = (int) ((fontRenderer.height() + padding) * 9);
        int infoHeight = totalTextHeight;
        RenderUtil.drawRoundedRect((screenWidth - infoWidth) / 2, infoY - 5, infoWidth, infoHeight, 30, new Color(0, 0, 0, 150).getRGB());
        
        int currentY = infoY;
        String title = "Sakura Client Information";
        int titleWidth = fontRenderer.width(title);
        Fonts.MAIN.get(18, Weight.BOLD).draw(title, (screenWidth - titleWidth) / 2, currentY, -1);
        currentY += fontRenderer.height() + padding;

        String type = Sakura.instance.getClientInfo().getType();
        String version = Sakura.instance.getClientInfo().getVersion();

        int typeColor = getColorForType(type);
        int green = new Color(0, 200, 0).getRGB();
        int red = new Color(200, 0, 0).getRGB();
        String buildStatus = type.equals("Private") ? "Customer" : "Beta";
        int buildColor = type.equals("Private") ? green : red;

        currentY = drawLabel("Client Type: ", type, typeColor, 10, currentY, screenWidth);
        currentY = drawLabel("Client Version: ", version, green, 5, currentY, screenWidth);
        currentY = drawLabel("Rich Presence: ", isRPC() ? "ON" : "OFF", isRPC() ? green : red, 5, currentY, screenWidth);
        currentY = drawLabel("Total Modules: ", "" + Sakura.instance.moduleCounter, green, 5, currentY, screenWidth);
        currentY = drawLabel("Total Settings: ", "" + Sakura.instance.settingCounter, green, 5, currentY, screenWidth);
        currentY = drawLabel("User Type: ", buildStatus, buildColor, 14, currentY, screenWidth);
    }

    private int drawLabel(String label, String value, int valueColor, int xPosition, int yPosition, int screenWidth) {
        String fullLabel = label + value;
        int labelWidth = fontRenderer.width(fullLabel);
        fontRenderer.draw(label, (screenWidth - labelWidth) / 2, yPosition, -1);
        fontRenderer.draw(value, (screenWidth + fontRenderer.width(label)) / 2 - xPosition, yPosition, valueColor);
        return (int) (yPosition + fontRenderer.height() + 10);
    }

    private int getColorForType(String type) {
        switch (type) {
            case "Developer":
                return new Color(0, 200, 0).getRGB();
            case "Private":
                return new Color(185, 0, 54).getRGB();
            default:
                return new Color(31, 48, 189).getRGB();
        }
    }
    
    private boolean isRPC() {
    	return getModule(RichPresence.class).isEnabled();
    }
}