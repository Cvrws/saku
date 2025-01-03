package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.BlinkUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Block Hit", description = "Block hitea automáticamente", category = Category.GHOST)
public class BlockHit extends Module {

	private final BoundsNumberValue duration = new BoundsNumberValue("Block Duration", this, 20, 100, 1, 500, 1);
	private final BoundsNumberValue distance = new BoundsNumberValue("Distance", this, 0, 3, 0, 6, 0.1);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);
	private final BooleanValue blink = new BooleanValue("Blink", this, true);

	private boolean block;
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame())
			return;

		if (block) {
			if ((stopWatch.hasFinished() || !Mouse.isButtonDown(0)) && duration.getValue().intValue() <= stopWatch.getElapsedTime()) {
				block = false;
				mc.gameSettings.keyBindUseItem.pressed = false;
				
				if (blink.getValue()) {
					block = false;
					BlinkUtil.disable();
				}
			}
			return;
		}

		if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null
				&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) >= distance.getValue().floatValue()
				&& mc.objectMouseOver.entityHit != null
				&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) <= distance.getSecondValue().floatValue()
				&& (chance.getValue().intValue() == 100 || Math.random() <= chance.getValue().intValue() / 100)) {
			block = true;
			stopWatch.setMillis(duration.getSecondValue().intValue());
			stopWatch.reset();
			mc.gameSettings.keyBindUseItem.pressed = true;
			
			if (blink.getValue()) {
				block = true;
				BlinkUtil.enable();
			}
		}
	};
}