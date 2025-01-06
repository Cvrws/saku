package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.RotationEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "No Place Delay", description = "Remueve el delay al colocar bloques.", category = Category.PLAYER)
public class NoPlaceDelay extends Module {

	@EventLink
	public final Listener<RotationEvent> onRotation = event -> {
		if (mc.gameSettings.keyBindUseItem.pressed) {
			if (mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() != null && mc.player.getHeldItem().getItem() instanceof ItemBlock && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
				mc.rightClickMouse();
			}
		}	
	};
}