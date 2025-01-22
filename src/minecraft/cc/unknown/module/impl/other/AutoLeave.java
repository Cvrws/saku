package cc.unknown.module.impl.other;

import java.util.Arrays;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.TextValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(aliases = "Auto Leave", description = "Sal del juego automaticámente", category = Category.OTHER)
public final class AutoLeave extends Module {
	
	private final TextValue text = new TextValue("Command", this, "/leave");
	
	@EventLink
	public final Listener<PacketReceiveEvent> onPacket = event -> {
	    Packet<?> packet = event.getPacket();
	    if (packet instanceof S02PacketChat) {
	        S02PacketChat wrapper = ((S02PacketChat) packet);
	        String receiveMessage = wrapper.getChatComponent().getFormattedText();

	        if (containsAny(receiveMessage, "has ganado", "has perdido", "Deseas salirte", "Han ganado", mc.player.getName() + " ha muerto")) {
	            String command = text.getValue();
	            
	            if (!command.isEmpty()) {
	                PlayerUtil.sendInChat(command);
	            }
	        }
	    }
	};

	private boolean containsAny(String source, String... targets) {
	    return Arrays.stream(targets).anyMatch(source::contains);
	}
}
