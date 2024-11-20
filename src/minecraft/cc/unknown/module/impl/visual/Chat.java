package cc.unknown.module.impl.visual;

import java.util.Arrays;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

@ModuleInfo(aliases = "IRC", description = "Talk with other sakura users", category = Category.VISUALS)
public final class Chat extends Module {

    private static final List<String> BLOCKED_PREFIXES = Arrays.asList("/", ".", "@here", "@everyone");

	@EventLink
	public final Listener<PacketSendEvent> onPacketSend = event -> {
	    Packet<?> packet = event.getPacket();
	    if (packet instanceof C01PacketChatMessage) {
	        C01PacketChatMessage chatPacket = (C01PacketChatMessage) packet;
	        String sentMessage = chatPacket.getMessage();

	        if (isBlocked(sentMessage)) {
	            return;
	        }

	        Sakura.instance.getIrc().sendMessage(" ``" + sentMessage + "``");
	    }
	};

    private boolean isBlocked(String message) {
        for (String prefix : BLOCKED_PREFIXES) {
            if (message.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}