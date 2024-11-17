package cc.unknown.module.impl.other;

import java.util.Arrays;
import java.util.stream.Collectors;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.StringValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

@ModuleInfo(aliases = "Chat Bypass", description = "No rules in chat", category = Category.OTHER)
public final class ChatBypass extends Module {
		
	@EventLink
	public final Listener<PacketSendEvent> onPacket = event -> {
	    Packet<?> packet = event.getPacket();
		if (packet instanceof C01PacketChatMessage) {
			C01PacketChatMessage wrapper = (C01PacketChatMessage) packet;
			String message = wrapper.getMessage();
			if (!Arrays.stream(new String[]{"/", "!"}).anyMatch(message::startsWith)) {
				String m = Arrays.stream(message.split(" ")).map(word -> "i" + word).collect(Collectors.joining(" "));
				wrapper.setMessage(m);
				event.setPacket(wrapper);
			}
	    }
	};
}
