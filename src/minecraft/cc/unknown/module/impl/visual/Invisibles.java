package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Invisibles", description = "Revela bloques o jugadores invisibles", category = Category.VISUALS)
public final class Invisibles extends Module {
    
	private final BooleanValue players = new BooleanValue("Show players", this, false);
    public final BooleanValue barriers = new BooleanValue("Show barriers", this, false);
        
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
    	if (players.getValue()) {
            mc.theWorld.playerEntities.stream().forEach(player -> {
            	player.removePotionEffect(Potion.invisibility.getId());
                player.setInvisible(false);   
            });
    	}
    };
}
