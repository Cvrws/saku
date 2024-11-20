package cc.unknown.util.client.irc;

import java.util.List;

import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
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
public class IRC extends ListenerAdapter implements Accessor {

	private String channelId = "1308613616198746143";
	private String token = "MTMwNTkzODQ4MDgwMjgyODM1MA.GvrF5r.Ud2K8aDUoWV-XnocGRdGhOUfpq2vW_JZViKrrU";

	@Getter
	private JDA jda;
	private String lastMessage = "";

	@SneakyThrows
	public void init() {		
        if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN) {
            jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new IRC()).build();
            jda.awaitReady();
        }
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    if (!event.getChannel().getId().equals(channelId)) {
	        return;
	    }

	    String messageContent = event.getMessage().getContentDisplay();
	    if (messageContent.isEmpty()) {
	        return;
	    }
	    
	    lastMessage = messageContent;

	    String username = format(event.getAuthor().getName());	    
	    if (lastMessage.startsWith("-# [IRC]") && !lastMessage.isEmpty()) {
	        String username2 = extractUsername(messageContent);
	        String message2 = extractMessage(messageContent);

	        clientToClient(message2, username2);
	    } else {
	        discordToClient(username, messageContent);
	    }
	}

	private String extractUsername(String messageContent) {
	    int startIdx = messageContent.indexOf("]") + 2;
	    int endIdx = messageContent.indexOf(":", startIdx);
	    if (startIdx > 0 && endIdx > startIdx) {
	        return messageContent.substring(startIdx, endIdx).trim();
	    }
	    return "";
	}

	private String extractMessage(String messageContent) {
	    int startIdx = messageContent.indexOf(":") + 1;
	    String message = messageContent.substring(startIdx).trim();
	    
	    if (message.startsWith("``") && message.endsWith("``")) {
	        message = message.substring(2, message.length() - 2);
	    }
	    
	    return message;
	}
	
	private void discordToClient(String username, String content) {
	    String message = ChatFormatting.BLUE + "[Discord] " + ChatFormatting.RED + username + ": " + ChatFormatting.WHITE + content;
	    ChatUtil.display(message);
	}
	
	private void clientToClient(String content, String username) {
	    String message = ChatFormatting.LIGHT_PURPLE + "[Sakura] " + ChatFormatting.DARK_AQUA + username + ": " + ChatFormatting.WHITE + content;
	    ChatUtil.display(message);
	}
	
	public void sendMessage(String message, String clientBrand) {
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel != null) {
			channel.sendMessage(getHeaders(clientBrand) + message).queue();
		}
	}
	
	public String format(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public String getHeaders(String clientBrand) {
	    return "-# [IRC] " + UserUtil.getUser() + ": ";
	}

    public List<Message> getMessages() {
        TextChannel channel = jda.getTextChannelById(channelId);
        return channel.getHistory().retrievePast(10).complete();
    }
}
