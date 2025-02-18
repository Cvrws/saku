package cc.unknown.module.impl.combat.velocity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.util.Vec3;

public class LegitVelocity extends Mode<Velocity> {
	public LegitVelocity(String name, Velocity parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {

	};
}
