package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Auto Block", description = ">:3c", category = Category.GHOST)
public class AutoBlock extends Module {

	private final BoundsNumberValue duration = new BoundsNumberValue("Block Duration", this, 20, 100, 1, 500, 1);
	private final BoundsNumberValue distance = new BoundsNumberValue("Distance", this, 0, 3, 0, 6, 0.1);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	public boolean block;
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<PreMotionEvent> onPreUpdate = event -> {
		if (!isInGame())
			return;

		if (mc.currentScreen != null) return;

		if (InventoryUtil.isSword()) {
			if (block) {
				if ((stopWatch.hasFinished() || !Mouse.isButtonDown(0)) && duration.getValue().intValue() <= stopWatch.getElapsedTime()) {
					block = false;
				}
				return;
			}
	
			if (Mouse.isButtonDown(0) && (chance.getValue().intValue() == 100 || Math.random() <= chance.getValue().intValue() / 100)) {
				block = true;
				stopWatch.setMillis(duration.getSecondValue().intValue());
				stopWatch.reset();
	
				mc.playerController.sendUseItem(mc.player, mc.world, PlayerUtil.getItemStack());
			}
		}
	};
}