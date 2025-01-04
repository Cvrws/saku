package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Extra Sensory Perception", description = "Renderiza a los jugadores", category = Category.VISUALS)
public final class ExtraSensoryPerception extends Module {
	
	private final BooleanValue colorTeams = new BooleanValue("Color based in team color", this, true);
	private final BooleanValue checkInvis = new BooleanValue("Check Invisibles", this, false);
	private final BooleanValue redDamage = new BooleanValue("Red on Damage", this, false);
	private final BooleanValue renderSelf = new BooleanValue("Render Self", this, false);
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		for (EntityPlayer player : mc.theWorld.playerEntities) {
			String name = player.getName();
    			
			if (name.contains("CLICK DERECHO") || name.contains("MEJORAS") || name.contains("CLIQUE PARA COMPRAR") || name.contains("[SHOP]") || name.contains("CLIQUE PARA ABRIR")) {
				return;
            }
			
			if (player == mc.player && !renderSelf.getValue()) {
				continue;
			}
			
			if (player.deathTime == 0 && (checkInvis.getValue() || !player.isInvisible())) {
				int color = 0;
            	
				if (colorTeams.getValue()) {
					color = ColorUtil.getTeamColor(player);
				} else {
					color = getTheme().getFirstColor().getRGB();
				}
				
				if (redDamage.getValue() && player.hurtTime > 0) {
					color = new Color(255, 0, 0).getRGB();
				}
            	
				RenderUtil.drawSimpleBox(player, color, event.getPartialTicks());
            }
    	}
    };
}
