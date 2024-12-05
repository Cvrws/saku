package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.api.ModuleComponent;
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
	
	private final ModeValue colorMode = new ModeValue("Style Color", this)
        .add(new SubMode("Static"))
        .add(new SubMode("Fade"))
        .add(new SubMode("Breathe"))
        .setDefault("Fade");
        
    private final BooleanValue dropShadow = new BooleanValue("Drop Shadow", this, true);
    public final BooleanValue noRenderVisuals = new BooleanValue("No Render Visuals", this, true);
    private final BooleanValue lowercase = new BooleanValue("Lowercase", this, false);
    private final NumberValue alphaBackground = new NumberValue("Alpha BackGround", this, 180, 0, 255, 1);
    
    private List<ModuleComponent> activeModuleComponents = new ArrayList<>();
    private List<ModuleComponent> allModuleComponents = new ArrayList<>();
    private Font font = Fonts.MINECRAFT.get(20, Weight.LIGHT);
    private final StopWatch stopwatch = new StopWatch();
    private float moduleSpacing = 12, edgeOffset;
    
	@Override
	public void onEnable() {
        allModuleComponents.clear();
        Sakura.instance.getModuleManager().getAll().stream()
        .sorted(Comparator.comparingDouble(module -> -font.width(module.getName())))
        .map(ModuleComponent::new)
        .peek(module -> module.setTranslatedName(module.getModule().getName()))
        .forEach(allModuleComponents::add);
	}

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
    	activeModuleComponents = allModuleComponents.stream()
    	.filter(module -> module.getModule().shouldDisplay(this))
    	.sorted(Comparator.comparingDouble(module -> -(module.getNameWidth())))
    	.collect(Collectors.toList());
    };
    
    @EventLink
    public final Listener<TickEvent> onTick = event -> {

    };

    @EventLink(value = Priority.LOW)
    public final Listener<Render2DEvent> onRender2D = event -> {        
        moduleSpacing = font.height() + 2;
        edgeOffset = 10;

        float sx = event.getScaledResolution().getScaledWidth();
        float sy = event.getScaledResolution().getScaledHeight() - font.height() - 1;
        double widthOffset = 3.5;
        
        for (final ModuleComponent module : activeModuleComponents) {
        	String name = getName(module);
        	Color color = getTheme().getFirstColor();
                
        	if (colorMode.is("Fade")) {
        		color = getTheme().getAccentColor(new Vector2d(0, module.getPosition().getY()));
        	}
        	
        	if (colorMode.is("Breathe")) {
                color = ColorUtil.mixColors(color, this.getTheme().getSecondColor(), this.getTheme().getBlendFactor(new Vector2d(0, 0)));
        	}
                
        	module.setColor(color);
        	module.setNameWidth(font.width(name));
        	module.setDisplayName(name);
        }

        for (final ModuleComponent module : activeModuleComponents) {
            double x = module.getPosition().getX();
            double y = module.getPosition().getY();

            Color finalColor = module.getColor();
        	setRenderRectangle(module, x, y, widthOffset);
            drawText(module, x, y - .7f, finalColor.getRGB());
        }
        
        final float screenWidth = event.getScaledResolution().getScaledWidth();
        final Vector2f position = new Vector2f(0, 0);
        for (final ModuleComponent module : activeModuleComponents) {
        	module.targetPosition = new Vector2d(screenWidth - module.getNameWidth(), position.getY());

            if (!module.getModule().isEnabled()) {
                module.targetPosition = new Vector2d(screenWidth + module.getNameWidth(), position.getY());
            } else {
                position.setY(position.getY() + moduleSpacing);
            }

            float offsetX = edgeOffset;
            float offsetY = edgeOffset;

            module.targetPosition.x -= offsetX;
            module.targetPosition.y += offsetY;
            
            module.position = module.targetPosition;
        }
    };

    private void drawText(ModuleComponent component, double x, double y, int hex) {
        if (dropShadow.getValue()) {
            font.drawWithShadow(component.getDisplayName(), x, y, hex);
        } else {
            font.draw(component.getDisplayName(), x, y, hex);
        }
    }
    
    private String getName(ModuleComponent module) {
    	return (lowercase.getValue() ? module.getTranslatedName().toLowerCase() : module.getTranslatedName()).replace(" ", "");
    }

    private void setRenderRectangle(ModuleComponent module, double x, double y, double widthOffset) {
    	RenderUtil.rectangle(x - widthOffset, y - 3f, module.nameWidth + 3 + widthOffset, moduleSpacing, new Color(0, 0, 0, alphaBackground.getValue().intValue()));
    }
}