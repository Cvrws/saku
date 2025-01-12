package cc.unknown.command.impl;

import static cc.unknown.util.client.StreamerUtil.gray;
import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;
import static cc.unknown.util.client.StreamerUtil.yellow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.command.Command;

public final class Help extends Command {

    public Help() {
        super("Obtienes la lista de comandos", "help");
    }
    
    @Override
    public void execute(final String[] args) {
        getInstance().getCommandManager().commandList
                .forEach(command -> success(StringUtils.capitalize(command.getExpressions()[0]) + " " + Arrays.toString(command.getExpressions()) + ": " + gray + command.getDescription()));
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length > 0) {
            return Collections.emptyList();
        }

        return Collections.singletonList("help");
    }
}