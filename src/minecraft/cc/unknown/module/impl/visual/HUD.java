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
import cc.unknown.event.impl.input.ModuleToggleEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "HUD", description = "Renderiza los modulos del cliente.", category = Category.VISUALS, autoEnabled = true)
public final class HUD extends Module {

    private final ModeValue colorMode = new ModeValue("Style Color", this)
            .add(new SubMode("Static"))
            .add(new SubMode("Custom"))
            .add(new SubMode("Fade"))
            .setDefault("Fade");

    private final BooleanValue lgbt = new BooleanValue("LBGT+", this, false);
    private final BooleanValue bloom = new BooleanValue("Bloom", this, false);
    private final BooleanValue renderCategories = new BooleanValue("Render Category", this, true);
    public final BooleanValue hideCombat = new BooleanValue("Hide Combat", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hideVisuals = new BooleanValue("Hide Visuals", this, true, () -> !renderCategories.getValue());
    public final BooleanValue hideMovement = new BooleanValue("Hide Movement", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hideGhost = new BooleanValue("Hide Ghost", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hideLatency = new BooleanValue("Hide Latency", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hideOther = new BooleanValue("Hide Other", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hidePlayer = new BooleanValue("Hide Player", this, false, () -> !renderCategories.getValue());
    public final BooleanValue hideWorld = new BooleanValue("Hide World", this, false, () -> !renderCategories.getValue());
    private final BooleanValue lowercase = new BooleanValue("Lowercase", this, false);
    private final NumberValue alphaBackground = new NumberValue("Alpha BackGround", this, 180, 0, 255, 1);

    private List<ModuleComponent> activeModuleComponents = new ArrayList<>();
    private List<ModuleComponent> allModuleComponents = new ArrayList<>();
    private final StopWatch stopwatch = new StopWatch();
    private float moduleSpacing = 12, edgeOffset;

    @Override
    public void onEnable() {
        allModuleComponents.clear();
        allModuleComponents = Sakura.instance.getModuleManager().getAll().stream()
                .sorted(Comparator.comparingDouble(module -> -mc.fontRendererObj.width(module.getName())))
                .map(ModuleComponent::new)
                .peek(module -> module.translatedName = module.module.getName())
                .collect(Collectors.toList());
    }

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        activeModuleComponents = allModuleComponents.stream()
                .filter(module -> module.module.shouldDisplay(this))
                .sorted(Comparator.comparingDouble(module -> -module.nameWidth))
                .collect(Collectors.toList());
    };

    @EventLink(value = Priority.LOW)
    public final Listener<Render2DEvent> onRender2D = event -> {
        moduleSpacing = mc.fontRendererObj.height() + 2;
        edgeOffset = 4;
        
        if (lgbt.getValue()) {
    		final String name = "CRIS";
    		mc.fontRendererObj.drawCentered(name, event.getScaledResolution().getScaledWidth() / 2F,
    				event.getScaledResolution().getScaledHeight() - 89.5F, new Color(0, 0, 0, 200).hashCode());
    		mc.fontRendererObj.drawCentered(name, event.getScaledResolution().getScaledWidth() / 2F,
    				event.getScaledResolution().getScaledHeight() - 90, getTheme().getAccentColor().getRGB());
        }

        float sx = event.getScaledResolution().getScaledWidth();
        float sy = event.getScaledResolution().getScaledHeight() - mc.fontRendererObj.height() - 4;
        double widthOffset = 4;
        float totalHeight = activeModuleComponents.size() * moduleSpacing;

        for (ModuleComponent module : activeModuleComponents) {
            String name = (lowercase.getValue() ? module.translatedName.toLowerCase() : module.translatedName).replace(" ", "");
            Color color = getTheme().getFirstColor();

            if (colorMode.is("Fade")) {
                color = getTheme().getAccentColor(new Vector2d(0, module.position.getY()));
            }

            module.color = color;
            module.nameWidth = mc.fontRendererObj.width(name);
            module.displayName = name;
        }

        float screenWidth = event.getScaledResolution().getScaledWidth();
        Vector2f position = new Vector2f(0, 0);

        for (ModuleComponent module : activeModuleComponents) {
            module.targetPosition = new Vector2d(screenWidth - module.nameWidth, position.getY());

            if (!module.module.isEnabled()) {
                module.targetPosition = new Vector2d(screenWidth + module.nameWidth, position.getY());
            } else {
                position.setY(position.getY() + moduleSpacing);
            }

            module.targetPosition.x -= edgeOffset;
            module.targetPosition.y += edgeOffset;
            module.position = module.targetPosition;
        }

        for (ModuleComponent module : activeModuleComponents) {
            double x = module.position.getX();
            double y = module.position.getY();
            Color finalColor = module.color;

            setRenderRectangle(module, x, y, widthOffset, totalHeight);
            mc.fontRendererObj.drawWithShadow(module.displayName, x, y - 0.7f, finalColor.getRGB());
        }
    };

    private void setRenderRectangle(ModuleComponent module, double x, double y, double widthOffset, float totalHeight) {
        float rectangleWidth = (float) (module.nameWidth + 3 + widthOffset);

        if (bloom.getValue()) {
            RenderUtil.drawBloomShadow((float) (x - widthOffset), (float) (y - 3f), rectangleWidth, (float) moduleSpacing + 4, 6, 0, new Color(0, 0, 0, alphaBackground.getValue().intValue()));
        }

        int color = new Color(0, 0, 0, alphaBackground.getValue().intValue()).getRGB();

        Gui.drawRect((int) (x - widthOffset), (int) (y - 3f), (int) (x - widthOffset + rectangleWidth), (int) (y - 3f + moduleSpacing), color);
    }
    
    @RequiredArgsConstructor
    public final class ModuleComponent {

        public final Module module;
        public Vector2d position = new Vector2d(5000, 0);
        public Vector2d targetPosition = new Vector2d(5000, 0);
        public float nameWidth = 0;
        public Color color = Color.WHITE;
        public String translatedName = "";
        public String displayName = "";
    }
}