package cc.unknown.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.command.impl.*;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ChatInputEvent;
import lombok.Getter;

@Getter
public final class CommandManager {
    public static final List<Command> commandList = new ArrayList<>();
    public static final String prefix = ".";

    public void init() {
        this.add(new Bind());
        this.add(new Config());
        this.add(new Friend());
        this.add(new Help());
        this.add(new Name());
        this.add(new Join());
        this.add(new MemoryLeak());
        this.add(new Script());
        this.add(new Toggle());
        this.add(new Target());
        this.add(new Transaction());
        
        Sakura.instance.getEventBus().register(this);
    }

    public void add(Command command) {
        this.commandList.add(command);
    }

    public <T extends Command> T get(final String name) {
        return (T) this.commandList.stream().filter(command -> Arrays.stream(command.getExpressions()).anyMatch(expression -> expression.equalsIgnoreCase(name))).findAny().orElse(null);
    }
    
    @EventLink
    public final Listener<ChatInputEvent> onChatInput = event -> {
        String message = event.getMessage();

        if (!message.startsWith(prefix)) return;

        message = message.substring(1);
        final String[] args = message.split(" ");

        final AtomicBoolean commandFound = new AtomicBoolean(false);

        try {
        	commandList.stream().filter(command -> Arrays.stream(command.getExpressions()).anyMatch(expression -> expression.equalsIgnoreCase(args[0]))).forEach(command -> {
        		commandFound.set(true);
        		command.execute(args);
        	});
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        event.setCancelled();
    };
    
    public Collection<String> autoComplete(@NotNull String currCmd) {
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");

        List<String> ret = new ArrayList<>();


        Command currentCommand = split.length >= 1 ? commandList.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null) : null;

        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {

            if (currentCommand == null) return ret;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autocomplete = currentCommand.autocomplete(args.length + (currCmd.endsWith(" ") ? 1 : 0), args);

            return autocomplete == null ? new ArrayList<>() : autocomplete;
        } else if (split.length == 1) {
            for (Command command : commandList) {
                ret.addAll(command.getNameAndAliases());
            }

            return ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase())).collect(Collectors.toList());
        }

        return ret;
    }
}