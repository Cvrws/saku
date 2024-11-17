package cc.unknown.script.api.wrapper.impl.event.impl.netty;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class ScriptPacketSendEvent extends CancellableScriptEvent<PacketSendEvent> {

	public ScriptPacketSendEvent(final PacketSendEvent wrappedEvent) {
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
        return "onSend";
    }
}
