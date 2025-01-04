package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;

@ModuleInfo(aliases = "No Jump Delay", description = "Remueve el delay al saltar", category = Category.MOVEMENT)
public class NoJumpDelay extends Module {

	@EventLink
	public final Listener<PreMotionEvent> onPre = event -> {
		mc.player.jumpTicks = 0;
	};
}