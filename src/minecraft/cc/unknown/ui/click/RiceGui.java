package cc.unknown.ui.click;

import java.io.IOException;
import java.text.Collator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.component.BoundsNumberValueComponent;
import cc.unknown.ui.click.component.ModuleComponent;
import cc.unknown.ui.click.component.NumberValueComponent;
import cc.unknown.ui.click.component.SidebarComponent;
import cc.unknown.ui.click.component.StringValueComponent;
import cc.unknown.ui.click.component.ValueComponent;
import cc.unknown.ui.click.screen.Colors;
import cc.unknown.ui.click.screen.HomeScreen;
import cc.unknown.ui.click.screen.Screen;
import cc.unknown.ui.click.screen.ThemeScreen;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.gui.GUIUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

@Getter
public class RiceGui extends GuiScreen implements Accessor {

    public Vector2f position = new Vector2f(-1, -1);
    public Vector2f scale = new Vector2f(320 * 1.3f, 260 * 1.3f);

    /* Sidebar */
    public SidebarComponent sidebar = new SidebarComponent();

    /* Selected Screen */
    public Screen selectedScreen = Category.SEARCH.getClickGUIScreen();
    public Screen renderedScreen = selectedScreen;
    public Screen lastScreen = selectedScreen;

    public float draggingOffsetX, draggingOffsetY;
    public boolean dragging;
    public StopWatch timeInCategory = new StopWatch();
    public StopWatch stopwatch = new StopWatch();

    public ConcurrentLinkedQueue<ModuleComponent> moduleList = new ConcurrentLinkedQueue<>();

    public Vector2f mouse;
    public double animationTime, opacity, animationVelocity;

    public int round = 7;

    Vector2d translate;
    public ValueComponent overlayPresent;
    public Vector2f moduleDefaultScale = new Vector2f(283, 38);
    public Animation scaleAnimation = new Animation(Easing.EASE_IN_EXPO, 300);
    public Animation opacityAnimation = new Animation(Easing.EASE_IN_EXPO, 300);

    public void rebuildModuleCache() {
        moduleList.clear();
        java.util.List<Module> sortedModules = Sakura.instance.getModuleManager().getAll();
        sortedModules.sort((o1, o2) -> Collator.getInstance().compare(o1.getName(), o2.getName()));
        sortedModules.forEach(module -> moduleList.add(new ModuleComponent(module)));
    }

    @Override
    public void initGui() {
        if (moduleList == null || moduleList.isEmpty()) {
            rebuildModuleCache();
        }

        round = 12;
        scaleAnimation.reset();
        scaleAnimation.setValue(0);

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        lastScreen = selectedScreen;
        timeInCategory.reset();
        timeInCategory.setMillis(System.currentTimeMillis() - 150);

        Keyboard.enableRepeatEvents(true);
        stopwatch.reset();
        selectedScreen.onInit();

        if (this.position.x < 0 || this.position.y < 0 ||
        		this.position.x + this.scale.x > scaledResolution.getScaledWidth() ||
        		this.position.y + this.scale.y > scaledResolution.getScaledHeight()) {
        	this.position.x = scaledResolution.getScaledWidth() / 2f - this.scale.x / 2;
        	this.position.y = scaledResolution.getScaledHeight() / 2f - this.scale.y / 2;
        }

        moduleList.forEach(moduleComponent -> {
        	moduleComponent.getValueList().forEach(valueComponent -> {
        		if (valueComponent instanceof NumberValueComponent) {
        			((NumberValueComponent) valueComponent).updateSliders();
        		} else if (valueComponent instanceof BoundsNumberValueComponent) {
        			((BoundsNumberValueComponent) valueComponent).updateSliders();
        		}
        	});
        });
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        dragging = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
        this.mouse = new Vector2f(mouseX, mouseY);
        
        if (mouse == null) {
            return;
        }
        
        scale = new Vector2f(390, 300);

        final Minecraft mc = Minecraft.getInstance();

        mouseX = (int) mouse.x;
        mouseY = (int) mouse.y;
        partialTicks = mc.timer.renderPartialTicks;

        if (dragging) {

            if (this.selectedScreen instanceof ThemeScreen) {
                ((ThemeScreen) selectedScreen).resetAnimations();
            }

            position.x = mouseX + draggingOffsetX;
            position.y = mouseY + draggingOffsetY;
        }

        opacityAnimation.setEasing(mc.currentScreen == Sakura.instance.getClickGui() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
        opacityAnimation.setDuration(mc.currentScreen == Sakura.instance.getClickGui() ? 300 : 100);
        opacityAnimation.animate(mc.currentScreen == Sakura.instance.getClickGui() ? 1 : 0);
        opacity = opacityAnimation.getValue();

        scaleAnimation.setEasing(mc.currentScreen == Sakura.instance.getClickGui() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
        scaleAnimation.animate(mc.currentScreen == Sakura.instance.getClickGui() ? 1 : 0);
        animationTime = scaleAnimation.getValue();

        if (mc.currentScreen == Sakura.instance.getClickGui() && animationTime == 0) animationTime = 0.01;

        if (animationTime == 0) {
            Sakura.instance.getModuleManager().get(ClickGUI.class).setEnabled(false);
            return;
        }

        // Opening and closing animation gl
        translate = new Vector2d((position.x + scale.x / 2f) * (1 - animationTime), (position.y + scale.y / 2f) * (1 - animationTime));

        GlStateManager.pushMatrix();

        if (animationTime != 1) {
        	GlStateManager.translate(translate.x, translate.y, 0);
        	GlStateManager.scale(animationTime, animationTime, 0);
        }

        /* Background */
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, round, Colors.BACKGROUND.get());

        /* Stop objects from going outside the ClickGUI */
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int padding = 1;
        RenderUtil.scissor(
        		position.x * animationTime + translate.x + padding, 
        		position.y * animationTime + translate.y + padding, 
        		scale.x * animationTime - padding * 2, 
        		scale.y * animationTime - padding * 2);

        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 0);

        int length = 0;

        /* Renders screen depending on selected category */
        (renderedScreen = timeInCategory.finished(length) ? selectedScreen : lastScreen).onDraw(mouseX, mouseY, partialTicks);

        for (int i = 0; i <= 1; i++) {
            double radius = i * 50;
            RenderUtil.circle(position.x + sidebar.sidebarWidth - radius / 2, position.y + scale.y / 2 - radius / 2,
                    radius, ColorUtil.withAlpha(getTheme().getFirstColor(), 1));
        }
        
        /* Sidebar */
        sidebar.renderSidebar(mouseX, mouseY);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();    
        GL11.glPopMatrix();

        stopwatch.reset();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (GUIUtil.mouseOver(position.x, position.y, scale.x, 15, mouseX, mouseY) && overlayPresent == null) {
            draggingOffsetX = position.x - mouseX;
            draggingOffsetY = position.y - mouseY;
            dragging = true;
        }

        // Only register click if within the ClickGUI
        else if (GUIUtil.mouseOver(position.getX(), position.getY(), scale.getX(), scale.getY(), mouseX, mouseY)) {
            if (overlayPresent == null) sidebar.clickSidebar(mouseX, mouseY, mouseButton);
            selectedScreen.onClick(mouseX, mouseY, mouseButton);
        }

        overlayPresent = null;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        /* Registers the mouse being released */
        dragging = false;

        selectedScreen.onMouseRelease();
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if ("abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(typedChar).toLowerCase()) && selectedScreen.automaticSearchSwitching() && !getClickGUI().activeTextBox()) {
            this.switchScreen(Category.SEARCH);
        }

        selectedScreen.onKey(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void switchScreen(final Category category) {
        if (!category.getClickGUIScreen().equals(this.selectedScreen)) {
            lastScreen = this.getClickGUI().selectedScreen;
            selectedScreen = category.getClickGUIScreen();

            this.timeInCategory.reset();
            selectedScreen.onInit();
            
            final HomeScreen search = ((HomeScreen) Category.SEARCH.getClickGUIScreen());
            search.relevantModules = search.getRelevantModules(search.searchBar.getText());
        }
    }

    public boolean activeTextBox() {
        for (final ModuleComponent moduleComponent : moduleList) {
            for (final ValueComponent value : moduleComponent.getValueList()) {
                if (value instanceof StringValueComponent && value.position != null && ((StringValueComponent) value).textBox.selected && !((StringValueComponent) value).textBox.drawn.finished(50)) {
                    return true;
                } else if (value instanceof NumberValueComponent && ((NumberValueComponent) value).valueDisplay.isSelected() && !((NumberValueComponent) value).valueDisplay.drawn.finished(50)) {
                    return true;
                } else if (value instanceof BoundsNumberValueComponent && ((BoundsNumberValueComponent) value).valueDisplay.isSelected() && !((BoundsNumberValueComponent) value).valueDisplay.drawn.finished(50)) {
                    return true;
                }
            }
        }

        return false;
    }
}