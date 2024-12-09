package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.script.ScriptManager;
import cc.unknown.util.player.PlayerUtil;

public final class Script extends Command {

    public Script() {
        super("Script", "script", "scripts", "js");
    }

    @Override
    public void execute(final String[] args) {
        final String action = args[1].toLowerCase(Locale.getDefault());

        final ScriptManager scriptManager = getInstance().getScriptManager();

        final cc.unknown.script.Script script;
        if (args.length > 3) {
            script = scriptManager.getScript(args[2]);
            if (script == null) {
            	error("File not found " + args[2]);
                return;
            }
        } else script = null;

        try {
            switch (action) {
                case "load":
                    if (script == null) scriptManager.loadScripts();
                    else script.load();
                    break;

                case "reload":
                    getInstance().getScriptManager().reloadScripts();
                    getInstance().getClickGui().moduleList = new ConcurrentLinkedQueue<>();
                    break;

                case "unload":
                    if (script == null) scriptManager.unloadScripts();
                    else script.unload();
                    break;

                case "open":
                case "folder":
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        File dirToOpen = new File(String.valueOf(ScriptManager.SCRIPT_DIRECTORY));
                        desktop.open(dirToOpen);
                    } catch (IllegalArgumentException | IOException exception) {
                    	error("Script directory not found");
                    }
                    break;
            }

            success(
                    "Successfully " + action + "ed "
                            + (script == null ? "all scripts" : "\"" + script.getName() + "\"")
                            + "."
            );
        } catch (final Exception ex) {
            ex.printStackTrace();
            error("Failed to " + action + " a script. Stacktrace printed.");
        }
    }
}