package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.managers.ConfigManager;
import cc.unknown.util.file.config.ConfigFile;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

public final class Config extends Command {

    public Config() {
        super("Te permite guardar o cargar configs", "cfg");
    }

    @Override
    public void execute(final String[] args) {
        final ConfigManager configManager = getInstance().getConfigManager();
        final String command = args[1].toLowerCase();

        switch (args.length) {
            case 3:
                final String name = args[2];

                switch (command) {
                    case "load":
                        configManager.update();

                        final ConfigFile config = configManager.get(name);

                        if (config != null) {
                            CompletableFuture.runAsync(() -> {
                                if (config.read()) {
                                	success("Loaded success!");
                                }
                            });
                        }
                        break;

                    case "save":
                    case "create":
                        if (name.equalsIgnoreCase("latest")) {
                            return;
                        }

                        CompletableFuture.runAsync(() -> {
                            configManager.set(name);

                            success("Saved config file!");
                        });
                        break;
                        
                    case "remove":
                        CompletableFuture.runAsync(() -> {
                            ConfigFile configToRemove = configManager.get(name);
                            if (configToRemove != null && configToRemove.getFile().delete()) {
                                configManager.update();
                                success("Removed config file: " + name);
                            } else {
                            	success("Failed to remove config file: " + name);
                            }
                        });
                        break;
                    default:
                        warning("Usage: .config save/load/list/folder");
                        break;
                }
                break;

            case 2:
                switch (command) {
                    case "list":
                        configManager.update();

                        configManager.forEach(configFile -> {
                            final String configName = configFile.getFile().getName().replace(".json", "");
                            final String configCommand = ".config load " + configName;
                            final String color = getTheme().getChatAccentColor().toString();

                            final ChatComponentText chatText = new ChatComponentText(color + "> " + configName);
                            mc.player.addChatMessage(chatText);
                        });
                        break;
                        
                    case "open":
                    case "folder":
                        try {
                            Desktop desktop = Desktop.getDesktop();
                            File dirToOpen = new File(String.valueOf(ConfigManager.CONFIG_DIRECTORY));
                            desktop.open(dirToOpen);
                            success("Opened config folder");
                        } catch (IllegalArgumentException | IOException iae) {
                        	error("Config file not found!");
                        }
                        break;
                }
                break;
        }
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        switch (args.length) {
            case 1:
                return Arrays.asList("list", "load", "save", "remove", "folder").stream()
                        .filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());

            case 2:
                if ("load".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0])) {
                    return getInstance().getConfigManager().stream()
                            .map(config -> config.getFile().getAbsoluteFile().getName().replace(".json", ""))
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();

            default:
                return Collections.emptyList();
        }
    }
}
