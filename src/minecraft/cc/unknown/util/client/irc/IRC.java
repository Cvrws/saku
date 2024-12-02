package cc.unknown.util.client.irc;

import static cc.unknown.util.client.StreamerUtil.blue;
import static cc.unknown.util.client.StreamerUtil.darkAqua;
import static cc.unknown.util.client.StreamerUtil.lightPurple;
import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;

import cc.unknown.util.client.user.UserUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.security.HardwareUtil;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.blacklist.BlackListUtil;
import cc.unknown.util.security.hook.WebhookUtil;
import cc.unknown.util.security.remote.RemoteUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
@Setter
public class IRC extends ListenerAdapter {
	private String channelId = "1308613616198746143";

	@Getter
	private JDA jda;
	private String lastMessage = "";

	@SneakyThrows
	public synchronized void init() {
        if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN) {
            jda = JDABuilder.createDefault(AesUtil.decrypt(RemoteUtil.tokenRemote)).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new IRC()).build();
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
	    	
	    	PlayerUtil.display(lightPurple + "[Sakura] " + darkAqua + extUser + ": " + reset + extContent);
	    } else {
	    	PlayerUtil.display(blue + "[Discord] " + red + username + ": " + reset + content);
	    }
	}
	
    private String getPrefix(String content) {
        String pre1 = content.split(" ")[0];
        return pre1.replace("x","");
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
			channel.sendMessage("-# [IRC] " + UserUtil.getUser() + ": " + message).queue();
		}
	}
	
	public String format(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
