package cc.unknown.command.impl;

import java.util.Collections;
import java.util.List;

import cc.unknown.command.Command;
import cc.unknown.util.client.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public final class Name extends Command {

    public Name() {
        super("Copia y muestra tu nick", "name", "ign", "username", "nick", "nickname");
    }

    @Override
    public void execute(final String[] args) {
        final String name = mc.player.getName();

        GuiScreen.setClipboardString(name);
        ChatUtil.display("Copied your username to clipboard. (%s)", name);
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return args.length == 0 ? Collections.singletonList("name") : Collections.emptyList();
    }
}
