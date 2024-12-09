package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PostUpdateEvent;
import cc.unknown.event.impl.player.PreLivingUpdateEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(aliases = "Velocity", description = "Uses heavy dick and balls to drag across the floor to reduce velocity.", category = Category.COMBAT)
public final class Velocity extends Module {

	public final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Simple"))
			.add(new SubMode("Legit"))
			.add(new SubMode("Polar"))
			.add(new SubMode("Polar Drunk"))
			.setDefault("Simple");

	public final NumberValue horizontal = new NumberValue("Horizontal", this, 0, 0, 100, 1, () -> !mode.is("Simple"));
	public final NumberValue vertical = new NumberValue("Vertical", this, 0, 0, 100, 1, () -> !mode.is("Simple"));

	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	private final BooleanValue onSwing = new BooleanValue("On Swing", this, false);
	public final BooleanValue legitTiming = new BooleanValue("Legit Timing", this, true, () -> !mode.is("Legit"));

	private boolean reduced;

	@Override
	public void onEnable() {
		reduced = false;
	}
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
	    if (shouldSkipUpdate()) return;

	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = MathUtil.getRandomFactor(chanceValue);

	    if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;

		switch (mode.getValue().getName()) {
		case "Polar Drunk":
			if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.player.hurtTime == 9 && !mc.player.isBurning()) {
				reduced = true;
			} else
				reduced = false;
			break;
		case "Polar":
			if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.player.hurtTime == 9 && !mc.player.isBurning()) {
				reduced = true;
			} else
				reduced = false;
			break;
		}
	};
	@EventLink
	public final Listener<PreLivingUpdateEvent> onPreLiving = event -> {
		if (shouldSkipUpdate()) return;
		
		double chanceValue = chance.getValue().doubleValue();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);
		
		if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
		
		switch (mode.getValue().getName()) {
		case "Polar Drunk":
			if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.player.hurtTime == 9 && !mc.player.isBurning()) {
				reduced = true;
			} else
				reduced = false;
			break;
		}
	};
	
	@EventLink
	public final Listener<PostUpdateEvent> onPostUpdate = event -> {
		if (shouldSkipUpdate()) return;
		
		double chanceValue = chance.getValue().doubleValue();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);
		
		if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
		
		switch (mode.getValue().getName()) {
		case "Polar Drunk":
			if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.player.hurtTime == 9 && !mc.player.isBurning()) {
				reduced = true;
			} else
				reduced = false;
			break;
		}
	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		if (mode.is("Legit") && reduced && MoveUtil.isMoving()) {
			event.setJump(true);
		}
		
		if (reduced && mode.is("Polar")) {
			event.setJump(true);
		}
		
		if (reduced && mode.is("Polar Drunk")) {
			event.setJump(true);
		}
	};
	
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		Packet p = event.getPacket();
		
		if (p instanceof S12PacketEntityVelocity) {
			final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;

			if (wrapper.getEntityID() == mc.player.getEntityId()) {
				switch (mode.getValue().getName()) {
				case "Simple":
					if (wrapper.getMotionY() / 8000.0D > 0.6) {
						return;
					}

					if (horizontal.getValue().doubleValue() == 0) {
						if (vertical.getValue().doubleValue() != 0 && !event.isCancelled()) {
							mc.player.motionY = wrapper.getMotionY() / 8000.0D;
						}

						event.setCancelled();
						return;
					}

					wrapper.motionX *= horizontal.getValue().doubleValue() / 100;
					wrapper.motionY *= vertical.getValue().doubleValue() / 100;
					wrapper.motionZ *= horizontal.getValue().doubleValue() / 100;

					event.setPacket(wrapper);
					break;
				case "Polar":
					if (reduced) {
						mc.player.jump();
					}
					break;
				case "Polar Drunk":
					if (reduced) {
						mc.player.jump();
					}
					break;
				case "Legit":
					if (mc.player.onGround && mc.player.motionY > 0) {
						if (!legitTiming.getValue() || mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1) {
							reduced = true;
						}
					} else
						reduced = false;

					break;
				}
			}
		}
	};
	
	private boolean shouldSkipUpdate() {
	    return onSwing.getValue() && !mc.player.isSwingInProgress;
	}
}