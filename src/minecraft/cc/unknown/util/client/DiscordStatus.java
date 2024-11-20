package cc.unknown.util.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import cc.unknown.util.security.user.UserUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class DiscordStatus implements Accessor {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public boolean started;
    public Map<String, ServerData> serverDataMap = new HashMap<>();
    public String serverName;
    public String serverAddresses;
    public String status = "";
    
	public void init() {
		status = "ON";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRPC.discordInitialize("1305938480802828350", handlers, true);

        scheduler.scheduleAtFixedRate(() -> {
        	DiscordRPC.discordRunCallbacks();
            
        }, 0, 2, TimeUnit.SECONDS);
        started = true;
	}
	
    public void stop() {
        DiscordRPC.discordShutdown();
        status = "OFF";
        started = false;
    }
    
    public void makeRPC(String serverName, String user) {
    	DiscordRichPresence rpc = new DiscordRichPresence.Builder(user)
                .setDetails(serverName)
                .setBigImage("sakura", Sakura.NAME + " " + Sakura.VERSION_FULL)
                .setStartTimestamps(System.currentTimeMillis())
                .build();
    	DiscordRPC.discordUpdatePresence(rpc);
    }
    
    @SneakyThrows
    public void fetchServerMappings() {
        try (InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("sakura/mapping/servers.json")).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Gson gson = new Gson();

            List<ServerData> serverMappings = gson.fromJson(reader, new TypeToken<List<ServerData>>(){}.getType());

            for (ServerData mapping : serverMappings) {
            	ServerData serverData = new ServerData();
                serverData.name = mapping.name;
                serverDataMap.put(mapping.primaryAddress.toLowerCase(), serverData);

                for (String address : mapping.addresses) {
                    serverDataMap.put(address.toLowerCase(), serverData);
                }
            }
        }
    }

    @SneakyThrows
    private String readFromConnection(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public boolean findServerData(String serverIP) {
        if (serverIP == null) return false;
        serverIP = serverIP.toLowerCase();
        serverAddresses = serverIP;
        ServerData serverData = serverDataMap.get(serverIP);

        if (serverData != null) {
            serverName = "Playing in " + serverData.name;
            return true;
        }
        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            String knownAddress = entry.getKey();
            if (serverIP.endsWith(knownAddress)) {
                serverData = entry.getValue();
                serverName = "Playing in " + serverData.name;
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
