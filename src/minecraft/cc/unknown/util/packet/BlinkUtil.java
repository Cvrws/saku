package cc.unknown.util.packet;

import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

@UtilityClass
public class BlinkUtil implements Accessor {
	
    private final CopyOnWriteArrayList<Packet> clientPackets = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Packet> serverPackets = new CopyOnWriteArrayList<>();
    private boolean BLINK_INBOUND = false;
    private boolean BLINK_OUTBOUND = false;
    private boolean isEnabled = false;
	
    public void setConfig(boolean inboundState, boolean outboundState) {
        BLINK_INBOUND = inboundState;
        BLINK_OUTBOUND = outboundState;
    }

    public void enable() {
        enable(false, true);
    }

    public void enable(boolean inboundState, boolean outboundState) {
        setConfig(inboundState, outboundState);
        isEnabled = true;
        clientPackets.clear();
        serverPackets.clear();
    }
    
    public void disable() {
        disable(true);
    }

    public void disable(boolean releasePackets) {
        if (releasePackets) {
            releasePackets();
        }
        isEnabled = false;
    }

    public void clearPackets() {
        clearPackets(true, true);
    }

    public void clearPackets(boolean clearInbound, boolean clearOutbound) {
        if (clearInbound) serverPackets.clear();
        if (clearOutbound) clientPackets.clear();
    }

    public void releasePackets() {
        releasePackets(true, true);
    }

    public void releasePackets(boolean releaseInbound, boolean releaseOutgoing) {
        if (releaseInbound) {
            serverPackets.forEach(packet -> {
                PacketUtil.receiveNoEvent(packet);
                serverPackets.remove(packet);
            });
        }

        if (releaseOutgoing) {
            clientPackets.forEach(packet -> {
                PacketUtil.sendNoEvent(packet);
                clientPackets.remove(packet);
            });
        }

        clearPackets();
    }

    public int getSize() {
        return getSize(true, true);
    }

    public int getSize(boolean sizeInbound, boolean sizeOutgoing) {
        int size = 0;
        if (sizeInbound) size += serverPackets.size();
        if (sizeOutgoing) size += clientPackets.size();
        return size;
    }
    
    public boolean handleReceivePacket(PacketReceiveEvent event) {
        final Packet packet = event.getPacket();
        if (mc.player == null || mc.theWorld == null || mc.player.ticksExisted < 4) return false;
        if (isBlinking()) {
        	if (BLINK_INBOUND) {
                if (!(packet instanceof S00PacketDisconnect || packet instanceof S01PacketPong ||
                        packet instanceof S00PacketServerInfo || packet instanceof S3EPacketTeams ||
                        packet instanceof S19PacketEntityStatus || packet instanceof S02PacketChat ||
                        packet instanceof S3BPacketScoreboardObjective || packet instanceof S0CPacketSpawnPlayer ||
                        packet instanceof S40PacketDisconnect )) {

                    serverPackets.add(packet);
                    return true;
                }
            }
        }
    	return false;
    }
    
    public boolean handleSendPacket(PacketSendEvent event) {
        final Packet packet = event.getPacket();
        if (mc.player == null || mc.theWorld == null || mc.player.ticksExisted < 4) return false;
        if (isBlinking()) {
        	if (BLINK_OUTBOUND) {
                if (!(packet instanceof C00PacketKeepAlive || packet instanceof C00Handshake ||
                        packet instanceof C00PacketLoginStart)) {
                    clientPackets.add(packet);
                    return true;
                }
            }
        }
    	return false;
    }
    
    public boolean isBlinking() {
        return isEnabled;
    }
}
