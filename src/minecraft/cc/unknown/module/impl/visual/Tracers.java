package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Tracers", description = "Dibuja una l�nea hasta el jugador.", category = Category.VISUALS)
public final class Tracers extends Module {

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        for (final Entity player : mc.world.playerEntities) {
            if (player == mc.player || player.isDead) {
                continue;
            }
            
            final Color color = ColorUtil.withAlpha(
                    ColorUtil.mixColors(getTheme().getSecondColor(), getTheme().getFirstColor(), Math.min(1, mc.player.getDistanceToEntity(player) / 50)),
                    128);

    		RenderUtil.drawSimpleLine((EntityPlayer) player, event.getPartialTicks(), color);
        }

    };
}