package cc.unknown.module.impl.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
@ModuleInfo(aliases = "Anti Staff", description = "Detecta staffs del servidor", category = Category.OTHER)
public final class AntiStaff extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Universocraft"))
            .setDefault("Universocraft");

    private final File dir = new File(mc.mcDataDir, "Sakura" + File.separator + "staffs");
    private File staffFile;
    private Set<String> staffSet = new HashSet<>();
    private Set<String> tabList = new HashSet<>();
    
    @Override
    public void onEnable() {
        if (!dir.exists()) {
            dir.mkdir();
        }

        updateStaffFile();
        loadStaffList();

        super.onEnable();
    }

    private void updateStaffFile() {
        String modeName = mode.getValue().getName().toLowerCase();
        staffFile = new File(dir, modeName + ".txt");

        if (!staffFile.exists()) {
            try {
                staffFile.createNewFile();
                List<String> defaultStaff = getDefaultStaffList(modeName);
                Files.write(staffFile.toPath(), defaultStaff, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStaffList() {
        staffSet.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(staffFile))) {
            staffSet = reader.lines()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getDefaultStaffList(String mode) {
        switch (mode) {
            case "universocraft":
                return Arrays.asList("ocedq", "denila", "_JuPo_", "reginat", "Mygxl", "nywcol", "Fxrchus", "4WYA", "Cvvrlss");
            default:
                return Collections.emptyList();
        }
    }

    public boolean isStaff(String nickname) {
        return staffSet.contains(nickname.toLowerCase());
    }
    
    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        final Packet packet = event.getPacket();

        if (packet instanceof S02PacketChat) {
            S02PacketChat chatPacket = (S02PacketChat) packet;
            String message = chatPacket.getChatComponent().getUnformattedText().toLowerCase();

            for (String staff : staffSet) {
                if (message.contains(staff)) {
                    ChatUtil.display("Staff Detectado: " + staff);
                }
            }
        }

    };
}
