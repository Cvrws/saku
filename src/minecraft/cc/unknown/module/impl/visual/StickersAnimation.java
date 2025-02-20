package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.gifs.GifType;
import cc.unknown.util.render.gif.GifRenderer;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.value.impl.DragValue;
import cc.unknown.value.impl.ListValue;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = {"Stickers Animation", "gif"}, description = "Stickers pero con movimiento", category = Category.VISUALS)
public final class StickersAnimation extends Module {

	private final ListValue<GifType> gifType = new ListValue<>("Gif Type", this);
	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	private GifRenderer gifRenderer;
	
	public StickersAnimation() {
		for (GifType gif : GifType.values()) {
			gifType.add(gif);
		}
		gifType.setDefault(GifType.TACO);
	}

    @Override
    public void guiUpdate() {
    	gifRenderer = new GifRenderer(new ResourceLocation(gifType.getValue().getImagePath()));
    }

    @EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		Vector2d pos = position.position;		
		int x = (int) pos.x, y = (int) pos.y;
		
		gifRenderer.drawTexture(x, y, gifRenderer.getWidth() - 40, gifRenderer.getHeight() - 40);
		gifRenderer.update();
	};
}
