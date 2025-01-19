package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;

@ModuleInfo(aliases = "Sprint", description = "Corre automáticamente", category = Category.MOVEMENT)
public class Sprint extends Module {

	public final BooleanValue legit = new BooleanValue("Legit", this, true);

    @EventLink(value = Priority.LOW)
    public final Listener<PreStrafeEvent> onStrafe = event -> {
    	if (legit.getValue()) mc.gameSettings.keyBindSprint.setPressed(true);
    	
    	if (!legit.getValue()) {
            mc.player.omniSprint = MoveUtil.isMoving();

            MoveUtil.preventDiagonalSpeed();

            mc.player.setSprinting(MoveUtil.isMoving() && !mc.player.isCollidedHorizontally &&
                    !mc.player.isSneaking() && !mc.player.isUsingItem());
    	}
    };

    @Override
    public void onDisable() {
        mc.player.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        mc.player.omniSprint = false;
    }
}