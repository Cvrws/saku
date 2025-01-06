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
	private final BooleanValue projectiles = new BooleanValue("Egg/SnowBall", this, false);
	private final NumberValue projectileDelay = new NumberValue("Egg/SnowBall Delay", this, 50.0, 0.0, 300.0, 1, () -> !projectiles.getValue());
	private final BooleanValue xpBottle = new BooleanValue("Exp Bottle", this, false);
	private final NumberValue xpBottleDelay = new NumberValue("Exp Bottle Delay", this, 50.0, 0.0, 300.0, 1, () -> !xpBottle.getValue());
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<NaturalPressEvent> onNatural = event -> {
		if (mc.currentScreen == null && Mouse.isButtonDown(1)) {
			ItemStack stack = mc.player.getCurrentEquippedItem();
			if (stack != null) {
				Item item = mc.player.getCurrentEquippedItem().getItem();
				if (mc.objectMouseOver.typeOfHit == null || mc.objectMouseOver == null) return;
				if (blocks.getValue() && item instanceof ItemBlock && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					long random = blockDelay.getValue().longValue() == 0.0 ? 0L : blockDelay.getValue().longValue() + MathUtil.nextLong(-30L, 30L);
					if (stopWatch.finished(random)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				} else if (projectiles.getValue() && item instanceof ItemSnowball || item instanceof ItemEgg) {
					long random = projectileDelay.getValue().longValue() == 0.0 ? 0L : projectileDelay.getValue().longValue() + MathUtil.nextLong(-30L, 30L);
					if (stopWatch.finished(random)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				} else if (xpBottle.getValue() && item instanceof ItemExpBottle) {
					long random = xpBottleDelay.getValue().longValue() == 0.0 ? 0L : xpBottleDelay.getValue().longValue() + MathUtil.nextLong(-30L, 30L);
					if (stopWatch.finished(random)) {
						mc.rightClickMouse();
						stopWatch.reset();
					}
					event.setCancelled();
				}
			}
		}
	};
}