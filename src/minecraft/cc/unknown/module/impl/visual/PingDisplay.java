package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.NetworkUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.DragValue;

@ModuleInfo(aliases = "Ping Display", description = "Muestra la latencia de tu conexión actual.", category = Category.VISUALS)
public final class PingDisplay extends Module {

    private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));

    private final Vector2f scale = new Vector2f(22, 22);
    private int lastPing;

    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
    	Vector2d position = this.position.position;
		
        int ping = NetworkUtil.getPing(mc.player);
        Color color;
        
        if (ping >= 0 && ping <= 99) {
            color = Color.GREEN;
        } else if (ping >= 100 && ping <= 199) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }
        
        final String titleString = ping + "";
        final String pingString = " ms";
        final float titleWidth = Fonts.MAISON.get(20, Weight.NONE).width(titleString);

        if (ping != lastPing) {
            scale.x = titleWidth + Fonts.MAISON.get(20, Weight.NONE).width(pingString);
        }

        lastPing = ping;

        this.position.setScale(new Vector2d(scale.x + 6, scale.y - 1));

        final double textX = position.x + 3.0F;
        final double textY = position.y + scale.y / 2.0F - Fonts.MAISON.get(20, Weight.NONE).height() / 4.0F;

        RenderUtil.roundedRect(textX + titleWidth + 24, textY + 10, textX - 5, textY - 4, 0, getTheme().getBackgroundShade().getRGB());
        
        Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(titleString, textX, textY, color.getRGB());
        Fonts.MAISON.get(20, Weight.NONE).drawWithShadow(pingString, textX + titleWidth, textY, Color.WHITE.getRGB());
    };
}
