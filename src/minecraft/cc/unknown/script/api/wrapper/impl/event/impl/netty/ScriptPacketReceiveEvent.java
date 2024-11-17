package cc.unknown.script.api.wrapper.impl.event.impl.netty;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class ScriptPacketReceiveEvent extends CancellableScriptEvent<PacketReceiveEvent> {

    public ScriptPacketReceiveEvent(final PacketReceiveEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public Packet getPacket() {
		return wrapped.getPacket();
	}

	public void setPacket(Packet packet) {
		wrapped.setPacket(packet);
	}
	
	public NetworkManager getNetworkManager() {
		return wrapped.getNetworkManager();
	}
	
	public void setNetworkManager(NetworkManager net) {
		wrapped.setNetworkManager(net);
	}

    @Override
    public String getHandlerName() {
        return "onReceive";
    }
}
