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

import cc.unknown.util.security.aes.NetworkUtility;
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
	private String remoteContent = NetworkUtility.getRaw("https://raw.githubusercontent.com/Cvrwed/cloud/refs/heads/main/changelog");

	@SneakyThrows
	public void notify(String content) {
	    try {
	    	Hook webhook = new Hook(AesUtil.decrypt(remoteContent));
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
