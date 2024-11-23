package cc.unknown.util.security.aes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NetworkUtility {

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
}
