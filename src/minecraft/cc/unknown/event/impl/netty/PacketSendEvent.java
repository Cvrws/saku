package cc.unknown.event.impl.netty;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter
@Setter
public class PacketSendEvent extends CancellableEvent {
	private Packet<?> packet;
    private NetworkManager networkManager;
}
