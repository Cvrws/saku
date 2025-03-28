package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.structure.CPSHelper;
import cc.unknown.util.structure.CPSHelper.MouseButton;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.DragValue;

@ModuleInfo(aliases = "CPS Display", description = "Muestra tus clicks por segundo", category = Category.VISUALS)
public final class CPSDisplay extends Module {

	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));
	private final Vector2f scale = new Vector2f(22, 22);
	
	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
	    Vector2d position = this.position.position;

	    final String leftCPS = CPSHelper.getCPS(MouseButton.LEFT) + "";
	    final String rightCPS = CPSHelper.getCPS(MouseButton.RIGHT) + "";
	    final String title = " CPS";
	    final String separator = " | ";

	    final float leftCPSWidth = Fonts.MAISON.get(20, Weight.NONE).width(leftCPS);
	    final float separatorWidth = Fonts.MAISON.get(20, Weight.NONE).width(separator);
	    final float rightCPSWidth = Fonts.MAISON.get(20, Weight.NONE).width(rightCPS);
	    final float titleWidth = Fonts.MAISON.get(20, Weight.NONE).width(title);

	    scale.x = leftCPSWidth + separatorWidth + rightCPSWidth + titleWidth;

	    this.position.setScale(new Vector2d(scale.x + 6, scale.y - 1));

	    final double textX = position.x + 3.0F;
	    final double textY = position.y + scale.y / 2.0F - Fonts.MAISON.get(20, Weight.NONE).height() / 4.0F;

	    RenderUtil.roundedRect(textX + scale.x + 3, textY + 11, textX - 2, textY - 4, 0, getTheme().getBackgroundShade().getRGB());

	    Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(leftCPS, textX, textY, Color.WHITE.getRGB());
	    Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(separator, textX + leftCPSWidth, textY, Color.WHITE.getRGB());
	    Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(rightCPS, textX + leftCPSWidth + separatorWidth, textY, Color.WHITE.getRGB());
	    Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(title, textX + leftCPSWidth + separatorWidth + rightCPSWidth, textY, Color.WHITE.getRGB());
	};
}