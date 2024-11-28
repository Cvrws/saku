package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Extra Sensory Perception", description = "Renderiza a los jugadores", category = Category.VISUALS)
public final class ExtraSensoryPerception extends Module {

	private final BooleanValue checkInvis = new BooleanValue("Check Invisibles", this, false);
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player && player.deathTime == 0 && (checkInvis.getValue() || !player.isInvisible())) {
            	RenderUtil.drawSimpleBox(player, ColorUtil.getTeamColor(player), event.getPartialTicks());
            }
    	}
    };
}
