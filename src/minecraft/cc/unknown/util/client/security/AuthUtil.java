package cc.unknown.util.client.security;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.unknown.util.client.security.impl.Hooker;
import cc.unknown.util.socket.EncryptUtil;
import cc.unknown.util.socket.SocketUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtil {
	protected String avatar = "https://i.ibb.co/bNfZbWL/sakura.png";
	
	@SneakyThrows
	public void notify(String message) {
	    try {
	        String username = "Auth System";
	        String remote = EncryptUtil.decrypt(SocketUtil.auth);
	        constructor(remote, avatar, username, "-# [" + getTime() + "] " + message);
	    } catch (Exception e) {
	        System.exit(0);
	    }
	}
	
	@SneakyThrows
	public void constructor(String remote, String avatar, String name, String prefix) {
		Hooker hook = new Hooker(remote);
		hook.setAvatarUrl(avatar);
		hook.setUsername(name);
		hook.setContent(prefix);
		hook.execute();
	}
    
    public String getTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }
}
