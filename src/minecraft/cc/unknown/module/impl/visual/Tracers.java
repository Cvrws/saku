package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Tracers", description = "Renders a line from your crosshair to every player", category = Category.VISUALS)
public final class Tracers extends Module {

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        for (final Entity player : mc.world.playerEntities) {
            if (player == mc.player || player.isDead || Sakura.instance.getBotManager().contains(player)) {
                continue;
            }
            
            final Color color = ColorUtil.withAlpha(
                    ColorUtil.mixColors(getTheme().getSecondColor(), getTheme().getFirstColor(), Math.min(1, mc.player.getDistanceToEntity(player) / 50)),
                    128);

    		RenderUtil.drawSimpleLine((EntityPlayer) player, event.getPartialTicks(), color);
        }

    };
}