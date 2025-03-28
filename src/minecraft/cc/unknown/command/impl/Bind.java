package cc.unknown.command.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.module.Module;
import cc.unknown.util.interfaces.Bindable;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

public final class Bind extends Command {

    public Bind() {
        super("Binds a module to the given key", "bind", "b");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 3) {
            final Bindable bindable = Sakura.instance.getBindableManager().get(args[1]);

            if (bindable == null) {
            	error("Invalid module");
                return;
            }

            final String inputCharacter = args[2].toUpperCase();
            int keyCode;

            if (inputCharacter.toLowerCase().startsWith("mouse_")) {
                int mouseButton = -1;
                mouseButton = Integer.parseInt(inputCharacter.substring(6));

                int totalButtons = Mouse.getButtonCount();
                if (mouseButton < 0 || mouseButton >= totalButtons) {
                    return;
                }

                keyCode = 100 + mouseButton;
            } else {
            	keyCode = Keyboard.getKeyIndex(inputCharacter.toUpperCase());
            }
            
            bindable.setKey(keyCode);
            success("Bound " + bindable.getName() + " to " + (keyCode >= 100 ? "Mouse_" + (keyCode - 100) : Keyboard.getKeyName(keyCode)) + ".");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
        	getInstance().getBindableManager().getBinds().forEach(module -> {
                if (module.getKey() != 0) {
                    final String color = getTheme().getChatAccentColor().toString();
                    final String keyName = module.getKey() >= 100 ? "Mouse_" + (module.getKey() - 100) : Keyboard.getKeyName(module.getKey());

                    final ChatComponentText chatText = new ChatComponentText(color + "> " + module.getAliases()[0] + " �f " + keyName);
                    final ChatComponentText hoverText = new ChatComponentText("Click to remove " + module.getAliases()[0] + " bind");
                    chatText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".bind " + module.getName().replace(" ", "") + " none")).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                    mc.player.addChatMessage(chatText);
                }
            });

        } else {
            warning(".bind <list/module/config> (KEY)");
        }
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