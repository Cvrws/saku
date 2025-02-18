package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "Fast Bow", description = "Elimina el delay al usar el arco", category = Category.PLAYER)
public class FastBow extends Module {

	private final NumberValue speed = new NumberValue("Packets", this, 20, 10, 100, 10);

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		ItemStack item = mc.player.getHeldItem();
		if (item == null) return;
		if (item.getItem() == null) return;
		
		if (mc.player.isUsingItem()) {
			for (int i = 0; i <= speed.getValue().intValue(); i++) {
				PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), event.getYaw(), event.getPitch(), event.isOnGround()));
			}

			if (item.getItem() instanceof ItemBow) {
				mc.playerController.onStoppedUsingItem(mc.player);
			}
		}
	};
}