package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.bluearchive.HaloRenderer;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Stickers", description = "Stickers/Pegatinas", category = Category.VISUALS)
public final class Stickers extends Module {
	
	private final ModeValue haloType = new ModeValue("Halo Type", this)
			.add(new SubMode("Shiroko"))
			.add(new SubMode("Hoshino"))
			.add(new SubMode("Aris"))
			.add(new SubMode("Natsu"))
			.add(new SubMode("Reisa"))
			.add(new SubMode("None"))
			.setDefault("None");

	private final BooleanValue showInFirstPerson = new BooleanValue("First Person", this, true, () -> haloType.is("None"));

	private final ModeValue stickerType = new ModeValue("Sticker Type", this)
			.add(new SubMode("Shiroko"))
			.add(new SubMode("53XO"))
			.add(new SubMode("Mika"))
			.add(new SubMode("None"))
			.setDefault("53XO");
	
	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);
	
    private ScaledResolution sr = new ScaledResolution(mc);
    
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        Vector2d pos = position.position;

        StickerData sticker = null;
        
        switch (stickerType.getValue().getName()) {
            case "Shiroko":
                sticker = new StickerData("sakura/images/shiroko.png", 85, 160);
                break;
            case "53XO":
                sticker = new StickerData("sakura/images/jupo.png", 85, 130);
                break;
            case "Mika":
                sticker = new StickerData("sakura/images/mika.png", 95, 160);
                break;
        }

        if (sticker != null) {
            RenderUtil.image(new ResourceLocation(sticker.imagePath), (int) pos.x, (int) pos.y, sticker.width, sticker.height);
        }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.getValue()) return;
        
        switch (haloType.getValue().getName()) {
            case "Shiroko":
                HaloRenderer.drawShirokoHalo(event);
                break;
            case "Hoshino":
                HaloRenderer.drawHoshinoHalo(event);
                break;
            case "Aris":
                HaloRenderer.drawArisHalo(event);
                break;
            case "Natsu":
                HaloRenderer.drawNatsuHalo(event);
                break;
            case "Reisa":
                HaloRenderer.drawReisaHalo(event);
                break;
        }
    };
    
    @AllArgsConstructor
    class StickerData {
        String imagePath;
        int width;
        int height;
    }
}