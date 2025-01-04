package cc.unknown.handlers;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.util.Accessor;
import lombok.Getter;
import net.minecraft.item.ItemStack;

public class SpoofHandler implements Accessor  {
	private static int spoofedSlot;

	@Getter
	private static boolean spoofing;

	public static void startSpoofing(int slot) {
		spoofing = true;
		spoofedSlot = slot;
	}

	public static void stopSpoofing() {
		spoofing = false;
	}

	public static int getSpoofedSlot() {
		return spoofing ? spoofedSlot : mc.player.inventory.currentItem;
	}

	public static ItemStack getSpoofedStack() {
		return spoofing ? mc.player.inventory.getStackInSlot(spoofedSlot) : mc.player.inventory.getCurrentItem();
	}

	@EventLink
	public Listener<WorldChangeEvent> onDisconnect = event -> {
		stopSpoofing();
	};
}
