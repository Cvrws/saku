package cc.unknown.component.impl.render;

import static cc.unknown.util.client.DiscordStatus.fetchServerMappings;
import static cc.unknown.util.client.DiscordStatus.findServerData;
import static cc.unknown.util.client.DiscordStatus.makeRPC;
import static cc.unknown.util.client.DiscordStatus.serverAddresses;
import static cc.unknown.util.client.DiscordStatus.serverDataMap;
import static cc.unknown.util.client.DiscordStatus.serverName;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.util.client.DiscordStatus;
import cc.unknown.util.security.user.UserUtil;

public class StatusComponent extends Component {

	@EventLink(value = Priority.EXTREMELY_HIGH)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> displayServer();
	
    public void displayServer() {
    	try {
	        String currentServerIP = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : null;
	
	        if (currentServerIP != null && !currentServerIP.endsWith(serverAddresses)) {
	            if (serverDataMap.isEmpty()) {
	            	fetchServerMappings();
	            }
	            
	            if (findServerData(currentServerIP)) {
	            	makeRPC(serverName, "User: " + UserUtil.getUser());
	            } else {
	            	DiscordStatus.makeRPC("in Main Menu", "User: " + UserUtil.getUser());
	            }
	        }
    	} catch (NullPointerException ignored) {
    	}
    }
}
