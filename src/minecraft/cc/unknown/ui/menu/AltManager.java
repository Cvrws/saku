package cc.unknown.ui.menu;

import static cc.unknown.util.render.ColorUtil.gray;
import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.yellow;

import java.awt.Color;
import java.io.IOException;

import cc.unknown.ui.menu.saku.SakuMenu;
import cc.unknown.ui.menu.saku.api.Button;
import cc.unknown.ui.menu.saku.api.TextField;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.auth.MicrosoftAccount;
import cc.unknown.util.account.name.UsernameGenerator;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.structure.geometry.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Session;

public class AltManager extends GuiScreen implements Accessor {
    private static TextField usernameBox;
    private static GuiScreen reference;
    public static String status = yellow + "Idle...";
    private static final Font FONT_RENDERER = Fonts.MAISON.get(20, Weight.NONE);

    public AltManager() {
        reference = this;
    }
    
    @Override
    public void initGui() {
    	status = yellow + "Idle...";
    	this.buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding * 2) / 3.0F;

        Vector2d position = new Vector2d(this.width / 2 - boxWidth / 2, this.height / 2 - 24);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Generar un nick random"));
    	this.buttonList.add(new Button(2, (int) (position.x + padding - 32), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Login"));
    	this.buttonList.add(new Button(3, (int) (position.x + (buttonWidth + padding) * 1 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Browser Login"));
    	this.buttonList.add(new Button(4, (int) (position.x + (buttonWidth + padding) * 2 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Cookie Login"));
    	this.buttonList.add(new Button(5, (int) (position.x + (buttonWidth + padding) * 3 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Atr�s"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);

        usernameBox.drawTextBox();
        GlStateManager.pushMatrix();

	     int backgroundWidth = FONT_RENDERER.width(status) + 22;
	     int backgroundHeight = (int) (FONT_RENDERER.height() + 5);
	     int backgroundX = (width / 2) - (backgroundWidth / 2);
	     int backgroundY = (int) (height / 2 - 35 - (backgroundHeight / 2));
	
	     FONT_RENDERER.drawCentered(status, width / 2, (int) (backgroundY + backgroundHeight / 2 - FONT_RENDERER.height() / 2), Color.WHITE.getRGB());
	     this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
	     GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	usernameBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(1));
        }
        
        if (keyCode == 1) {
        	mc.displayGuiScreen(new SakuMenu());
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText();

    	switch (button.id) {
        case 1:        	
        	String name = UsernameGenerator.generate();
        	if (name != null && UsernameGenerator.validate(name)) {
        		usernameBox.setText(name);
        	}
        	status = gray + "Te gusta este nombre > " + red + name + gray + "?";
        	break;
        case 2:
        	if (username.isEmpty()) status = gray + "Debes ingresar un ign primero.";
            if (UsernameGenerator.validate(username)) {
            	mc.setSession(new Session(username, "none", "none", "mojang"));
            	status = gray + "Logeado como > " + green + username;
            }
        	break;
        case 3:
        	status = gray + "Abriendo navegador...";
        	
            MicrosoftAccount.create();
        	break;
        case 4:
        	mc.displayGuiScreen(new CookieMenu());
            break;
        case 5:
        	mc.displayGuiScreen(new SakuMenu());
        	break;
        }
    }
}
