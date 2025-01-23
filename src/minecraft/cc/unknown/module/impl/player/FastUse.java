package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "Fast Use", description = "Come m�s rapido", category = Category.PLAYER)
public class FastUse extends Module {

	private final NumberValue speed = new NumberValue("Packets", this, 10, 10, 100, 10);

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.isUsingItem() && mc.player.getHeldItem().getItem() instanceof ItemFood) {
			for (int i = 0; i <= speed.getValue().intValue(); i++) {
				PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
			}
		}
	};
}