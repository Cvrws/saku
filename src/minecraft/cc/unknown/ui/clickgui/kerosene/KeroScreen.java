package cc.unknown.ui.clickgui.kerosene;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.Value;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class KeroScreen extends GuiScreen {
    private int settingsLeft, settingsTop, settingsRight, settingsBottom;
    private List<CategoryButton> categoryButtons;
    private List<ModuleButton> moduleButtons;
    private Category selectedCategory;
    private int guiLeft, guiTop, guiRight, guiBottom;
    private final int MIN_WIDTH = 660;
    private final int MIN_HEIGHT = 400;
    private static final int TOP_BAR_HEIGHT = 20;
    private static final int CLOSE_BUTTON_SIZE = 15;
    private Module selectedModule;
    private List<ValueComponent> valueComponents;


    public KeroScreen() {
        this.categoryButtons = new ArrayList<>();
        this.moduleButtons = new ArrayList<>();
        this.selectedCategory = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        //BlurUtil.initFboAndShader();
        categoryButtons.clear();

        ScaledResolution rs = new ScaledResolution(mc);
        int scaledWidth = rs.getScaledWidth();
        int scaledHeight = rs.getScaledHeight();

        int guiWidth = Math.max(scaledWidth / 2, MIN_WIDTH);
        int guiHeight = Math.max(scaledHeight / 2, MIN_HEIGHT);

        guiLeft = (scaledWidth - guiWidth) / 2;
        guiTop = (scaledHeight - guiHeight) / 2;
        guiRight = guiLeft + guiWidth;
        guiBottom = guiTop + guiHeight;

        settingsLeft = guiRight - (int) (guiWidth * 0.80f);
        settingsTop = guiTop + TOP_BAR_HEIGHT + 67;
        settingsRight = guiRight - (int) 212f; // anchura
        settingsBottom = guiBottom;

        int categoryWidth = 45;
        int spacing = 5;

        int totalWidth = (categoryWidth + spacing) * Category.values().length - spacing;
        int startX = guiLeft + (guiWidth - totalWidth) / 2;

        for (Category category : Category.values()) {
            if (category != Category.SEARCH) {
                categoryButtons.add(new CategoryButton(category, startX, guiTop + TOP_BAR_HEIGHT + 8, categoryWidth, 20));
                startX += categoryWidth + spacing;
            }
        }

        if (selectedCategory != null) {
            updateModuleButtons();
        }

        if (selectedModule != null) {
            updateValueComponents();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

      //  BlurUtil.blur(guiLeft, guiTop, guiRight - guiLeft, guiBottom - guiTop, 100f, partialTicks);
        drawHorizontalLine(guiLeft + 8, guiRight - 1, guiTop + TOP_BAR_HEIGHT + 35, new Color(255, 255, 255, 100).getRGB());
        drawVerticalLine(settingsLeft - 4, guiTop + TOP_BAR_HEIGHT + 380, guiTop, new Color(255, 255, 255, 100).getRGB());
        RenderUtil.drawBloomShadow(guiLeft, guiTop, guiRight - guiLeft, guiBottom - guiTop, 6, new Color(0, 0, 0, 160));
        Fonts.ROBOTO.get(15, Weight.LIGHT).drawCentered("x",
                guiRight - CLOSE_BUTTON_SIZE / 2 - 5,
                guiTop + TOP_BAR_HEIGHT / 2 - Fonts.ROBOTO.get(15, Weight.LIGHT).height() / 2,
                Color.WHITE.getRGB());



        for (CategoryButton button : categoryButtons) {
            button.draw(mouseX, mouseY);
        }

        if (selectedCategory != null) {
            // drawhori y vertical estaban aqui
            for (ModuleButton button : moduleButtons) {
                button.draw(mouseX, mouseY);
            }
        }

        if (selectedModule != null && valueComponents != null) {

            //BUG
            //drawRect(settingsLeft, settingsTop - 25, settingsRight, settingsBottom, new Color(30, 234, 111, 200).getRGB());
            for (ValueComponent component : valueComponents) {
                component.drawComponent(mouseX, mouseY);
            }

        }

        Fonts.MONSERAT.get(15, Weight.BOLD).draw("CLICKGUI", guiLeft + 8, guiTop + TOP_BAR_HEIGHT / 1.8 - Fonts.ROBOTO.get(15, Weight.LIGHT).height() / 2, new Color(230, 230, 230, 200).getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int closeButtonX = guiRight - CLOSE_BUTTON_SIZE - 5;
        int closeButtonY = guiTop;
        if (mouseX >= closeButtonX && mouseX <= guiRight - 5 && mouseY >= closeButtonY && mouseY <= closeButtonY + TOP_BAR_HEIGHT) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
            return;
        }

        for (CategoryButton button : categoryButtons) {
            if (button.isMouseOver(mouseX, mouseY)) {
                selectedCategory = button.getCategory();
                updateModuleButtons();
                selectedModule = null;
                valueComponents = null;
                return;
            }
        }

        if (selectedCategory != null) {
            for (ModuleButton button : moduleButtons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        button.getModule().toggle();
                    } else if (mouseButton == 1) {
                        selectedModule = button.getModule();
                        updateValueComponents();
                    }
                    return;
                }
            }
        }

        if (selectedModule != null && valueComponents != null) {
            if (mouseX >= settingsLeft && mouseX <= settingsRight && mouseY >= settingsTop - 25 && mouseY <= settingsBottom) {
                for (ValueComponent component : valueComponents) {
                    if (component.isMouseOver(mouseX, mouseY)) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                        return;
                    }
                }
            }
        }

    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (selectedModule != null && valueComponents != null) {
            for (ValueComponent component : valueComponents) {
                component.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (selectedModule != null && valueComponents != null) {
            for (ValueComponent component : valueComponents) {
                component.mouseDragged(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        //BlurUtil.destroy();
        super.onGuiClosed();
    }

    private void updateValueComponents() {
        valueComponents = new ArrayList<>();
        if (selectedModule != null) {
            int settingsPanelWidth = settingsRight - settingsLeft;
            int columnWidth = (settingsPanelWidth + 360) / 5;
            int startX = settingsLeft;
            int startY = settingsTop;
            int componentHeight = 23;
            int maxY = settingsBottom - componentHeight;
            int columnSpacing = 2;

            int currentColumn = 0;

            for (Value<?> value : selectedModule.getValues()) {
                if (startY + componentHeight > maxY) {
                    currentColumn++;
                    if (currentColumn >= 5) break;
                    startX = settingsLeft + 5 + (columnWidth + columnSpacing) * currentColumn;
                    startY = settingsTop;
                }

                valueComponents.add(new ValueComponent(value, startX, startY, columnWidth - columnSpacing, componentHeight, selectedModule));


                if (value instanceof BooleanValue || value instanceof ModeValue) {
                    startY += componentHeight - 6;
                } else {
                    startY += componentHeight;
                }
            }


            if (startY + componentHeight <= maxY || currentColumn < 4) {
                if (startY + componentHeight > maxY) {
                    currentColumn++;
                    startX = settingsLeft + 5 + (columnWidth + columnSpacing) * currentColumn;
                    startY = settingsTop;
                }
                valueComponents.add(new ValueComponent(null, startX, startY, columnWidth - columnSpacing, componentHeight, selectedModule));
            }
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {

        	this.mc.displayGuiScreen(null);
        	if (this.mc.currentScreen == null) {
        		this.mc.setIngameFocus();
        	}
        } else {

            if (valueComponents != null) {
                for (ValueComponent component : valueComponents) {
                    if (component != null) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
    }

    private void updateModuleButtons() {
        moduleButtons.clear();
        if (selectedCategory != null && selectedCategory != Category.SEARCH) {
            int y = guiTop + 70;
            int moduleWidth = guiRight - guiLeft - 510;

            for (Module module : Sakura.instance.getModuleManager().getModulesByCategory(selectedCategory)) {
                moduleButtons.add(new ModuleButton(module, guiLeft + 5, y, moduleWidth, 10));
                y += 10;
            }
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}