package cc.unknown.ui.menu;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.impl.movement.Sprint;
import cc.unknown.ui.menu.api.Button;
import cc.unknown.ui.menu.api.TextField;
import cc.unknown.util.client.irc.UserUtil;
import cc.unknown.util.client.security.AuthUtil;
import cc.unknown.util.client.security.BlackListUtil;
import cc.unknown.util.client.security.HardwareUtil;
import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.socket.EncryptUtil;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public final class LoginMenu extends GuiScreen {
    
    private TextField usernameBox;
    private TextField keyBox;
    private String status = "Ingresa tu usuario y dale a login.";
    private Animation animation;
    private final Font FONT_RENDERER = Fonts.ROBOTO.get(20, Weight.LIGHT);
    private final int MAX_ATTEMPTS = 3;
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    
    @Override
    public void initGui() {
    	super.initGui();
    	this.buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding * 2) / 3.0F;

        Vector2d position = new Vector2d(this.width / 2 - boxWidth / 2, this.height / 2 - 24);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
        keyBox = new TextField(1, this.fontRendererObj, (int) position.x, (int) (position.y + boxHeight + 5), (int) boxWidth, (int) boxHeight);
        this.buttonList.add(new Button(1, (int) ((int) position.x + buttonWidth + padding), (int) (position.y + boxHeight * 2 + padding * 2), (int) buttonWidth, (int) boxHeight, "Login"));
        animation = new Animation(Easing.EASE_OUT_QUINT, 600);
        animation.setStartValue(-200);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	BackgroundUtil.renderBackground(this);
        usernameBox.drawTextBox();
        keyBox.drawTextBox();

        GlStateManager.pushMatrix();
        int backgroundWidth = FONT_RENDERER.width(status) + 10;
        int backgroundHeight = (int) (FONT_RENDERER.height() + 5);

        int backgroundX = (width / 2) - (backgroundWidth / 2);
        int backgroundY = (int) ((height / 2 - 55 + animation.getValue()) - (backgroundHeight / 2));

        RenderUtil.roundedRect(backgroundX, backgroundY + animation.getValue(), backgroundX + backgroundWidth, backgroundY + backgroundHeight + animation.getValue(), 6.0, new Color(0, 0, 0, 150).getRGB());
        FONT_RENDERER.drawCentered(status, width / 2, height / 2 - 58 + animation.getValue(), Color.WHITE.getRGB());
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();
    }

    @Override
    @SneakyThrows
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    	keyBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    @SneakyThrows
    protected void keyTyped(char typedChar, int keyCode) {
        usernameBox.textboxKeyTyped(typedChar, keyCode);
        keyBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText().trim();
        String key = keyBox.getText().trim();
        String systemUuid = HardwareUtil.getUuid();

        switch (button.id) {
            case 1:
                if (username.isEmpty()) {
                    setStatusAndNotify("No puedes usar un nombre vacio.", "Invalid Username: Username cannot be empty.");
                    return;
                }

                if (key.isEmpty()) {
                    handleNewUser(username, systemUuid);
                    return;
                }

                if (BlackListUtil.isBlacklisted(key)) {
                    setStatusAndNotify("Tu key ha sido blacklisteada.", "BlackListed Key: " + key);
                    return;
                }

                handleExistingUser(username, key, systemUuid);
                break;

            default:
            	status = "Acción invalida.";
        }
    }
    
    @SneakyThrows
    private void handleNewUser(String username, String systemUuid) {
        String dataToEncrypt = username + "::" + systemUuid;
        String encryptedKey = EncryptUtil.encrypt(dataToEncrypt);
        AuthUtil.notify("`New User: " + username + " - Key: " + encryptedKey + "`");
        status = "Nuevo usuario creado correctamente.";
    }

    private void handleExistingUser(String username, String key, String systemUuid) {
        String decryptedKey = EncryptUtil.decrypt(key);
        String[] parts = decryptedKey.split("::");

        if (parts.length != 2) {
        	AuthUtil.notify("`Decrypted key format is invalid.`");
            status = "Key invalida.";
            return;
        }

        String decryptedUsername = parts[0];
        String decryptedUuid = parts[1];

        if (!decryptedUuid.equals(systemUuid)) {
            blacklistKey(key, "Invalid HWID: " + systemUuid + " - Key: " + key + " - Hwid Key: " + decryptedUuid);
            return;
        }

        if (!decryptedUsername.equalsIgnoreCase(username)) {
            handleInvalidUsername(username, key, decryptedUsername);
            return;
        }

        UserUtil.setUser(decryptedUsername);
        getModule(Sprint.class).logged = true;
        mc.displayGuiScreen(new MainMenu());
        AuthUtil.notify("`Login Success - User: " + UserUtil.getUser() + "`");
    }

    private void handleInvalidUsername(String username, String key, String decryptedUsername) {
        int attempts = loginAttempts.getOrDefault(key, 0) + 1;
        loginAttempts.put(key, attempts);

        int remainingAttempts = MAX_ATTEMPTS - attempts;
        if (remainingAttempts > 0) {
        	status = "Tu tienes " + remainingAttempts + " intentos" + (remainingAttempts > 1 ? "s" : "") + " de lo contrario tu key se bloqueará por motivos de seguridad.";
        } else {
        	status = "Tu key ha sido bloqueada por seguridad.";
            BlackListUtil.add(key);
            AuthUtil.notify("`Key added to blacklist due to multiple invalid username attempts.`");
            loginAttempts.remove(key);
        }

        AuthUtil.notify("`Invalid Username: " + username + " - Key: " + key + " - User Key: " + decryptedUsername + "`");
    }

    private void blacklistKey(String key, String message) {
    	AuthUtil.notify("`" + message + "`");
        BlackListUtil.add(key);
        AuthUtil.notify("`Key added to blacklist.`");
    }

    private void setStatusAndNotify(String statusMessage, String webhookMessage) {
    	status = statusMessage;
    	AuthUtil.notify("`" + webhookMessage + "`");
    }
}