package cc.unknown.util.netty.packet;

import java.io.IOException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

@RequiredArgsConstructor
@Getter
public abstract class RawPacket implements Packet {

    private final int packetID;
    private final EnumConnectionState direction;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void processPacket(INetHandler handler) {

    }

}