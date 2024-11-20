package cc.unknown.util.client.irc;

import cc.unknown.Sakura;
import cc.unknown.module.impl.movement.Sprint;
import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.security.user.UserUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.util.ChatFormatting;

@Getter
@Setter
public class IRC extends ListenerAdapter implements Accessor {

	private String channelId = "1308613616198746143";
	private String token = "MTMwNTkzODQ4MDgwMjgyODM1MA.GvrF5r.Ud2K8aDUoWV-XnocGRdGhOUfpq2vW_JZViKrrU";

	private static int connectionCount = 0;
	private static JDA jda;

	@SneakyThrows
	public void init() {		
        if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN) {
            jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new IRC()).build();
            jda.awaitReady();
        }
        
        updateBotStatus();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                decrementConnectionCount();
                
                if (jda != null) {
                	jda.shutdown();
                	System.out.println("Bot has been shut down.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
	}
	
	private synchronized void incrementConnectionCount() {
	    connectionCount++;
	    updateBotStatus();
	}

	private synchronized void decrementConnectionCount() {
	    if (connectionCount > 0) {
	        connectionCount--;
	    }
	    updateBotStatus();
	}

	private void updateBotStatus() {
	    if (jda != null && !(getModule(Sprint.class).logged)) {
	        String statusMessage = "Sakura users " + connectionCount + " connected!";
	        jda.getPresence().setActivity(Activity.playing(statusMessage));
	    }
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		updateBotStatus();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    if (event.getAuthor().isBot() || !event.getChannel().getId().equals(channelId)) {
	        return;
	    }

	    String messageContent = event.getMessage().getContentDisplay();
	    if (messageContent.isEmpty()) {
	        return;
	    }

	    String username = format(event.getAuthor().getName());
	    sendToMinecraft(username, messageContent);
	}
	
	private void sendToMinecraft(String username, String content) {
	    String message = ChatFormatting.BLUE + "[Discord] " + ChatFormatting.RED + username + ": " + ChatFormatting.WHITE + content;
	    ChatUtil.display(message);   
	}
	
	public void sendMessage(String message, String clientBrand) {
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel != null) {
			channel.sendMessage(getHeaders(clientBrand) + message).queue();
		}
	}
	
	private String format(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public String getHeaders(String clientBrand) {
	    return "-# [IRC] " + UserUtil.getUser() + ": ";
	}
}
