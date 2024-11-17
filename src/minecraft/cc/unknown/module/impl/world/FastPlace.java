package cc.unknown.module.impl.world;

import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Fast Place", description = "Makes the place delay shorter", category = Category.WORLD)
public class FastPlace extends Module {

	private final BooleanValue blocks = new BooleanValue("Blocks", this, true);
	private final NumberValue blockDelay = new NumberValue("Block Delay", this, 0, 0, 3, 1, () -> !blocks.getValue());
	private final BooleanValue projectiles = new BooleanValue("Egg/SnowBall", this, true);
	private final NumberValue projectileDelay = new NumberValue("Egg/SnowBall Delay", this, 0, 0, 3, 1, () -> !projectiles.getValue());

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		ItemStack stack = getComponent(Slot.class).getItemStack();		
		if (stack != null) {
			Item item = getComponent(Slot.class).getItemStack().getItem();
			if (blocks.getValue() && item instanceof ItemBlock) {
				mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, blockDelay.getValue().intValue());
			} else if (projectiles.getValue() && item instanceof ItemSnowball || item instanceof ItemEgg) {
				mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, projectileDelay.getValue().intValue());
			}
		}
	};
}