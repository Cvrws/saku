package cc.unknown.module.impl.other;

import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.ChatComponentText;

@ModuleInfo(aliases = {"Internet Relay Chat", "Irc"}, description = "Comunicate con otros sakura users.", category = Category.OTHER)
public class InternetRelayChat extends Module {
	
	private String sakuraUser = "[S]";
	
    @Override
    public void onEnable() {
        super.onEnable();
        sendIRCIdentificationPacket();
    }
    
    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	sendIRCIdentificationPacket();
    };
	
    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
    	Packet<?> packet = event.getPacket();
    	
        if (packet instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem wrapper = (S38PacketPlayerListItem) event.getPacket();
            List<S38PacketPlayerListItem.AddPlayerData> players = wrapper.func_179767_a();
            for (S38PacketPlayerListItem.AddPlayerData playerData : players) {
                NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(playerData.getProfile().getId());
                if (playerInfo != null) {
                    String displayName = playerInfo.getDisplayName().getUnformattedText();
                    if (!displayName.contains("[IRC]")) {
                        playerInfo.setDisplayName(new ChatComponentText(displayName + " [IRC]"));
                    }
                }
            }
        }
    };
    
    private void sendIRCIdentificationPacket() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeString("IRC");
        buffer.writeString(sakuraUser);
        PacketUtil.send(new C17PacketCustomPayload("IRC|Ident", buffer));
    }
}