package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.CPSHelper;
import cc.unknown.util.structure.CPSHelper.MouseButton;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.DragValue;

@ModuleInfo(aliases = "CPS Display", description = "Muestra tus clicks por segundo", category = Category.VISUALS)
public final class CPSDisplay extends Module {

	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		Vector2d position = this.position.position;

		final String titleString = "CPS ";
		final String cpsString = CPSHelper.getCPS(MouseButton.LEFT) + "";
        final float cpsWidth = Fonts.ROBOTO.get(20, Weight.LIGHT).width(cpsString);
		final float titleWidth = Fonts.ROBOTO.get(20, Weight.LIGHT).width(titleString);
		scale.x = titleWidth + Fonts.ROBOTO.get(20, Weight.LIGHT).width(cpsString);

		this.position.setScale(new Vector2d(scale.x + 6, scale.y - 1));

		final double textX = position.x + 3.0F;
		final double textY = position.y + scale.y / 2.0F - Fonts.ROBOTO.get(20, Weight.LIGHT).height() / 4.0F;
		
        RenderUtil.roundedRect(textX + cpsWidth + 24, textY + 11, textX - 2, textY - 4, 0, getTheme().getBackgroundShade().getRGB());
		
		Fonts.ROBOTO.get(20, Weight.LIGHT).drawWithShadow(titleString, textX, textY, getTheme().getFirstColor().getRGB());
		Fonts.ROBOTO.get(20, Weight.LIGHT).drawWithShadow(cpsString, textX + titleWidth, textY, Color.WHITE.getRGB());
	};
}