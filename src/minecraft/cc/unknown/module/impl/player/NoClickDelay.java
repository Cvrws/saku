package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "No Click Delay", description = "Se remueve el delay al hacer click solo si el mouse está sobre el hitbox de un objetivo", category = Category.PLAYER)
public class NoClickDelay extends Module {
		
	@EventLink
	public final Listener<GameEvent> onGame = event -> {
		if (mc.objectMouseOver.typeOfHit != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			mc.leftClickCounter = 0;
		} else mc.leftClickCounter = 0;
		
	};
}