package cc.unknown.util.client.irc;

import static cc.unknown.util.streamer.StreamerUtil.blue;
import static cc.unknown.util.streamer.StreamerUtil.darkAqua;
import static cc.unknown.util.streamer.StreamerUtil.lightPurple;
import static cc.unknown.util.streamer.StreamerUtil.red;
import static cc.unknown.util.streamer.StreamerUtil.reset;

import java.util.List;

import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.hook.AuthUtil;
import cc.unknown.util.security.user.UserUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.util.ChatFormatting;

@Getter
@Setter
public class IRC extends ListenerAdapter {
	private String channelId = "1308613616198746143";
	private String token = "Izj7lzMa2QdWdWlR1oxm8Tlu2yVWO+nePVcf0pE7oVkqfK0Y6wZMP1qO+/n+iFvWBdxYL4AwuiHm94hnjZRRll8ibfOATugLsUmDgZb/kx8=";

	@Getter
	private JDA jda;
	private String lastMessage = "";

	@SneakyThrows
	public void init() {		
        if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN) {
            jda = JDABuilder.createDefault(AesUtil.decrypt(token)).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new IRC()).build();
            jda.awaitReady();
        }
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    if (!event.getChannel().getId().equals(channelId)) {
	        return;
	    }

	    String content = event.getMessage().getContentDisplay();
	    if (content.isEmpty()) {
	        return;
	    }
	    
	    lastMessage = content;

	    String username = format(event.getAuthor().getName());	    
	    if (lastMessage.startsWith("-# [IRC]") && !lastMessage.isEmpty()) {
	    	String extUser = extractUsername(content);
	    	String extContent = extractMessage(content);
	    	
		    ChatUtil.display(lightPurple + "[Sakura] " + darkAqua + extUser + ": " + reset + extContent);
	    } else {
	    	ChatUtil.display(blue + "[Discord] " + red + username + ": " + reset + content);
	    }
	}

	private String extractUsername(String content) {
	    int startIdx = content.indexOf("]") + 2;
	    int endIdx = content.indexOf(":", startIdx);
	    if (startIdx > 0 && endIdx > startIdx) {
	        return content.substring(startIdx, endIdx).trim();
	    }
	    return "";
	}

	private String extractMessage(String content) {
	    int startIdx = content.indexOf(":") + 1;
	    String message = content.substring(startIdx).trim();
	    
	    if (message.startsWith("``") && message.endsWith("``")) {
	        message = message.substring(2, message.length() - 2);
	    }
	    
	    return message;
	}

	private String stripSpecialFormatting(String message) {
	    if (message.startsWith("``") && message.endsWith("``")) {
	        return message.substring(2, message.length() - 2).trim();
	    }
	    return message;
	}
	
	public synchronized void sendMessage(String message) {
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel != null) {
			AuthUtil.ircMessage(message);
		}
	}
	
	public String format(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
