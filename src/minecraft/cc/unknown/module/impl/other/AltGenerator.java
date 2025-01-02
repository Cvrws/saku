package cc.unknown.module.impl.other;

import java.util.Random;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.ui.menu.MainMenu;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;

@ModuleInfo(aliases = "Alt Generator", description = "Genera cuentas de 3 letras no registradas para universocraft [BETA]", category = Category.OTHER)
public final class AltGenerator extends Module {

    private String serverAddress = "mc.universocraft.com";
    private int serverPort = 25565;
    private String currentName;

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (currentName == null) {
            currentName = genName();
            PlayerUtil.displayInClient("Attempting with name: " + currentName);

            mc.session = new Session(currentName, "", "", "mojang");

            connectToServer(serverAddress, serverPort);
        }
    };

    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof S02PacketChat) {
            S02PacketChat wrapped = (S02PacketChat) packet;
            String message = wrapped.getChatComponent().getUnformattedText();

            if (message.contains("/codigo")) {
                PlayerUtil.displayInClient("Success " + currentName + ".");

                disconnectAndRetry();
            }
        }
    };

    private String genName() {
        String letters = "abcdefghjklmnoprstvyz1234567890";
        StringBuilder name = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            name.append(letters.charAt(random.nextInt(letters.length())));
        }
        return name.toString();
    }

    private void connectToServer(String address, int port) {
        mc.theWorld = null;
        mc.displayGuiScreen(new GuiConnecting(new MainMenu(), mc, new ServerData("FurryServer", address, false)));
    }

    private void disconnectAndRetry() {
        if (mc.player != null && mc.getNetHandler() != null) {
            mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText("Disconnecting..."));
        }

        currentName = null;

    }
}