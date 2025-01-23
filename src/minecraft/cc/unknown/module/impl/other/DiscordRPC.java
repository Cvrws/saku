package cc.unknown.module.impl.other;

import java.util.Arrays;

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

@ModuleInfo(aliases = "Discord RPC", description = "Discord status", category = Category.OTHER)
public final class DiscordRPC extends Module {
	
	@Override
	public void onEnable() {
		if (Sakura.instance.getDiscordHandler().running) {
			Sakura.instance.getDiscordHandler().stop();
		} else {
			Sakura.instance.getDiscordHandler().start();
		}
	}
	
	@Override
	public void onDisable() {
		Sakura.instance.getDiscordHandler().stop();
	}
}
