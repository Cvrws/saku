package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ModuleToggleEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.api.ModuleComponent;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.geometry.Vector2d;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "HUD", description = "Renderiza los modulos del cliente.", category = Category.VISUALS, autoEnabled = true)
public final class HUD extends Module {
	
	private final ModeValue colorMode = new ModeValue("ArrayList Color Mode", this)
        .add(new SubMode("Static"))
        .add(new SubMode("Fade"))
        .add(new SubMode("Breathe"))
        .setDefault("Fade");
	
    public final ModeValue modulesToShow = new ModeValue("Modules to Show", this)
        .add(new SubMode("All"))
        .add(new SubMode("Exclude render"))
        .add(new SubMode("Only bound"))
        .setDefault("Exclude render");
        
    private final BooleanValue dropShadow = new BooleanValue("Drop Shadow", this, true);
    private final BooleanValue lowercase = new BooleanValue("Lowercase", this, false);
    private final BooleanValue arrayAnimation = new BooleanValue("ArrayList Animation", this, true);
    private final BooleanValue removeSpaces = new BooleanValue("No Spaces", this, false);
    private final NumberValue alphaBackground = new NumberValue("Alpha BackGround", this, 180, 0, 255, 1);
    private final BooleanValue toggleNotifications = new BooleanValue("Toggle Notifications", this, false);
    
    private List<ModuleComponent> activeModuleComponents = new ArrayList<>();
    private List<ModuleComponent> allModuleComponents = new ArrayList<>();
    private Font widthComparator = Fonts.MAIN.get(20, Weight.LIGHT);
    private final StopWatch stopwatch = new StopWatch();
    private Font font = Fonts.MAIN.get(18, Weight.LIGHT);
    private float moduleSpacing = 12, edgeOffset;
    
	@Override
	public void onEnable() {
        allModuleComponents.clear();
        Sakura.instance.getModuleManager().getAll().stream()
                .sorted(Comparator.comparingDouble(module -> -widthComparator.width(module.getName())))
                .map(ModuleComponent::new)
                .forEach(allModuleComponents::add);

        updateTranslations();
	}
    
    @EventLink
    public final Listener<ModuleToggleEvent> onModuleToggle = event -> {
        if (toggleNotifications.getValue()) {
            NotificationComponent.post("Toggled", "Toggled " + event.getModule().getName() + " " + (event.getModule().isEnabled() ? "on" : "off"), 900);
        }
    };

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {;
    	if (stopwatch.finished(15000)) {
    		updateTranslations();
    		stopwatch.reset();
    	}

    	stopwatch.reset();
    	sortArrayList();
    };
    
    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	Font minecraft = Fonts.MINECRAFT.get();
    	if (!font.equals(minecraft)) font = minecraft;
        
        for (final ModuleComponent moduleComponent : activeModuleComponents) {
        	String name = getName(moduleComponent);
        	Color color = getTheme().getFirstColor();
                
        	if (colorMode.is("Fade")) {
        		color = getTheme().getAccentColor(new Vector2d(0, moduleComponent.getPosition().getY()));
        	}
        	
        	if (colorMode.is("Breathe")) {
                color = ColorUtil.mixColors(color, this.getTheme().getSecondColor(), this.getTheme().getBlendFactor(new Vector2d(0, 0)));
        	}
                
        	setVarious(moduleComponent, color, name);
        }
    };

    @EventLink(value = Priority.LOW)
    public final Listener<Render2DEvent> onRender2D = event -> {
        boolean minecraft = font == Fonts.MINECRAFT.get();
        
        moduleSpacing = font.height() + 2;
        widthComparator = font;
        edgeOffset = 10;

        float sx = event.getScaledResolution().getScaledWidth();
        float sy = event.getScaledResolution().getScaledHeight() - font.height() - 1;
        double widthOffset = 3.5;

        for (final ModuleComponent moduleComponent : activeModuleComponents) {
            double x = moduleComponent.getPosition().getX();
            double y = moduleComponent.getPosition().getY();

            Color finalColor = moduleComponent.getColor();
        	setRenderRectangle(moduleComponent, x, y, widthOffset);
            drawText(moduleComponent, x, y - .7f, finalColor.getRGB());
        }

        if (stopwatch.finished(150 * 50)) {
        	stopwatch.reset();
        	stopwatch.reset();
        }
        
        final float screenWidth = event.getScaledResolution().getScaledWidth();
        final Vector2f position = new Vector2f(0, 0);
        for (final ModuleComponent moduleComponent : activeModuleComponents) {
            moduleComponent.targetPosition = new Vector2d(screenWidth - moduleComponent.getNameWidth(), position.getY());

            if (!moduleComponent.getModule().isEnabled()) {
                moduleComponent.targetPosition = new Vector2d(screenWidth + moduleComponent.getNameWidth(), position.getY());
            } else {
                position.setY(position.getY() + moduleSpacing);
            }

            float offsetX = edgeOffset;
            float offsetY = edgeOffset;

            moduleComponent.targetPosition.x -= offsetX;
            moduleComponent.targetPosition.y += offsetY;
            
            if (arrayAnimation.getValue()) {
            	if (Math.abs(moduleComponent.getPosition().getX() - moduleComponent.targetPosition.x) > 0.5 || Math.abs(moduleComponent.getPosition().getY() - moduleComponent.targetPosition.y) > 0.5) {
                    moduleComponent.position.x = MathUtil.lerp(moduleComponent.position.x, moduleComponent.targetPosition.x, 1.5E-2F * stopwatch.getElapsedTime());
                    moduleComponent.position.y = MathUtil.lerp(moduleComponent.position.y, moduleComponent.targetPosition.y, 1.5E-2F * stopwatch.getElapsedTime());
                }
            } else {
                moduleComponent.position = moduleComponent.targetPosition;
            }
        }

        stopwatch.reset();
    };
    
    public void createArrayList() {
        allModuleComponents.clear();
        Sakura.instance.getModuleManager().getAll().stream()
                .sorted(Comparator.comparingDouble(module -> -widthComparator.width(module.getName())))
                .map(module -> new ModuleComponent(module))
                .forEach(allModuleComponents::add);

        updateTranslations();
    }

    private void updateTranslations() {
        allModuleComponents.forEach(moduleComponent -> 
            moduleComponent.setTranslatedName(moduleComponent.getModule().getName())
        );
    }

    private void sortArrayList() {
        activeModuleComponents = allModuleComponents.stream()
                .filter(moduleComponent -> moduleComponent.getModule().shouldDisplay(this))
                .sorted(Comparator.comparingDouble(module -> -(module.getNameWidth())))
                .collect(Collectors.toList());
    }
    
    private String removeNonAlphabetCharacters(String input) {
        return input.replaceAll("[^a-zA-Z]", "");
    }

    private void drawText(ModuleComponent component, double x, double y, int hex) {
        if (dropShadow.getValue()) {
            font.drawWithShadow(component.getDisplayName(), x, y, hex);
        } else {
            font.draw(component.getDisplayName(), x, y, hex);
        }
    }
    
    private String getName(ModuleComponent moduleComponent) {
    	return (lowercase.getValue() ? moduleComponent.getTranslatedName().toLowerCase() : moduleComponent.getTranslatedName()).replace(removeSpaces.getValue() ? " " : "", "");
    }

    private Color getColor() {
    	return new Color(0, 0, 0, alphaBackground.getValue().intValue());
    }
    
    private void setRenderRectangle(ModuleComponent moduleComponent, double x, double y, double widthOffset) {
    	RenderUtil.rectangle(x - widthOffset, y - 3f, moduleComponent.nameWidth + 3 + widthOffset, moduleSpacing, getColor());
    }
    
    private void setVarious(ModuleComponent moduleComponent, Color color, String name) {
        moduleComponent.setColor(color);
        moduleComponent.setNameWidth(font.width(name));
        moduleComponent.setDisplayName(name);
    }
}