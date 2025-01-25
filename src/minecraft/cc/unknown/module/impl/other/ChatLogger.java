package cc.unknown.module.impl.other;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(aliases = "Chat Logger", description = "Comienza un registro de tus chats", category = Category.OTHER)
public final class ChatLogger extends Module {
	
    private final File dir = new File(mc.mcDataDir, "Sakura" + File.separator + "chatlogs");
    private File chatLog;
    public String fileName;
    public String extension = "txt";
    
    @Override
    public void onEnable() {
        if (!dir.exists()) {
            dir.mkdir();
        }
    	
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
        fileName = dtf.format(now) + "." + extension;
        chatLog = new File(dir, fileName);
        if (!chatLog.exists()) {
            try {
                chatLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onEnable();
    }
	
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
	    Packet<?> packet = event.getPacket();
	    if (packet instanceof S02PacketChat) {
	    	S02PacketChat wrapper = (S02PacketChat) packet;
	    	String message = wrapper.getChatComponent().getUnformattedText();
	    	
            try (FileWriter fw = new FileWriter(chatLog.getPath(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println(message);
            } catch (IOException e) {
            }
	    }
	};
}
