package cc.unknown.module.impl.visual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.irc.UserUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.util.ResourceLocation;;

@ModuleInfo(aliases = {"Rich Presence", "discord status"}, category = Category.VISUALS, description = "Discord Status")
public class RichPresence extends Module {	
    private final String clientId = "1305938480802828350";
    private Map<String, ServerData> serverDataMap = new HashMap<>();
    private boolean started;
    private String serverName;
    private String serverAddresses;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    
	@EventLink(value = Priority.EXTREMELY_HIGH)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> onRPC();
	
    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
        started = false;
    }
    
    private void onRPC() {
    	try {
	        String currentServerIP = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : null;
	
	        if (!started || !isInGame() || (currentServerIP != null && !currentServerIP.endsWith(serverAddresses))) {
	            if (started) {
	                onDisable();
	            }
	            if (serverDataMap.isEmpty()) {
	                try {
	                    fetchServerMappings();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (isInGame()) {
	                if (findServerData(currentServerIP)) {
	                    DiscordRPC.discordUpdatePresence(makeRPC(serverName));
	                } else {
	                    updatePrivateRPC();
	                }
	            }
	            
	            DiscordEventHandlers handlers = new DiscordEventHandlers();
	            DiscordRPC.discordInitialize(clientId, handlers, true);

	            scheduler.scheduleAtFixedRate(() -> {
	                if (isEnabled()) {
	                    DiscordRPC.discordRunCallbacks();
	                }
	            }, 0, 2, TimeUnit.SECONDS);
	            started = true;
	        }
    	} catch (NullPointerException ignored) {
    		
    	}
    }
    
    private void fetchServerMappings() throws IOException {
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

    private String readFromConnection(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private boolean findServerData(String serverIP) {
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

    private void updatePrivateRPC() {
        serverName = "Playing in Private Server";
        DiscordRPC.discordUpdatePresence(makeRPC(serverName));
    }

    public DiscordRichPresence makeRPC(String serverName) {
        return new DiscordRichPresence.Builder("User: " + UserUtil.getUser())
                .setDetails(serverName)
                .setBigImage("sakura", "")
                .setStartTimestamps(System.currentTimeMillis())
                .build();
    }
    
    class ServerData {
        String name;
        String primaryAddress;
        List<String> addresses;
    }
}
