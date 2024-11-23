package cc.unknown.util.security.hook;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.unknown.util.Accessor;
import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.hook.impl.Hook;
import cc.unknown.util.security.remote.RemoteUtil;
import cc.unknown.util.security.user.UserUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WebhookUtil implements Accessor {
	
	@SneakyThrows
	public void notify(String content) {
	    try {
	    	Hook webhook = new Hook(AesUtil.decrypt(RemoteUtil.authRemote));
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
		Hook irc = new Hook(AesUtil.decrypt(RemoteUtil.ircRemote));
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
