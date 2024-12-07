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
import cc.unknown.util.client.irc.IRC;
import cc.unknown.value.impl.StringValue;

@ModuleInfo(aliases = {"Irc", "Internet Relay Chat"}, description = "Habla con otros Sakura users", category = Category.OTHER)
public final class InternetRelayChat extends Module {

    private final List<String> blockWords = Arrays.asList("/", ".", "@here", "@everyone");
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private IRC irc = new IRC();
    
    private final StringValue prefix = new StringValue("Prefix", this, "#");
    
    @Override
    public void onEnable() {
    	irc.init();
    }
    
    @Override
    public void onDisable() {
    	irc.getJda().shutdown();
    }
    
    @EventLink
    public final Listener<ChatInputEvent> onChat = event -> {
        String message = event.getMessage();

        if (message.startsWith(prefix.getValue()) && message.length() > 1) {
            event.setCancelled();
            message = message.substring(1);
            message = StringUtils.normalizeSpace(message);   
            if (!isBlocked(message)) {
                String finalMessage = message;
            	executor.execute(() -> irc.sendMessage(" ``" + finalMessage + "``"));
            }
        }
    };
    
    private boolean isBlocked(String message) {
        for (String words : blockWords) {
            if (message.startsWith(words)) {
                return true;
            }
        }
        return false;
    }
}