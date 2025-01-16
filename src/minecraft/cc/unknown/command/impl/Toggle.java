package cc.unknown.command.impl;

import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.module.Module;

public final class Toggle extends Command {

    public Toggle() {
        super("Toggles the specified module", "toggle", "t");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 2) {
            error(String.format(".%s <module>", args[0]));
            return;
        }
        final Module module = Sakura.instance.getModuleManager().get(args[1]);
        if (module == null) {
        	warning("Invalid module");
            return;
        }
        module.toggle();
        success(String.format("%s",
                module.getAliases()[0] + " " + (module.isEnabled() ? green + "enabled" : red + "disabled"))
        );
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        String moduleName = args[0];

        if (args.length == 1) {
            return Sakura.instance.getModuleManager().getAll().stream().map(Module::getName).filter(name -> name.toLowerCase().startsWith(moduleName.toLowerCase())).map(name -> name.replace(" ", "")).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}