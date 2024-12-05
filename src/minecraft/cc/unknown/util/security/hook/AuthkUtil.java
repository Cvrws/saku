package cc.unknown.util.security.hook;

import static cc.unknown.util.security.socket.SocketUtil.authentication;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.unknown.util.Accessor;
import cc.unknown.util.security.hook.impl.Webhook;
import cc.unknown.util.security.socket.AesUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthkUtil implements Accessor {
	
	protected String avatar = "https://i.ibb.co/bNfZbWL/sakura.png";
	
	@SneakyThrows
	public void notify(String message) {
	    try {
	        String username = "Auth System";
	        String auth = AesUtil.decrypt(authentication);
	        constructor(auth, avatar, username, "-# [" + getTime() + "] " + message);
	    } catch (Exception e) {
	        System.exit(0);
	    }
	}
	
	@SneakyThrows
	public void constructor(String remote, String avatar, String name, String prefix) {
		Webhook hook = new Webhook(remote);
		hook.setAvatarUrl(avatar);
		hook.setUsername(name);
		hook.setContent(prefix);
		hook.execute();
	}
    
    public String getTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }
}
