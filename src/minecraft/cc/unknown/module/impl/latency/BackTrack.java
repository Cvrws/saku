package cc.unknown.module.impl.latency;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.handlers.NetworkingHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Back Track", description = "Incrementa la distancia al golpear utilizando lag", category = Category.LATENCY)
public final class BackTrack extends Module {
	public final BooleanValue cancelClientP = new BooleanValue("Cancel Client Packet", this, false);
	public final BooleanValue swingCheck = new BooleanValue("Swing Check", this, false);
	private final ModeValue mode = new ModeValue("Active Mode", this).add(new SubMode("Hit"))
			.add(new SubMode("Not Hit")).add(new SubMode("Always")).setDefault("Hit");

	public final BooleanValue releaseOnVelocity = new BooleanValue("Release On Velocity", this, false);
	private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 50, 200, 0, 6, 0.1);
	public EntityPlayer target;
	public Vec3 realPosition = new Vec3(0, 0, 0);

	private int ping;
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
		if (mc.player.isDead)
			return;

		target = (EntityPlayer) TargetUtil.getTarget(9);

		if (target == null)
			return;

		if (swingCheck.getValue() && !mc.player.isSwingInProgress)
			return;

		double realDistance = realPosition.distanceTo(mc.player);
		double clientDistance = target.getDistanceToEntity(mc.player);

		boolean on = realDistance > clientDistance && realDistance > 2.3 && realDistance < 5.9 && shouldActive(target)
				&& (releaseOnVelocity.getValue() && mc.player.hurtTime == 0 || !releaseOnVelocity.getValue());

		if (on) {
			if (shouldActive(target)) {
				ping = MathHelper.randomInt(delay.getValue().intValue(), delay.getSecondValue().intValue());
				NetworkingHandler.spoof(ping, true, true, true, true, cancelClientP.getValue(),
						cancelClientP.getValue());
			} else {
				NetworkingHandler.disable();
				NetworkingHandler.dispatch();
			}
		} else {
			NetworkingHandler.disable();
			NetworkingHandler.dispatch();
		}
	};

	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {

		final Packet<?> packet = event.getPacket();

		if (target == null) {
			return;
		}

		double realDistance = realPosition.distanceTo(mc.player);
		double clientDistance = target.getDistanceToEntity(mc.player);

		boolean on = realDistance > clientDistance && realDistance > 2.3 && realDistance < 5.9;

		if (on) {
			if (packet instanceof S14PacketEntity) {
				S14PacketEntity wrapper = (S14PacketEntity) packet;
				if (wrapper.getEntityId() == target.getEntityId()) {
					realPosition = realPosition.addVector(wrapper.getPosX() / 32.0D,
							wrapper.getPosY() / 32.0D, wrapper.getPosZ() / 32.0D);
				}
			} else if (packet instanceof S18PacketEntityTeleport) {
				S18PacketEntityTeleport wrapper = (S18PacketEntityTeleport) packet;
				if (wrapper.getEntityId() == target.getEntityId()) {
					realPosition = new Vec3(wrapper.getX() / 32D, wrapper.getY() / 32D,
							wrapper.getZ() / 32D);
				}
			}
		} else {
			realPosition = target.getPositionVector();
		}
	};

	public boolean shouldActive(EntityPlayer target) {
		return mode.is("Always") || mode.is("Hit") && target.hurtTime != 0
				|| mode.is("Not Hit") && target.hurtTime == 0;
	}

}