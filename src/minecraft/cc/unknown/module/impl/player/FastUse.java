package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "Fast Use", description = "Come m�s rapido", category = Category.PLAYER)
public class FastUse extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Vanilla"))
			.add(new SubMode("Universocraft"))
			.setDefault("Universocraft");
	
	private final NumberValue speed = new NumberValue("Packets", this, 10, 10, 100, 10, () -> !mode.is("Vanilla"));
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.isUsingItem() && PlayerUtil.getItemStack().getItem() instanceof ItemFood) {
			switch (mode.getValue().getName()) {
			case "Vanilla":
				for (int i = 0; i <= speed.getValue().intValue(); i++) {
					PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), event.getYaw(), event.getPitch(), event.isOnGround()));
				}
				break;
				
			case "Universocraft":
                int foodUseDuration = mc.player.getItemInUseDuration();
                int halfDuration = 29;
                if (foodUseDuration >= halfDuration) {
                	mc.player.stopUsingItem();
                }
				break;
			}
		}
	};
}