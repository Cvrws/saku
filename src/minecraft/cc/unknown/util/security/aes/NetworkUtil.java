package cc.unknown.util.security.aes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import cc.unknown.util.Accessor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class NetworkUtil implements Accessor {

	@SneakyThrows
    public String getRaw(String url) {
    	URL website = new URL(url);
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream()))) {
    		StringBuilder response = new StringBuilder();
    		String line;
    		while ((line = br.readLine()) != null) {
    			response.append(line);
    		}
    		return response.toString().trim();
    	}
    }

    @SneakyThrows
    public String getRaw(String url, String instruction) {
    	URL website = new URL(url);
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream()))) {
    		StringBuilder response = new StringBuilder();
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(" \\| ");
    			if (parts.length == 2 && parts[1].equals(instruction)) {
    				response.append(parts[0]);
    				break;
    			}
    		}
    		return response.toString().trim();
    	}
    }
    
    
	public int getPing(EntityPlayer player) {
		return mc.getNetHandler().getPlayerInfo(player.getUniqueID()) != null ? mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() : 0;
	}
}
