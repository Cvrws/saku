package cc.unknown.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import lombok.SneakyThrows;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;

public class DiscordHandler implements Accessor {
    private boolean running = true;
    private long timeElapsed = 0;
    private String discordUser = "";
    public String serverName;
    public String serverAddresses;
    public Map<String, ServerData> serverDataMap = new HashMap<>();

    public void init() {
        this.timeElapsed = System.currentTimeMillis();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(discordUser -> {
        	this.discordUser = discordUser.username;
        }).build();

        DiscordRPC.discordInitialize("1305938480802828350", handlers, true);
        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                while (running) {
                    if (mc.player != null) {
                        
                        if (mc.isSingleplayer()) {
                            update("", "in SinglePlayer");
                        } else if (fetchAndFindServerData(mc.getCurrentServerData().serverIP)) {
                        	update("", "Cheating on " + serverName);
                            
                        } else if (mc.currentScreen instanceof GuiDownloadTerrain) {
                            update("Loading World...", "");
                        }
                    } else {
                        if (mc.currentScreen instanceof GuiSelectWorld) {
                            update("Selecting World...", "");
                        } else if (mc.currentScreen instanceof GuiMultiplayer) {
                            update("In Multiplayer...", "");
                        } else if (mc.currentScreen instanceof GuiDownloadTerrain) {
                            update("Loading World...", "");
                        } else {
                            update("In MainMenu...", "");
                        }
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void stop() {
        running = false;
        DiscordRPC.discordShutdown();
    }
    
    public void update(String line1, String line2) {
        DiscordRichPresence.Builder rpc = new DiscordRichPresence.Builder(line2).setDetails(line1).setBigImage("sakura", "Sakura [v" + Sakura.VERSION + "]");
        rpc.setStartTimestamps(timeElapsed);
        DiscordRPC.discordUpdatePresence(rpc.build());
    }
    
    @SneakyThrows
    public boolean fetchAndFindServerData(String serverIP) {
        if (serverIP == null) return false;

        try (InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("sakura/mapping/servers.json")).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Gson gson = new Gson();

            List<ServerData> serverMappings = gson.fromJson(reader, new TypeToken<List<ServerData>>() {}.getType());

            for (ServerData mapping : serverMappings) {
                ServerData serverData = new ServerData();
                serverData.name = mapping.name;
                serverData.primaryAddress = mapping.primaryAddress;

                serverDataMap.put(mapping.primaryAddress.toLowerCase(), serverData);

                for (String address : mapping.addresses) {
                    serverDataMap.put(address.toLowerCase(), serverData);
                }
            }
        } catch (Exception e) {
            return false;
        }

        serverIP = serverIP.toLowerCase();
        serverAddresses = serverIP;
        ServerData serverData = serverDataMap.get(serverIP);

        if (serverData != null) {
            serverName = serverData.name;
            return true;
        }

        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            String knownAddress = entry.getKey();
            if (serverIP.endsWith(knownAddress)) {
                serverData = entry.getValue();
                serverName = serverData.name;
                return true;
            }
        }

        return false;
    }
    
    class ServerData {
        String name;
        String primaryAddress;
        List<String> addresses;
    }
}
