package cc.unknown.module.impl.world;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Fast Place", description = "Te permite colocar o usar items más rapido", category = Category.WORLD)
public class FastPlace extends Module {

	private final BooleanValue blocks = new BooleanValue("Blocks", this, true);
	private final NumberValue blockDelay = new NumberValue("Block Delay", this, 50.0, 0.0, 300.0, 1, () -> !blocks.getValue());
	private final BooleanValue snowBall = new BooleanValue("SnowBall", this, false);
	private final NumberValue snowBallDelay = new NumberValue("SnowBall Delay", this, 50.0, 0.0, 300.0, 1, () -> !snowBall.getValue());
	private final BooleanValue egg = new BooleanValue("Egg", this, false);
	private final NumberValue eggDelay = new NumberValue("Egg Delay", this, 50.0, 0.0, 300.0, 1, () -> !egg.getValue());
	private final BooleanValue xpBottle = new BooleanValue("Exp Bottle", this, false);
	private final NumberValue xpBottleDelay = new NumberValue("Exp Bottle Delay", this, 50.0, 0.0, 300.0, 1, () -> !xpBottle.getValue());
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<NaturalPressEvent> onNatural = event -> {
		if (mc.currentScreen == null && Mouse.isButtonDown(1)) {
			ItemStack stack = mc.player.getCurrentEquippedItem();
			if (stack != null) {
				Item item = mc.player.getCurrentEquippedItem().getItem();
				if (item == null) return;
				if (mc.objectMouseOver.typeOfHit == null || mc.objectMouseOver == null) return;
				
				long delay;
				long random = MathUtil.nextRandom(-30L, 30L).longValue();
				
				if (blocks.getValue() && item instanceof ItemBlock && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					delay = blockDelay.getValue().longValue() == 0.0 ? 0L : blockDelay.getValue().longValue() + random;
					if (stopWatch.finished(delay)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				} else if (snowBall.getValue() && item instanceof ItemSnowball) {
					delay = snowBallDelay.getValue().longValue() == 0.0 ? 0L : snowBallDelay.getValue().longValue() + random;
					if (stopWatch.finished(delay)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				} else if (egg.getValue() && item instanceof ItemEgg) {
					delay = eggDelay.getValue().longValue() == 0.0 ? 0L : eggDelay.getValue().longValue() + random;
					if (stopWatch.finished(delay)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				} else if (xpBottle.getValue() && item instanceof ItemExpBottle) {
					delay = xpBottleDelay.getValue().longValue() == 0.0 ? 0L : xpBottleDelay.getValue().longValue() + random;
					if (stopWatch.finished(delay)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				}
			}
		}
	};
}