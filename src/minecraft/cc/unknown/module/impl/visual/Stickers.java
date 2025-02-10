package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Stickers", description = "Stickers/Pegatinas", category = Category.VISUALS)
public final class Stickers extends Module {
	
	private final ListValue<WaifuList> stickerType = new ListValue<>("Sticker Type", this);

	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);
	
    private ScaledResolution sr = new ScaledResolution(mc);
    
	public Stickers() {
		for (WaifuList waifu : WaifuList.values()) {
			stickerType.add(waifu);
		}

		stickerType.setDefault(WaifuList.JUPO);
	}
    
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        Vector2d pos = position.position;
        StickerData sticker = null;
        
        switch (stickerType.getValue()) {
            case SHIROKO:
                sticker = new StickerData("sakura/images/shiroko.png", 85, 160);
                break;
            case JUPO:
                sticker = new StickerData("sakura/images/jupo.png", 85, 130);
                break;
            case MIKA:
                sticker = new StickerData("sakura/images/mika.png", 95, 160);
                break;
            case HOSHINO:
            	sticker = new StickerData("sakura/images/ai.png", 95, 160);
            	break;
            case ASTOLFO:
            	sticker = new StickerData("sakura/images/astolfo.png", 95, 160);
            	break;
            case ASTOLFO2:
            	sticker = new StickerData("sakura/images/astolfo2.png", 130, 160);
            	break;
            case ELF:
            	sticker = new StickerData("sakura/images/elf.png", 95, 160);
            	break;
            case KIWI:
            	sticker = new StickerData("sakura/images/kiwi.png", 95, 160);
            	break;
            case KUMI:
            	sticker = new StickerData("sakura/images/kumi.png", 95, 160);
            	break;
            case KURUMI:
            	sticker = new StickerData("sakura/images/kurumi.png", 100, 180);
            	break;
            case MAGIC:
            	sticker = new StickerData("sakura/images/magic.png", 95, 160);
            	break;
            case MAI:
            	sticker = new StickerData("sakura/images/mai.png", 95, 160);
            	break;
            case MEGUMIN:
            	sticker = new StickerData("sakura/images/megumin.png", 95, 160);
            	break;
            case UTENA:
            	sticker = new StickerData("sakura/images/utena.png", 95, 160);
            	break;
            case UZAKI:
            	sticker = new StickerData("sakura/images/uzaki.png", 95, 160);
            	break;
            case HALFLIN:
            	sticker = new StickerData("sakura/images/manolo.png", 95, 160);
            	break;
            case KOMI:
            	sticker = new StickerData("sakura/images/komi.png", 95, 160);
            	break;
            case HIDERI:
            	sticker = new StickerData("sakura/images/hideri.png", 95, 160);
            	break;
            case FUJIWARA:
            	sticker = new StickerData("sakura/images/fujiwara.png", 95, 160);
            	break;
            case GWEN:
            	sticker = new StickerData("sakura/images/bunny.png", 95, 160);
            	break;
            case AKARI:
            	sticker = new StickerData("sakura/images/akari.png", 95, 160);
            	break;
            case TYPH:
            	sticker = new StickerData("sakura/images/typh.png", 95, 160);
            	break;
            case MILIM:
            	sticker = new StickerData("sakura/images/milim.png", 95, 160);
            	break;
            case AMONGUS:
            	sticker = new StickerData("sakura/images/amongus.png", 85, 90);
            	break;
            default:
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
    
	@AllArgsConstructor
	@Getter
    enum WaifuList {
    	SHIROKO("Shiroko"),
    	JUPO("Jupo"),
    	MIKA("Mika"),
    	HOSHINO("Ai Hoshino"),
    	ASTOLFO("Astolfo"),
    	ASTOLFO2("Astolfo 2"),
    	ELF("Elf"),
    	MILIM("Milim"),
    	AMONGUS("Among Us"),
    	KIWI("Kiwi"),
    	KUMI("Kumi"),
    	KURUMI("Kurumi"),
    	MAGIC("Magic"),
    	MAI("Mai"),
    	MEGUMIN("Megumin Cat"),
    	UTENA("Utena"),
    	UZAKI("Uzaki Chan"),
    	HALFLIN("Halflin"),
    	KOMI("Komi San"),
    	HIDERI("Hideri"),
    	GWEN("Gwen Bunny"),
    	FUJIWARA("Fujiwara"),
    	AKARI("Akari"),
    	TYPH("Typh");
    	
        final String name;
        
        @Override
        public String toString() {
        	return name;
        }
    }
}