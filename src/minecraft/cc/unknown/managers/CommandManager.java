package cc.unknown.managers;

import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;
import static cc.unknown.util.client.StreamerUtil.yellow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.command.impl.*;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ChatInputEvent;
import cc.unknown.util.player.PlayerUtil;
import lombok.Getter;

@Getter
public final class CommandManager {
    public static final List<Command> commandList = new ArrayList<>();
    public static final String prefix = ".";

    public CommandManager() {
        this.add(new Bind());
        this.add(new Config());
        this.add(new Friend());
        this.add(new Help());
        this.add(new Name());
        this.add(new Join());
        this.add(new Script());
        this.add(new Toggle());
        this.add(new Target());
        this.add(new Transaction());
    }

    public void add(Command command) {
        this.commandList.add(command);
    }

    public <T extends Command> T get(final String name) {
        return (T) this.commandList.stream().filter(command -> Arrays.stream(command.getExpressions()).anyMatch(expression -> expression.equalsIgnoreCase(name))).findAny().orElse(null);
    }
}