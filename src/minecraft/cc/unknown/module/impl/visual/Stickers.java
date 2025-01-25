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

	private final ModeValue stickerType = new ModeValue("Sticker Type", this)
			.add(new SubMode("Shiroko"))
			.add(new SubMode("Jupo"))
			.add(new SubMode("Mika"))
			.add(new SubMode("Ai Hoshino"))
			.add(new SubMode("Astolfo"))
			.add(new SubMode("Elf"))
			.add(new SubMode("Kiwi"))
			.add(new SubMode("Kumi"))
			.add(new SubMode("Kurumi"))
			.add(new SubMode("Magic"))
			.add(new SubMode("Mai"))
			.add(new SubMode("Megumin Cat"))
			.add(new SubMode("Utena"))
			.add(new SubMode("Uzaki Chan"))
			.add(new SubMode("Halflin"))
			.add(new SubMode("Komi San"))
			.add(new SubMode("Hideri"))
			.add(new SubMode("Gwen Bunny"))
			.add(new SubMode("Fujiwara"))
			.add(new SubMode("Akari"))
			.add(new SubMode("Typh"))
			.add(new SubMode("None"))
			.setDefault("Jupo");
	
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
            case "Jupo":
                sticker = new StickerData("sakura/images/jupo.png", 85, 130);
                break;
            case "Mika":
                sticker = new StickerData("sakura/images/mika.png", 95, 160);
                break;
            case "Ai Hoshino":
            	sticker = new StickerData("sakura/images/ai.png", 95, 160);
            	break;
            case "Astolfo":
            	sticker = new StickerData("sakura/images/astolfo.png", 95, 160);
            	break;
            case "Elf":
            	sticker = new StickerData("sakura/images/elf.png", 95, 160);
            	break;
            case "Kiwi":
            	sticker = new StickerData("sakura/images/kiwi.png", 95, 160);
            	break;
            case "Kumi":
            	sticker = new StickerData("sakura/images/kumi.png", 95, 160);
            	break;
            case "Kurumi":
            	sticker = new StickerData("sakura/images/kurumi.png", 100, 180);
            	break;
            case "Magic":
            	sticker = new StickerData("sakura/images/magic.png", 95, 160);
            	break;
            case "Mai":
            	sticker = new StickerData("sakura/images/mai.png", 95, 160);
            	break;
            case "Megumin Cat":
            	sticker = new StickerData("sakura/images/megumin.png", 95, 160);
            	break;
            case "Utena":
            	sticker = new StickerData("sakura/images/utena.png", 95, 160);
            	break;
            case "Uzaki Chan":
            	sticker = new StickerData("sakura/images/uzaki.png", 95, 160);
            	break;
            case "Halflin":
            	sticker = new StickerData("sakura/images/manolo.png", 95, 160);
            	break;
            case "Komi San":
            	sticker = new StickerData("sakura/images/komi.png", 95, 160);
            	break;
            case "Hideri":
            	sticker = new StickerData("sakura/images/hideri.png", 95, 160);
            	break;
            case "Fujiwara":
            	sticker = new StickerData("sakura/images/fujiwara.png", 95, 160);
            	break;
            case "Gwen Bunny":
            	sticker = new StickerData("sakura/images/bunny.png", 95, 160);
            	break;
            case "Akari":
            	sticker = new StickerData("sakura/images/akari.png", 95, 160);
            	break;
            case "Typh":
            	sticker = new StickerData("sakura/images/typh.png", 95, 160);
            	break;
        }

        if (sticker != null) {
            RenderUtil.image(new ResourceLocation(sticker.imagePath), (int) pos.x, (int) pos.y, sticker.width, sticker.height);
        }
    };
    
    @AllArgsConstructor
    class StickerData {
        String imagePath;
        int width;
        int height;
    }
}