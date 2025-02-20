package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.command.Command;
import cc.unknown.managers.ScriptManager;

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
                    success(
                            "Successfully " + action + "ed "
                                    + (script == null ? "all scripts" : "\"" + script.getName() + "\"")
                                    + "."
                    );
                    break;

                case "unload":
                    if (script == null) scriptManager.unloadScripts();
                    else script.unload();
                    break;
                    
                case "list":
                    File scriptDir = new File(String.valueOf(ScriptManager.SCRIPT_DIRECTORY));
                    File[] scriptFiles = scriptDir.listFiles((dir, name) -> name.endsWith(".js"));
                    if (scriptFiles == null || scriptFiles.length == 0) {
                        error("No scripts found in the directory.");
                    } else {
                        success("Available scripts:");
                        for (File scriptFile : scriptFiles) {
                            success(scriptFile.getName());
                        }
                    }
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
        } catch (final Exception ex) {
            ex.printStackTrace();
            error("Failed to " + action + " a script. Stacktrace printed.");
        }
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("load", "reload", "unload", "open", "folder");
        } else if (args.length == 2 && (args[1].equalsIgnoreCase("load") || args[1].equalsIgnoreCase("unload"))) {
            return (List<String>) getInstance().getScriptManager().SCRIPT_FILE_FILTER;
        }
        return Collections.emptyList();
    }
}