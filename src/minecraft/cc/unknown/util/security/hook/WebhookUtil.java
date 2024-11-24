package cc.unknown.util.security.hook;

import static cc.unknown.util.security.remote.RemoteUtil.authRemote;
import static cc.unknown.util.security.remote.RemoteUtil.ircRemote;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.user.UserUtil;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.hook.impl.Hook;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WebhookUtil implements Accessor {
	
	protected String avatar = "https://i.ibb.co/bNfZbWL/sakura.png";
	
	@SneakyThrows
	public void notify(String message) {
	    try {
	        String username = "Auth System";
	        String remote = AesUtil.decrypt(authRemote);
	        constructor(remote, avatar, username, "-# [" + getTime() + "] " + message);
	    } catch (Exception e) {
	        System.exit(0);
	    }
	}
	
	@SneakyThrows
	public void ircMessage(String message) {
		String username = "IRC Bridge";
		String remote = AesUtil.decrypt(ircRemote);
		constructor(remote, avatar, username, "-# [IRC] " + UserUtil.getUser() + ": " + message);
	}
	
	@SneakyThrows
	public void constructor(String remote, String avatar, String name, String prefix) {
		Hook hook = new Hook(remote);
		hook.setAvatarUrl(avatar);
		hook.setUsername(name);
		hook.setContent(prefix);
		hook.execute();
	}
    
    public String getTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }
    
    public String getDiscordID() {
    	
    	
    	return null;
    }
}
