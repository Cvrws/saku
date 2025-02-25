package cc.unknown.module.impl.combat.velocity;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class HypixelVelocity extends Mode<Velocity> {
	public HypixelVelocity(String name, Velocity parent) {
		super(name, parent);
	}

	private int ticks;
	private double motionY, motionX, motionZ;

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
		if (event.isCancelled()) return;
		if (!MathUtil.isChance(getParent().chance, getParent().notWhileSpeed, getParent().notWhileJumpBoost)) return;

		final Packet<?> packet = event.getPacket();

		if (mc.player.onGround && getParent().onlyAir.getValue()) return;

		final double horizontal = getParent().horizontal.getValueToDouble();
		final double vertical = getParent().vertical.getValueToDouble();
		final boolean onExplode = getParent().onExplode.getValue();

		if (packet instanceof S12PacketEntityVelocity) {

			final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

			if (wrapper.getEntityID() == mc.player.getEntityId()) {

				if (horizontal == 0) {
					if (vertical != 0 && !event.isCancelled()) {

						mc.player.motionY = wrapper.getMotionY() / 8000.0D;
					}

					event.setCancelled();
					return;
				}

				wrapper.motionX *= horizontal / 100;
				wrapper.motionY *= vertical / 100;
				wrapper.motionZ *= horizontal / 100;

				event.setPacket(wrapper);

			}
		} else if (packet instanceof S27PacketExplosion) {
			final S27PacketExplosion wrapper = (S27PacketExplosion) packet;

			if (onExplode) {
				event.setCancelled();
				return;
			}

			wrapper.posX *= horizontal / 100;
			wrapper.posY *= vertical / 100;
			wrapper.posZ *= horizontal / 100;

			event.setPacket(wrapper);
		}

	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (!MathUtil.isChance(getParent().chance, getParent().notWhileSpeed, getParent().notWhileJumpBoost)) return;

		if (getParent().delay.getValue()) {
			ticks++;

			if (mc.player.hurtTime == 9) {
				ticks = 0;
			}

			assert mc.player != null;

			if (mc.player.hurtTime == 9) {
				motionX = mc.player.motionX;
				motionY = mc.player.motionY;
				motionZ = mc.player.motionZ;
			}

			final double horizontal = getParent().delayHorizontal.getValueToDouble();
			final double vertical = getParent().delayVertical.getValueToDouble();

			if (mc.player.hurtTime == 8) {
				mc.player.motionX *= horizontal / 100;
				mc.player.motionY *= vertical / 100;
				mc.player.motionZ *= horizontal / 100;
			}
		}
	};
}
