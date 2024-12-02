package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;

public final class InteractEntityFixComponent extends Component {

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        if (!event.isCancelled() && ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {

            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity use = ((C02PacketUseEntity) event.getPacket());

                event.setCancelled(event.isCancelled() || !use.getAction().equals(C02PacketUseEntity.Action.ATTACK));
            }
        }
    };

}