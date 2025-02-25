package cc.unknown.module.impl.combat.velocity;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.Mode;

public class LegitVelocity extends Mode<Velocity> {
	public LegitVelocity(String name, Velocity parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (mc.player.maxHurtResistantTime != mc.player.hurtResistantTime || mc.player.maxHurtResistantTime == 0) return;
		if (!MathUtil.isChance(getParent().chance, getParent().notWhileSpeed, getParent().notWhileJumpBoost)) return;

		double horizontal = 0 / 100f;
		double vertical = 0 / 100f;

		mc.player.motionX *= horizontal;
		mc.player.motionZ *= horizontal;
		mc.player.motionY *= vertical;
	};
}
