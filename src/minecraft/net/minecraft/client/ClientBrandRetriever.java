package net.minecraft.client;

import cc.unknown.Sakura;
import cc.unknown.module.impl.other.ClientSpoofer;

public class ClientBrandRetriever {
	public static String getClientModName() {
		ClientSpoofer cs = Sakura.instance.getModuleManager().get(ClientSpoofer.class);
		if (cs.isEnabled()) {
			return cs.brand.getValue();
		}
		return "vanilla";
	}
}
