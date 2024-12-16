package cc.unknown.module.impl.player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.TickEndEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

@ModuleInfo(aliases = "Blink", description = "Bloquea temporalmente los datos que se envían al servidor.", category = Category.PLAYER)
public class Blink extends Module {

	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private int ticks;

	@Override
	public void onEnable() {
		packets.clear();
		ticks = 0;
	}

	@Override
	public void onDisable() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
	}

	@EventLink
	public final Listener<TickEndEvent> onGame = event -> {
		if (mc.player == null) return;
		while (!packets.isEmpty()) {
			Packet packet = packets.get(0);

			if (packet instanceof S32PacketConfirmTransaction) {
				S32PacketConfirmTransaction transaction = (S32PacketConfirmTransaction) packet;
				PacketUtil.sendNoEvent(new C0FPacketConfirmTransaction(transaction.getWindowId(), transaction.getActionNumber(), false));
			} else if (packet instanceof S00PacketKeepAlive) {
				S00PacketKeepAlive keepAlive = (S00PacketKeepAlive) packet;
				PacketUtil.sendNoEvent(new C00PacketKeepAlive(keepAlive.func_149134_c()));
			} else if (packet instanceof C03PacketPlayer) {
				break;
			}

			PacketUtil.sendNoEvent(packets.get(0));
			packets.remove(packets.get(0));
		}
	};

	@EventLink
	public final Listener<PacketSendEvent> onSend = event -> {
		packets.add(event.getPacket());
		event.setCancelled();
	};

	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		if (event.getPacket() instanceof S18PacketEntityTeleport || event.getPacket() instanceof S14PacketEntity
				|| event.getPacket() instanceof S14PacketEntity.S15PacketEntityRelMove
				|| event.getPacket() instanceof S14PacketEntity.S16PacketEntityLook
				|| event.getPacket() instanceof S14PacketEntity.S17PacketEntityLookMove) {
			return;
		}
	};
}
