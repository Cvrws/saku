package cc.unknown.module.impl.player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@ModuleInfo(aliases = { "Inventory Manager", "Inv Manager",
		"Manager" }, description = "Organiza tu inventario", category = Category.PLAYER)
public class InventoryManager extends Module {
	
	private final BooleanValue throwGarbage = new BooleanValue("Throw Garbage", this, true);
	private final NumberValue startDelay = new NumberValue("Start Delay", this, 150, 0, 1000, 1);
	private final NumberValue speed = new NumberValue("Speed Delay", this, 150, 0, 1000, 1);
	private final BooleanValue sword = new BooleanValue("Sword", this, true);
	private final NumberValue swordSlot = new NumberValue("sword Slot", this, 1, 1, 9, 1, () -> !sword.getValue());
	private final BooleanValue axe = new BooleanValue("Axe", this, false);
	private final NumberValue axeSlot = new NumberValue("Axe Slot", this, 2, 1, 9, 1, () -> !axe.getValue());
	private final BooleanValue pickaxe = new BooleanValue("Pickaxe", this, false);
	private final NumberValue pickaxeSlot = new NumberValue("Pickaxe", this, 3, 1, 9, 1, () -> !pickaxe.getValue());
	private final BooleanValue bow = new BooleanValue("Bow", this, false);
	private final NumberValue bowSlot = new NumberValue("Bow Slot", this, 5, 1, 9, 1, () -> !bow.getValue());
	private final BooleanValue goldenApple = new BooleanValue("Golden Apple", this, false);
	private final NumberValue goldenAppleSlot = new NumberValue("Golden Apple Slot", this, 5, 1, 9, 1, () -> !goldenApple.getValue());
	private final BooleanValue blocks = new BooleanValue("Blocks", this, true);
	private final NumberValue blockSlot = new NumberValue("Block Slot", this, 6, 1, 9, 1, () -> !blocks.getValue());

	private final StopWatch startTimer = new StopWatch();
	private final StopWatch stopWatch = new StopWatch();
	private List<Item> garbageItems = Arrays.asList(Items.fishing_rod, Items.bucket, Items.apple, Items.bread, Items.cooked_beef, Items.egg, Items.wheat_seeds, Items.cooked_rabbit, Items.pumpkin_pie, Items.snowball, Items.lava_bucket, Items.cooked_chicken, Items.book, Items.flint_and_steel, Items.cake);
	private List<Block> garbageBlocks = Arrays.asList(Blocks.sand, Blocks.enchanting_table, Blocks.chest, Blocks.cactus, Blocks.tnt, Blocks.trapdoor);
	
	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (mc.currentScreen == null) {
			startTimer.reset();
		}

		if (!startTimer.elapse(startDelay.getValue().doubleValue(), false)) {
			return;
		}
		
	    if (mc.currentScreen instanceof GuiInventory) {
	        for (int i = 9; i < 45; ++i) {
	            if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) {
	                continue;
	            }

	            ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

	            if (!stopWatch.elapse(speed.getValue().doubleValue(), false)) {
	                continue;
	            }

	            if (checkAndMoveItem(i, is, InventoryUtil.bestSword(), swordSlot, ItemSword.class, sword)) break;
	            if (checkAndMoveItem(i, is, InventoryUtil.bestBow(), bowSlot, ItemBow.class, bow)) break;
	            if (checkAndMoveItem(i, is, InventoryUtil.getGoldenAppleSlotInventory(), goldenAppleSlot, ItemAppleGold.class, goldenApple)) break;
	            if (checkAndMoveItem(i, is, InventoryUtil.bestPick(), pickaxeSlot, ItemPickaxe.class, pickaxe, true)) break;
	            if (checkAndMoveItem(i, is, InventoryUtil.bestAxe(), axeSlot, ItemAxe.class, axe, true)) break;

	            if (blockSlot.getValue().intValue() != 0 && blocks.getValue()) {
	                if (is.getItem() instanceof ItemBlock && is == InventoryUtil.getBlockSlotInventory()) {
	                    ItemStack blockStack = mc.player.inventoryContainer.getSlot(35 + blockSlot.getValue().intValue()).getStack();
	                    if (blockStack == null || 
	                        !(blockStack.getItem() instanceof ItemBlock) || 
	                        InventoryUtil.blacklist.contains(((ItemBlock) blockStack.getItem()).getBlock())) {
	                        onClick(i, blockSlot.getValue().intValue());
	                        stopWatch.reset();
	                        if (speed.getValue().doubleValue() != 0) break;
	                    }
	                }
	            }

	            if (InventoryUtil.isBadStack(is, true, true) || garbageBlocks.contains(Block.getBlockFromItem(is.getItem())) || garbageItems.contains(is.getItem()) && throwGarbage.getValue()) {
	                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 1, 4, mc.player);
	                stopWatch.reset();
	                if (speed.getValue().doubleValue() != 0) break;
	            }
	        }
	    }
	};
	
	private void onClick(int i, int slot) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (slot - 1), 2, mc.player);
	}
    
    private boolean checkAndMoveItem(int slot, ItemStack stack, ItemStack bestItem, NumberValue targetSlot, Class<?> itemClass, BooleanValue toggle, boolean excludeBestWeapon) {
        if (targetSlot.getValue().intValue() != 0 && toggle.getValue()) {
            if (itemClass.isInstance(stack.getItem()) &&
                stack == bestItem &&
                mc.player.inventoryContainer.getInventory().contains(bestItem) &&
                mc.player.inventoryContainer.getSlot(35 + targetSlot.getValue().intValue()).getStack() != stack &&
                (!excludeBestWeapon || stack != InventoryUtil.bestWeapon())) {

                onClick(slot, targetSlot.getValue().intValue());
                stopWatch.reset();
                return speed.getValue().doubleValue() != 0;
            }
        }
        return false;
    }

    private boolean checkAndMoveItem(int slot, ItemStack stack, ItemStack bestItem, NumberValue targetSlot, Class<?> itemClass, BooleanValue toggle) {
        return checkAndMoveItem(slot, stack, bestItem, targetSlot, itemClass, toggle, false);
    }
}
