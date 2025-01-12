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

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.stopWatch = new StopWatch();
    }

    public TimedPacket(final Packet<?> packet, long stopWatchTime) {
        this.packet = packet;
        this.stopWatch = new StopWatch();
        this.stopWatch.finished(stopWatchTime);
    }
}