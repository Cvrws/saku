package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(aliases = "No Block Hit Delay", description = "Remueve el delay de romper bloques", category = Category.PLAYER)
public class NoBlockHitDelay extends Module {
		
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		 mc.playerController.blockHitDelay = 0;
	};
}