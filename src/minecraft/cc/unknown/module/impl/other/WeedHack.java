package cc.unknown.module.impl.other;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Weed Hack", description = "Nano-particle atom biosphere velocity packet", category = Category.OTHER)
public final class WeedHack extends Module {
	
	@Override
	public void onEnable() {
		Display.setTitle("Weed Hack Premium Beta");
		mc.setWindowIcon("sakura/icon/weed16.jpg", "sakura/icon/weed32.jpg");
	}

	@Override
	public void onDisable() {
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
