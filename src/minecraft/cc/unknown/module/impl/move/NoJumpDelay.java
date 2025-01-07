package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(aliases = "No Jump Delay", description = "Remueve el delay al saltar", category = Category.MOVEMENT)
public class NoJumpDelay extends Module {

	@EventLink
	public final Listener<PreMotionEvent> onPre = event -> {
		mc.player.jumpTicks = 0;
	};
}