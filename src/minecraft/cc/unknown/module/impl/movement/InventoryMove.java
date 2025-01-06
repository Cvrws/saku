package cc.unknown.module.impl.movement;

import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

@ModuleInfo(aliases = { "Inventory Move",
		"Inv Move" }, description = "Te permite moverte con el inventario abierto", category = Category.MOVEMENT)
public class InventoryMove extends Module {

	private final KeyBinding[] AFFECTED_BINDINGS = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump };

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (mc.currentScreen instanceof GuiChat || mc.currentScreen == this.getClickGUI()) {
			return;
		}

		for (final KeyBinding bind : AFFECTED_BINDINGS) {
			bind.setPressed(GameSettings.isKeyDown(bind));
		}	
	};
}
