package cc.unknown.util.security.remote;

import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.aes.NetworkUtility;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RemoteUtil {
	protected String api = "https://raw.githubusercontent.com/Cvrwed/cloud/refs/heads/main/todo";
	public String SECRET_KEY = AesUtil.decrypt2(NetworkUtility.getRaw(api, "a"));
	public String tokenRemote = NetworkUtility.getRaw(api, "b");
	public String authRemote = NetworkUtility.getRaw(api, "c");
	public String ircRemote = NetworkUtility.getRaw(api, "d");
}
