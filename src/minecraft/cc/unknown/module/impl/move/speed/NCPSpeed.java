package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class NCPSpeed extends Mode<Speed> {
	public NCPSpeed(String name, Speed parent) {
		super(name, parent);
	}
	
    private boolean reset;
    private double speed;
    
    @Override
    public void onDisable() {
        speed = 0;
    }
	
    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (!getParent().boost.getValue()) return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity wrapper = ((S12PacketEntityVelocity) packet);
            if (wrapper.getEntityID() == mc.player.getEntityId()) {
                speed = Math.hypot(wrapper.motionX / 8000.0D, wrapper.motionZ / 8000.0D);
            }
        }
    };
    
    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (getParent().lowHop.getValue()){
            if (mc.player.offGroundTicks == 4){
                mc.player.motionY = -0.09800000190734864;
            }
        }

        if (getParent().yPort.getValue() && mc.player.offGroundTicks == 5 && Math.abs(mc.player.motionY - 0.09800000190734864) < 0.12){
            mc.player.motionY = -0.09800000190734864;
        }

        if (getParent().hurtBoost.getValue() && mc.player.hurtTime <= getParent().hurTime.getValue().intValue()) {
            speed = getParent().boostSpeed.getValue().doubleValue();
        }

        final double base = MoveUtil.getAllowedHorizontalDistance();

        if (MoveUtil.isMoving()) {
            switch (mc.player.offGroundTicks) {
                case 0:
                    float jumpMotion = getParent().jumpMotion.getValue().floatValue();

                    float motion = mc.player.isCollidedHorizontally ? 0.42F : jumpMotion == 0.4f ? jumpMotion : 0.42f;
                    mc.player.motionY = MoveUtil.jumpBoostMotion(motion);
                    speed = base * getParent().groundSpeed.getValue().doubleValue();
                    break;

                case 1:
                    speed -= (getParent().bunnySlope.getValue().doubleValue() * (speed - base));
                    break;

                default:
                    speed -= speed / MoveUtil.BUNNY_FRICTION;
                    break;
            }

            mc.timer.timerSpeed = getParent().timer.getValue().floatValue();
            reset = false;
        } else if (!reset) {
            speed = MoveUtil.getAllowedHorizontalDistance();
            mc.timer.timerSpeed = 1;
            reset = true;
        }

        if (mc.player.isCollidedHorizontally) {
            speed = MoveUtil.getAllowedHorizontalDistance();
        }

        event.setSpeed(Math.max(speed, base), Math.random() / 2000);
    };

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        speed = 0;
    };
}