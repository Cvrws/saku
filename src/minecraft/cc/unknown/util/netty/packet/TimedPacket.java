package cc.unknown.util.netty.packet;

import cc.unknown.util.client.StopWatch;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
public class TimedPacket {

    private final Packet<?> packet;
    private final StopWatch stopWatch;
    private final long millis;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.stopWatch = new StopWatch();
        this.millis = System.currentTimeMillis();
    }

    public TimedPacket(final Packet<?> packet, final long millis) {
        this.packet = packet;
        this.millis = millis;
        this.stopWatch = new StopWatch();
    }
}