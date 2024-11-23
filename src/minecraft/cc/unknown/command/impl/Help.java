package cc.unknown.command.impl;

import static cc.unknown.util.client.StreamerUtil.gray;
import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;
import static cc.unknown.util.client.StreamerUtil.yellow;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.command.Command;
import cc.unknown.util.player.PlayerUtil;

public final class Help extends Command {

    public Help() {
        super("Obtienes la lista de comandos", "help");
    }
    
    @Override
    public void execute(final String[] args) {
    	String prefix = yellow + "[" + red + "*" + yellow + "]" + reset + " ";
        getInstance().getCommandManager().getCommandList()
                .forEach(command -> PlayerUtil.display(prefix + StringUtils.capitalize(command.getExpressions()[0]) + " " + Arrays.toString(command.getExpressions()) + ": " + gray + command.getDescription()));
    }
}