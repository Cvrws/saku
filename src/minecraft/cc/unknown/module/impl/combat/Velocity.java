package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleInfo(aliases = "Velocity", description = "Modifica tu kb (Only Hypixel).", category = Category.COMBAT)
public final class Velocity extends Module {

	private final NumberValue vertical = new NumberValue("Vertical", this, 90, 0, 100, 1);
	private final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1);

	private final BooleanValue delay = new BooleanValue("Delay", this, false);
	private final NumberValue delayHorizontal = new NumberValue("Delayed Horizontal", this, 100, 0, 100, 1, () -> !delay.getValue());
	private final NumberValue delayVertical = new NumberValue("Delayed Vertical", this, 90, 0, 100, 1, () -> !delay.getValue());
	
	private final BooleanValue onlyAir = new BooleanValue("Only in Air", this, false);
	private final BooleanValue onExplode = new BooleanValue("Explosion Ignore", this, false);

	private int ticks;
	private double motionY, motionX, motionZ;

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
		if (event.isCancelled()) {
			return;
		}
		
		if (mc.player.onGround && onlyAir.getValue()) return;

		final Packet<?> packet = event.getPacket();

		final double horizontal = this.horizontal.getValue().doubleValue();
		final double vertical = this.vertical.getValue().doubleValue();
		final boolean onExplode = this.onExplode.getValue();

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
		if (delay.getValue()) {
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
	
			final double horizontal = this.delayHorizontal.getValue().doubleValue();
			final double vertical = this.delayVertical.getValue().doubleValue();
	
			if (mc.player.hurtTime == 8) {
				mc.player.motionX *= horizontal / 100;
				mc.player.motionY *= vertical / 100;
				mc.player.motionZ *= horizontal / 100;
			}
		}
	};
}