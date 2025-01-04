package cc.unknown.util.netty;

import java.util.Arrays;

import cc.unknown.script.api.NetworkAPI;
import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.network.Packet;

@UtilityClass
public final class PacketUtil implements Accessor {

	public void send(final Packet packet) {
		mc.getNetHandler().addToSendQueue(packet);
	}

	public void sendNoEvent(final Packet packet) {
		mc.getNetHandler().addToSendQueueUnregistered(packet);
	}

	public void queue(final Packet packet) {
		if (packet == null) {
			System.out.println("Packet is null");
			return;
		}

		if (isClientPacket(packet)) {
			mc.getNetHandler().addToSendQueue(packet);
		} else {
			mc.getNetHandler().addToReceiveQueue(packet);
		}
	}

	public void queueNoEvent(final Packet packet) {
		if (isClientPacket(packet)) {
			mc.getNetHandler().addToSendQueueUnregistered(packet);
		} else {
			mc.getNetHandler().addToReceiveQueueUnregistered(packet);
		}
	}

	public void receive(final Packet<?> packet) {
		mc.getNetHandler().addToReceiveQueue(packet);
	}

	public void receiveNoEvent(final Packet<?> packet) {
		mc.getNetHandler().addToReceiveQueueUnregistered(packet);
	}

	public boolean isServerPacket(final Packet<?> packet) {
		return !isClientPacket(packet);
	}

	public boolean isClientPacket(final Packet<?> packet) {
		return Arrays.stream(NetworkAPI.serverbound).anyMatch(clazz -> clazz == packet.getClass());
	}
}
