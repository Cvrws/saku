package cc.unknown.util.socket;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SocketUtil {
	private final byte[] API = { 104, 116, 116, 112, 115, 58, 47, 47, 114, 97, 119, 46, 103, 105, 116, 104, 117, 98, 117, 115, 101, 114, 99, 111, 110, 116, 101, 110, 116, 46, 99, 111, 109, 47, 67, 118, 114, 119, 115, 47, 99, 108, 111, 117, 100, 47, 114, 101, 102, 115, 47, 104, 101, 97, 100, 115, 47, 109, 97, 105, 110, 47, 116, 111, 100, 111 };

	public String key = EncryptUtil.decrypt2(NetworkUtil.getRaw(new String(API), "a"));
	public String ircBridge = NetworkUtil.getRaw(new String(API), "b");
	public String auth = NetworkUtil.getRaw(new String(API), "c");
}
