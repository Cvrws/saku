package cc.unknown.module.impl.world.scaffold.sprint;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;
import net.minecraft.util.MathHelper;

public class NormalSprint extends Mode<Scaffold> {

	public NormalSprint(String name, Scaffold parent) {
		super(name, parent);
	}

    @EventLink(value = Priority.LOW)
    public final Listener<PreStrafeEvent> onStrafe = event -> {
    	mc.gameSettings.keyBindSprint.setPressed(true);
    };
}
