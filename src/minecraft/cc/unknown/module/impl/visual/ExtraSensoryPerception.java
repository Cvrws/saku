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
import net.minecraft.scoreboard.ScorePlayerTeam;

@ModuleInfo(aliases = "Extra Sensory Perception", description = "Renderiza a los jugadores", category = Category.VISUALS)
public final class ExtraSensoryPerception extends Module {

	private final BooleanValue colorTeams = new BooleanValue("Color based in team color", this, true);
	private final BooleanValue checkInvis = new BooleanValue("Check Invisibles", this, false);
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	for (EntityPlayer player : mc.world.playerEntities) {
    		String name = player.getName();
    		
            if (name.contains("CLICK DERECHO") || name.contains("MEJORAS") || name.contains("CLIQUE PARA COMPRAR") || name.contains("[SHOP]") || name.contains("CLIQUE PARA ABRIR")) {
            	return;
            }
            
            if (player != mc.player && player.deathTime == 0 && (checkInvis.getValue() || !player.isInvisible())) {
            	int color = 0;
            	
            	if (colorTeams.getValue()) {
            		color = ColorUtil.getTeamColor(player);
            	} else {
            		color = getTheme().getFirstColor().getRGB();
            	}
            	 
            	RenderUtil.drawSimpleBox(player, color, event.getPartialTicks());
            }
    	}
    };
}
