package cc.unknown.ui.menu.main;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.impl.movement.Sprint;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.ui.menu.main.impl.TextField;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.security.HardwareUtil;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.blacklist.BlackListUtil;
import cc.unknown.util.security.hook.AuthUtil;
import cc.unknown.util.security.user.UserUtil;
import cc.unknown.util.vector.Vector2d;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public final class LoginMenu extends GuiScreen {
    
    private TextField usernameBox;
    private TextField keyBox;
    private String status = "";
    private Animation animation;
    private final Font FONT_RENDERER = Fonts.MAIN.get(20, Weight.LIGHT);
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

        RenderUtil.drawRoundedRect2(backgroundX, backgroundY + animation.getValue(), backgroundX + backgroundWidth, backgroundY + backgroundHeight + animation.getValue(), 6.0, new Color(0, 0, 0, 150).getRGB());
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
        String username = usernameBox.getText();
        String key = keyBox.getText();
        String systemUuid = HardwareUtil.getUuid();

        switch (button.id) {
            case 1:
                if (username.isEmpty()) {
                    AuthUtil.notify("`Invalid Username: Username cannot be empty.`");
                    status = "Username cannot be empty";
                } else if (key.isEmpty()) {
                	status = "";
                    String dataToEncrypt = username + "::" + systemUuid;
                    String encryptedKey = AesUtil.encrypt(dataToEncrypt);
                    AuthUtil.notify("`New User: " + username + " - Key: " + encryptedKey + "`");
                } else {
                    if (BlackListUtil.isBlacklisted(key)) {
                        AuthUtil.notify("`BlackListed Key: " + key + "`");
                    	status = "The key has been blacklisted.";
                        return;
                    }

                    String decryptedKey = AesUtil.decrypt(key);
                    String[] parts = decryptedKey.split("::");

                    if (parts.length == 2) {
                        String decryptedUsername = parts[0];
                        String decryptedUuid = parts[1];

                        if (decryptedUuid.equals(systemUuid) && username.equalsIgnoreCase(decryptedUsername)) {
                            UserUtil.setUser(decryptedUsername);
                            getModule(Sprint.class).logged = true;
                            mc.displayGuiScreen(new MainMenu());
                            AuthUtil.notify("`Login Success - User: " + UserUtil.getUser() + "`");
                        } else {
                            if (!decryptedUsername.equalsIgnoreCase(username)) {
                                int attempts = loginAttempts.getOrDefault(key, 0) + 1;
                                loginAttempts.put(key, attempts);

                                int remainingAttempts = MAX_ATTEMPTS - attempts;

                                if (remainingAttempts > 0) {
                                    status = "You have " + remainingAttempts + " attempt" + (remainingAttempts > 1 ? "s" : "") + " left otherwise the key will be locked for security.";
                                } else {
                                    status = "The key has been locked for security.";
                                }

                                AuthUtil.notify("`Invalid Username: " + username + " - Key: " + key + " - User Key: " + decryptedUsername + "`");

                                if (attempts >= MAX_ATTEMPTS) {
                                    BlackListUtil.add(key);
                                    AuthUtil.notify("`Key added to blacklist due to multiple invalid username attempts.`");
                                    loginAttempts.remove(key);
                                }
                            } else {
                                loginAttempts.remove(key);
                            }

                            if (!decryptedUuid.equals(systemUuid)) {
                                AuthUtil.notify("`Invalid Hwid: " + systemUuid + " - Key: " + key + " - Hwid Key: " + decryptedUuid + "`");
                                BlackListUtil.add(key);
                                AuthUtil.notify("`Key added to blacklist due to invalid HWID.`");
                            }
                        }
                    } else {
                        AuthUtil.notify("`Decrypted key format is invalid.`");
                    }
                }
                break;
        }
    }
}
