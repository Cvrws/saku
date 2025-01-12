package cc.unknown.util.netty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.util.Accessor;
import cc.unknown.util.structure.ValuePair;
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

    public static List<ValuePair> parse(String query, java.nio.charset.Charset charset) {
        List<ValuePair> result = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return result;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            try {
                String key = URLDecoder.decode(keyValue[0], charset.name());
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], charset.name()) : "";
                result.add(new ValuePair(key, value));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
    
	public int getPing(EntityPlayer player) {
		return mc.getNetHandler().getPlayerInfo(player.getUniqueID()) != null ? mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() : 0;
	}
}
