package cc.unknown.module.impl.move.spider;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.move.Spider;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.util.MathHelper;

public class VulcanSpider extends Mode<Spider> {
	public VulcanSpider(String name, Spider parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.isCollidedHorizontally) {
			if (mc.player.ticksExisted % 2 == 0) {
				event.setOnGround(true);
				mc.player.motionY = MoveUtil.jumpMotion();
			}

			final double yaw = MoveUtil.direction();
			event.setPosX(event.getPosX() - -MathHelper.sin((float) yaw) * 0.00000001);
			event.setPosZ(event.getPosZ() - MathHelper.cos((float) yaw) * 0.00000001);
		}
	};
}
