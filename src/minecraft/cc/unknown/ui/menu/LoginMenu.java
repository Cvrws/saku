package cc.unknown.ui.menu;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.api.Button;
import cc.unknown.ui.menu.api.TextField;
import cc.unknown.util.account.name.UsernameGenerator;
import cc.unknown.util.client.irc.UserUtil;
import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.font.Font;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public final class LoginMenu extends GuiScreen {
    
    private TextField usernameBox;
    private String status = "Esto es importante para el irc xd";
    private Animation animation;
    private final Font FONT_RENDERER = Fonts.ROBOTO.get(20, Weight.LIGHT);
    //private final Set<String> blacklist = new HashSet<>(Arrays.asList("Cv", "Cvr", "Cvrwed", "Kioshi", "Val"));
    
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
        this.buttonList.add(new Button(1, (int) (position.x - 140 + boxWidth + padding), (int) (position.y + boxHeight + padding), (int) buttonWidth,  (int) boxHeight, "Login"));
        animation = new Animation(Easing.EASE_OUT_QUINT, 600);
        animation.setStartValue(-200);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	BackgroundUtil.renderBackground(this);
        usernameBox.drawTextBox();

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
    }

    @Override
    @SneakyThrows
    protected void keyTyped(char typedChar, int keyCode) {
        usernameBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText().trim();


        if (button.id == 1) {
            Optional.of(username)
                .filter(u -> !u.isEmpty())
                .filter(u -> UsernameGenerator.validate(u, 4, 10))
                //.filter(u -> !blacklist.contains(u.toLowerCase()))
                .ifPresent(validUsername -> {
                    UserUtil.setUser(validUsername);
                    mc.displayGuiScreen(new MainMenu());
                });

            if (username.isEmpty()) {
                status = "No puedes tener un nombre vacío.";
            } else if (!UsernameGenerator.validate(username, 4, 20)) {
                status = "Tu usuario debe tener minimo 4 letras.";
            }/* else if (blacklist.contains(username.toLowerCase())) {
                status = "Este user esta blacklist.";
            }*/
        }
    }

}
