package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(aliases = "No Click Delay", description = "Elimina el delay de la 1.8 al clickear", category = Category.PLAYER)
public class NoClickDelay extends Module {

	@EventLink
	public final Listener<PreMotionEvent> onPre = event -> {
		mc.leftClickCounter = 0;
	};
}