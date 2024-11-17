package cc.unknown.command.impl;

import static cc.unknown.util.streamer.StreamerUtil.gray;
import static cc.unknown.util.streamer.StreamerUtil.red;
import static cc.unknown.util.streamer.StreamerUtil.reset;
import static cc.unknown.util.streamer.StreamerUtil.yellow;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;

public final class Help extends Command {

    public Help() {
        super("Gives you a list of all commands", "help");
    }
    
    @Override
    public void execute(final String[] args) {
    	String prefix = yellow + "[" + red + "*" + yellow + "]" + reset + " ";
        Sakura.instance.getCommandManager().getCommandList()
                .forEach(command -> ChatUtil.display(prefix + StringUtils.capitalize(command.getExpressions()[0]) + " " + Arrays.toString(command.getExpressions()) + ": " + gray + command.getDescription()));
    }
}