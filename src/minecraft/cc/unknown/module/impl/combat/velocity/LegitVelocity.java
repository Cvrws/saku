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
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.util.Vec3;

public class LegitVelocity extends Mode<Velocity> {
	public LegitVelocity(String name, Velocity parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (getParent().noAction() || getParent().checks() || !getParent().isChance() || mc.player.hurtTime <= 0) {
			return;
		}

		Vec3 playerPos = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
		List<Vec3> vec3s = Arrays.asList(PlayerUtil.getPredictedPos(1.0F, 0.0F).add(playerPos), PlayerUtil.getPredictedPos(1.0F, 1.0F).add(playerPos), PlayerUtil.getPredictedPos(1.0F, -1.0F).add(playerPos));

		Map<Vec3, Integer> map = new HashMap<>();
		map.put(vec3s.get(0), 0);
		map.put(vec3s.get(1), 1);
		map.put(vec3s.get(2), -1);

		vec3s.sort(Comparator.comparingDouble(v -> v.distanceTo(playerPos)));

		if (!event.isSneak()) {
			event.setStrafe(map.get(vec3s.get(0)));
		}

	};
}
