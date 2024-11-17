package cc.unknown.event.impl.netty;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.netty.ScriptPacketReceiveEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter
@Setter
public class PacketReceiveEvent extends CancellableEvent {
    private Packet<?> packet;
    private NetworkManager networkManager;
    
	
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptPacketReceiveEvent(this);
    }
}
