package cc.unknown.util.client;

import cc.unknown.util.Accessor;
import cc.unknown.util.netty.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;

@UtilityClass
public class ChatUtil implements Accessor {
	
    public void display(final Object message, final Object... objects) {
        if (mc.player != null) {
            final String format = String.format(message.toString(), objects);
            mc.player.addChatMessage(new ChatComponentText(format));
        }
    }

    public void chat(final Object message) {
        if (mc.player != null) {
            PacketUtil.send(new C01PacketChatMessage(message.toString()));
        }
    }
}
