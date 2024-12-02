package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.packet.custom.impl.PlayPongC2SPacket;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public final class TransactionFixComponent extends Component {

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        if (!event.isCancelled() && ViaLoadingBase.getInstance()
                .getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                C0FPacketConfirmTransaction transaction = ((C0FPacketConfirmTransaction) event.getPacket());

                PacketUtil.send(
                        new PlayPongC2SPacket(transaction.getUid()));
                event.setCancelled();
            }
        }
    };

}