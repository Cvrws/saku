package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;

public final class Name extends Command {

    public Name() {
        super("Copia y muestra tu nick", "name", "ign", "username", "nick", "nickname");
    }

    @Override
    public void execute(final String[] args) {
        final String name = mc.player.getName();

        GuiScreen.setClipboardString(name);
        PlayerUtil.display("Copied your username to clipboard. (%s)", name);
    }
}
