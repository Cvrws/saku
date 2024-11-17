package cc.unknown.util.security.hook;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cc.unknown.util.Accessor;
import cc.unknown.util.security.HardwareUtil;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.hook.impl.Hook;
import cc.unknown.util.security.user.UserUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtil implements Accessor {
	String api = "https://discord.com/api/webhooks/";
	String id = "1307039017350467584/";
	String fr = "OcJaidlkTa_Fv0LLrcj6iiZP3jsVYlJaPP4RIocSkx6G0h6itVvOQ3xEFd_tf5wgkyIX";
	
	String rest = api + id + fr;
	
	@SneakyThrows
	public void notify(String content) {
	    try {
	    	Hook webhook = new Hook(rest);
	        webhook.setAvatarUrl("https://i.ibb.co/bNfZbWL/sakura.png");
	        webhook.setUsername("Sakura Auth");
	        webhook.setContent("-# [" + getTime() + "] " + content);
	        webhook.execute();
	    } catch (Exception e) {
	        System.exit(0);
	    }
	}
    
    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }
}
