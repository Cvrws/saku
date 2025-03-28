package cc.unknown.module.impl.other;

import java.util.Arrays;

import com.sun.jna.Platform;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.TextValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(aliases = "Discord RPC", description = "Discord status", category = Category.OTHER, autoEnabled = true)
public final class DiscordRPC extends Module {
	
	@Override
	public void onEnable() {
		if (Platform.isWindows()) {
			Sakura.instance.getDiscordHandler().start();
		}
	}
	
	@Override
	public void onDisable() {
		if (Platform.isWindows()) {
			Sakura.instance.getDiscordHandler().stop();
		}
	}
}
