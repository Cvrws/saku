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
	private String authRemote = NetworkUtility.getRaw("https://raw.githubusercontent.com/Cvrwed/cloud/refs/heads/main/changelog");
	private String ircCrypt = "beODxuM36qmG5349T3nTtEZWwZ+Hao6/cn8mldvaIj+mO025Z8ZWqDsnu14TUidIYMr+M7vR5F+VE3ElNkqfmmem3kjjTNrZm1eKiI5rWtvRX4v26MU21a74c6oHgts70Q8uLLBuNkufqfGr1glJtnON3Sj6+7nWEV5tgc7m55c=";
	
	@SneakyThrows
	public void notify(String content) {
	    try {
	    	Hook webhook = new Hook(AesUtil.decrypt(authRemote));
	        webhook.setAvatarUrl("https://i.ibb.co/bNfZbWL/sakura.png");
	        webhook.setUsername("Sakura Auth");
	        webhook.setContent("-# [" + getTime() + "] " + content);
	        webhook.execute();
	        System.out.println(AesUtil.encrypt("1308613616198746143"));
	    } catch (Exception e) {
	        System.exit(0);
	    }
	}
	
	@SneakyThrows
	public void ircMessage(String message) {
		Hook irc = new Hook(AesUtil.decrypt(ircCrypt));
		irc.setAvatarUrl("https://i.ibb.co/bNfZbWL/sakura.png");
		irc.setUsername("Sakura IRC");
		irc.setContent("-# [IRC] " + UserUtil.getUser() + ": " + message);
		irc.execute();
	}
    
    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }
}
