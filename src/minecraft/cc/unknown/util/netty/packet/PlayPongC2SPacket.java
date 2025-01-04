package cc.unknown.util.netty.packet;

import java.io.IOException;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;

public final class PlayPongC2SPacket extends RawPacket {
    private int parameter;

    public PlayPongC2SPacket(int parameter) {
        super(0, EnumConnectionState.PLAY);
        this.parameter = parameter;
    }

    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        parameter = buf.readInt();
    }

    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeInt(parameter);
    }

    @Override
    public int getPacketID() {
        return 0x1D;
    }
}