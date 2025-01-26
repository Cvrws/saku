package cc.unknown.ui.menu.saku;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import cc.unknown.Sakura;
import cc.unknown.ui.menu.AltManager;
import cc.unknown.ui.menu.saku.api.Button;
import cc.unknown.ui.menu.saku.api.RainSystem;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.sound.SoundUtil;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class SakuMenu extends GuiMainMenu {

    private Font fontRenderer;
    private RainSystem rainParticleSystem;
    private final Map<Integer, Consumer<GuiButton>> buttonActions = new HashMap<>();

    public SakuMenu() {
        fontRenderer = Fonts.MAISON.get(18, Weight.NONE);
        buttonActions.put(0, button -> mc.displayGuiScreen(new GuiSelectWorld(this)));
        buttonActions.put(1, button -> mc.displayGuiScreen(new GuiMultiplayer(this)));
        buttonActions.put(2, button -> mc.displayGuiScreen(new AltManager()));
        buttonActions.put(3, button -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        final String[] keys = {"SinglePlayer", "MultiPlayer", "Alt Manager", "Settings"};
        final int initHeight = height / 4 + 36;
        final int objHeight = 20;
        final int objWidth = 100;
        final int buttonSpacing = 22;
        final int xMid = width / 2 - objWidth / 2;

        for (int i = 0; i < keys.length; i++) {
            String translatedString = I18n.format(keys[i]);
            int yOffset = initHeight + i * buttonSpacing;
            this.buttonList.add(new Button(i, xMid, yOffset, objWidth, objHeight, translatedString));
        }
        
        rainParticleSystem = new RainSystem(width, height);
    }

    @Override
    @SneakyThrows
    protected void actionPerformed(final GuiButton button) {
        buttonActions.getOrDefault(button.id, b -> {}).accept(button);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);

        String title = "§fSakura Client";
        String name = String.format("§fLogged in as §7%s", mc.getSession().getUsername());
        
        if (Sakura.instance.firstStart) {
        	SoundUtil.playSound();
        	Sakura.instance.firstStart = false;
        }

        fontRenderer.drawWithShadow(title, 2.0f, height - 10, -1);
        fontRenderer.drawWithShadow(name, width - fontRenderer.width(name) - 2, height - 10, -1);
        
        rainParticleSystem.update();
        rainParticleSystem.render();
    }
}