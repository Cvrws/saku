package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.stickers.StickerType;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.gif.GifRenderer;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Stickers", description = "Stickers/Pegatinas", category = Category.VISUALS)
public final class Stickers extends Module {

	private final ListValue<StickerType> stickerType = new ListValue<>("Sticker Type", this);
	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	
	public Stickers() {
		for (StickerType sticker : StickerType.values()) {
			stickerType.add(sticker);
		}
		stickerType.setDefault(StickerType.AMONGUS);
	}

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		Vector2d pos = position.position;
		StickerType sticker = stickerType.getValue();
		int x = (int) pos.x, y = (int) pos.y;
		if (sticker != null) {
			RenderUtil.image(new ResourceLocation(sticker.getImagePath()), x, y, sticker.getWidth(), sticker.getHeight());
		}
	};
}
