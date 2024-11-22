package cc.unknown.module.impl.other;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ChatInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.client.irc.IRC;
import cc.unknown.util.client.irc.Processor;
import cc.unknown.util.security.user.UserUtil;
import cc.unknown.value.impl.StringValue;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = {"Internet Relay Chat", "irc"}, description = "Habla con otros Sakura users [BETA]", category = Category.OTHER)
public final class InternetRelayChat extends Module {

    private final List<String> BLOCKED_PREFIXES = Arrays.asList("/", ".", "@here", "@everyone");
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private IRC ircBot = new IRC();
    private Processor irc = new Processor(ircBot);
    
    private final StringValue prefix = new StringValue("Prefix", this, "#");
    
    @Override
    public void onEnable() {
    	irc.initBot();
    }
    
    @Override
    public void onDisable() {
    	irc.stopBot();
    }
    
    @EventLink
    public final Listener<ChatInputEvent> onChat = event -> {
        String message = event.getMessage();

        if (message.startsWith(prefix.getValue()) && message.length() > 1) {
            event.setCancelled();
            message = message.substring(1);
            message = StringUtils.normalizeSpace(message);            
            if (!isBlocked(message)) {
            	irc.sendMessage(" ``" + message + "``");
            }
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