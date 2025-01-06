package cc.unknown.handlers;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ChatInputEvent;
import cc.unknown.managers.CommandManager;

public class CommandHandler {
    @EventLink
    public final Listener<ChatInputEvent> onChatInput = event -> {
        String message = event.getMessage();

        if (!message.startsWith(CommandManager.prefix)) return;

        message = message.substring(1);
        final String[] args = message.split(" ");

        final AtomicBoolean commandFound = new AtomicBoolean(false);

        try {
        	CommandManager.commandList.stream()
                    .filter(command ->
                    Arrays.stream(command.getExpressions())
                    .anyMatch(expression ->
                    expression.equalsIgnoreCase(args[0])))
                    .forEach(command -> {
                    	commandFound.set(true);
                        command.execute(args);
                    });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        event.setCancelled();
    };
}
