package cc.unknown.module.impl.visual;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ListValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Stickers", description = "Stickers/Pegatinas", category = Category.VISUALS)
public final class Stickers extends Module {

    private final ListValue<WaifuList> stickerType = new ListValue<>("Sticker Type", this);
    private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
    private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);

    public Stickers() {
        for (WaifuList waifu : WaifuList.values()) {
            stickerType.add(waifu);
        }

        stickerType.setDefault(WaifuList.JUPO);
    }

    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        Vector2d pos = position.position;
        WaifuList selectedSticker = stickerType.getValue();

        if (selectedSticker != null) {
            String imagePath = selectedSticker.getImagePath();
            int width = selectedSticker.getWidth();
            int height = selectedSticker.getHeight();

            RenderUtil.image(new ResourceLocation(imagePath), (int) pos.x, (int) pos.y, width, height);
        }
    };

    @AllArgsConstructor
    @Getter
    private static class StickerData {
        private final String imagePath;
        private final int width;
        private final int height;
    }

    @AllArgsConstructor
    @Getter
    private enum WaifuList {
        SHIROKO("Shiroko", "sakura/images/shiroko.png", 85, 160),
        JUPO("Jupo", "sakura/images/jupo.png", 85, 130),
        MIKA("Mika", "sakura/images/mika.png", 95, 160),
        HOSHINO("Ai Hoshino", "sakura/images/ai.png", 95, 160),
        ASTOLFO("Astolfo", "sakura/images/astolfo.png", 95, 160),
        ASTOLFO2("Astolfo 2", "sakura/images/astolfo2.png", 130, 160),
        ELF("Elf", "sakura/images/elf.png", 95, 160),
        KIWI("Kiwi", "sakura/images/kiwi.png", 95, 160),
        KUMI("Kumi", "sakura/images/kumi.png", 95, 160),
        KURUMI("Kurumi", "sakura/images/kurumi.png", 100, 180),
        MAGIC("Magic", "sakura/images/magic.png", 95, 160),
        MAI("Mai", "sakura/images/mai.png", 95, 160),
        MEGUMIN("Megumin Cat", "sakura/images/megumin.png", 95, 160),
        UTENA("Utena", "sakura/images/utena.png", 95, 160),
        UZAKI("Uzaki Chan", "sakura/images/uzaki.png", 95, 160),
        HALFLIN("Halflin", "sakura/images/manolo.png", 95, 160),
        KOMI("Komi San", "sakura/images/komi.png", 95, 160),
        HIDERI("Hideri", "sakura/images/hideri.png", 95, 160),
        FUJIWARA("Fujiwara", "sakura/images/fujiwara.png", 95, 160),
        GWEN("Gwen Bunny", "sakura/images/bunny.png", 95, 160),
        AKARI("Akari", "sakura/images/akari.png", 95, 160),
        TYPH("Typh", "sakura/images/typh.png", 95, 160),
        MILIM("Milim", "sakura/images/milim.png", 95, 160),
        AMONGUS("Among Us", "sakura/images/amongus.png", 85, 90);
    	
        private final String displayName;
        private final String imagePath;
        private final int width;
        private final int height;

        @Override
        public String toString() {
            return displayName;
        }
    }
}
