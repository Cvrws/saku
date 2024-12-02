package cc.unknown.util.security.remote;

import cc.unknown.util.security.aes.AesUtil;
import cc.unknown.util.security.aes.NetworkUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RemoteUtil {
	protected String api = "https://raw.githubusercontent.com/Cvrwed/cloud/refs/heads/main/todo";
	public String SECRET_KEY = AesUtil.decrypt2(NetworkUtil.getRaw(api, "a"));
	public String tokenRemote = NetworkUtil.getRaw(api, "b");
	public String authRemote = NetworkUtil.getRaw(api, "c");
}
