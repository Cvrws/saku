package cc.unknown.module.impl.other;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.Display;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.HUD;
import cc.unknown.script.api.RenderAPI;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.render.RenderUtil;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Weed Hack", description = "Nano-particle atom biosphere velocity packet", category = Category.OTHER)
public final class WeedHack extends Module {
	
	@Override
	public void onEnable() {
        HUD hud = getModule(HUD.class);
        if (hud != null && hud.isEnabled()) {
        	hud.toggle();
        }
                
		Display.setTitle("Weed Hack Premium Beta");
		mc.setWindowIcon("sakura/icon/weed16.jpg", "sakura/icon/weed32.jpg");
	}

	@Override
	public void onDisable() {
        HUD hud = getModule(HUD.class);
        if (hud != null && !hud.isEnabled()) {
        	hud.toggle();
        }
        
		Display.setTitle(Sakura.NAME + " " + Sakura.VERSION);
		mc.setWindowIcon();
	}

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		int imageWidth = 100, imageHeight = 100;
		int x = event.getScaledResolution().getScaledWidth() - 680;
		int y = event.getScaledResolution().getScaledHeight() - 360;

		RenderUtil.image(new ResourceLocation("sakura/images/weed.png"), x, y, imageWidth, imageHeight);
	};
}
