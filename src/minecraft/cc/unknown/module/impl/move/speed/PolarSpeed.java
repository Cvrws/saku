package cc.unknown.module.impl.move.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.move.Speed;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class PolarSpeed extends Mode<Speed> {

	public PolarSpeed(String name, Speed parent) {
		super(name, parent);
	}
	
    public StopWatch stopWatch = new StopWatch();
    public int ticks = 0;
    private boolean start = false;
    
    @Override
    public void onEnable() {
        ticks = 0;
        start = false;
    }

    @Override
    public void onDisable() {
        ticks = 0;
        start = false;
    }

	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
        Packet packet = event.getPacket();

        if (packet != null && packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
            if (wrapper.getEntityID() == mc.player.getEntityId()) {
                start = true;
            }
        }
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (start) {

            if (mc.player.motionY <= -0.10) {
                ticks++;
                if (ticks % 2 == 0) {
                    mc.player.motionY = -0.1;
                    mc.player.jumpMovementFactor = 0.0265f;
                } else {
                    mc.player.motionY = -0.16;
                    mc.player.jumpMovementFactor = 0.0265f;
                }
            } else {
                ticks = 0;
            }
        }
	};

}