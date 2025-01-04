package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.DamageSource;

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
				if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
					if (stopWatch.elapse(speed.getValue().doubleValue(), false)) {
						if (swordSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemSword && is == InventoryUtil.bestSword() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestSword()) && mc.player.inventoryContainer.getSlot((int) (35 + swordSlot.getValue().intValue())).getStack() != is && sword.getValue()) {
							onClick(i, swordSlot.getValue().intValue());
							
							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						
						} else if (bowSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemBow && is == InventoryUtil.bestBow() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestBow()) && mc.player.inventoryContainer.getSlot((int) (35 + bowSlot.getValue().intValue())).getStack() != is && bow.getValue()) {
							onClick(i, bowSlot.getValue().intValue());
							
							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (goldenAppleSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemAppleGold && is == InventoryUtil.getGoldenAppleSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getGoldenAppleSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + goldenAppleSlot.getValue().intValue())).getStack() != is && goldenApple.getValue()) {
							onClick(i, goldenAppleSlot.getValue().intValue());
							
							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (pickaxeSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemPickaxe && is == InventoryUtil.bestPick() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestPick()) && mc.player.inventoryContainer.getSlot((int) (35 + pickaxeSlot.getValue().intValue())).getStack() != is && pickaxe.getValue()) {
							onClick(i, pickaxeSlot.getValue().intValue());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (axeSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemAxe && is == InventoryUtil.bestAxe() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestAxe()) && mc.player.inventoryContainer.getSlot((int) (35 + axeSlot.getValue().intValue())).getStack() != is && axe.getValue()) {
							onClick(i, axeSlot.getValue().intValue());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (blockSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemBlock && is == InventoryUtil.getBlockSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getBlockSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack() != is && blocks.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack() != null && mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack().getItem() instanceof ItemBlock && !InventoryUtil.invalidBlocks.contains(((ItemBlock) mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack().getItem()).getBlock())) {
								return;
							}
							
							onClick(i, blockSlot.getValue().intValue());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (InventoryUtil.isBadStack(is, true, true) && throwGarbage.getValue()) {
	                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 1, 4, mc.player);

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						}
					}
				}
			}
		}
	};
	
	private void onClick(int i, int slot) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (slot - 1), 2, mc.player);
		
	}
	
    private int armorReduction(final ItemStack stack) {
        final ItemArmor armor = (ItemArmor) stack.getItem();
        return armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{stack}, DamageSource.generic);
    }
}
