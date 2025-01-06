package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;

@ModuleInfo(aliases = "Respawn", description = "Auto Respawn", category = Category.PLAYER)
public class Respawn extends Module {

	 private final BooleanValue auto = new BooleanValue("Auto Respawn", this, false);
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (mc.player != null) {
            if (canAuto()) {
            	mc.gameSettings.keyBindForward.pressed = true;
                mc.player.motionY = 0.45;
                mc.player.jump();
                mc.player.onGround = true;
                mc.player.jump();
            }
		}
	};
	
    public boolean canAuto() {
        if (auto.getValue()) {
            return mc.player.getHealth() < 4;
        }
        return true;
    }
}