package cc.unknown.util.client.irc;

import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatFormatting;
import net.minecraft.util.ChatStyle;

@Getter
@Setter
public class IRC extends ListenerAdapter implements Accessor {

	private String channelId = "1308613616198746143";
	private String token = "MTMwNTkzODQ4MDgwMjgyODM1MA.GvrF5r.Ud2K8aDUoWV-XnocGRdGhOUfpq2vW_JZViKrrU";

	private static JDA jda;

	public void init() {
		try {
			JDABuilder builder = JDABuilder.createDefault(token);
			builder.addEventListeners(new IRC());
			jda = builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    if (event.getAuthor().isBot()) {
	        return;
	    }

	    String content = event.getMessage().getContentRaw();
	    String username = event.getAuthor().getName();

	    sendToMinecraftChat(username, content);
	}
	
	private void sendToMinecraftChat(String username, String content) {
	    String message = ChatFormatting.BLUE + "[Discord] " + ChatFormatting.RED + username + ": " + ChatFormatting.WHITE + content;
	    
	    ChatUtil.display(message);
	    
	}
	
	public void sendMessage(String message, String clientBrand) {
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel != null) {
			channel.sendMessage(getHeaders(clientBrand) + message).queue();
		}
	}

	public String getHeaders(String clientBrand) {
	    return "-# [IRC] " + mc.getSession().getUsername() + ": ";
	}
}
