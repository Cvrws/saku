package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
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
import net.minecraft.util.DamageSource;

@ModuleInfo(aliases = { "Inventory Manager", "Inv Manager",
		"Manager" }, description = "Organiza tu inventario", category = Category.PLAYER)
public class InventoryManager extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Spoof"))
			.add(new SubMode("Open Inv"))
			.setDefault("Open Inv");
	
	private final BooleanValue throwGarbage = new BooleanValue("Throw Garbage", this, true);
	private final NumberValue startDelay = new NumberValue("Start Delay", this, 150, 0, 1000, 1);
	private final NumberValue speed = new NumberValue("Speed Delay", this, 150, 0, 1000, 1);
	private final BooleanValue sword = new BooleanValue("Sword", this, true);
	private final NumberValue swordSlot = new NumberValue("sword Slot", this, 1, 1, 9, 1, () -> !sword.getValue());
	private final BooleanValue axe = new BooleanValue("Axe", this, false);
	private final NumberValue axeSlot = new NumberValue("Axe Slot", this, 2, 1, 9, 1, () -> !axe.getValue());
	private final BooleanValue pickaxe = new BooleanValue("Pickaxe", this, false);
	private final NumberValue pickaxeSlot = new NumberValue("Pickaxe", this, 3, 1, 9, 1, () -> !pickaxe.getValue());
	private final BooleanValue shovel = new BooleanValue("Shovel", this, false);
	private final NumberValue shovelSlot = new NumberValue("Shovel Slot", this, 4, 1, 9, 1, () -> !shovel.getValue());
	private final BooleanValue bow = new BooleanValue("Bow", this, false);
	private final NumberValue bowSlot = new NumberValue("Bow Slot", this, 5, 1, 9, 1, () -> !bow.getValue());
	private final BooleanValue goldenApple = new BooleanValue("Golden Apple", this, false);
	private final NumberValue goldenAppleSlot = new NumberValue("Golden Apple Slot", this, 5, 1, 9, 1, () -> !goldenApple.getValue());
	private final BooleanValue blocks = new BooleanValue("Blocks", this, true);
	private final NumberValue blockSlot = new NumberValue("Block Slot", this, 6, 1, 9, 1, () -> !blocks.getValue());
	private final BooleanValue projectiles = new BooleanValue("Projectiles", this, true);
	private final NumberValue projectileSlot = new NumberValue("Projectile Slot", this, 7, 1, 9, 1, () -> !projectiles.getValue());
	private final BooleanValue waterBucket = new BooleanValue("Water Bucket", this, true);
	private final NumberValue waterBucketSlot = new NumberValue("Water Bucket Slot", this, 8, 1, 9, 1, () -> !waterBucket.getValue());
	private final KeyBinding[] moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak };

	private final StopWatch startTimer = new StopWatch();
	private final StopWatch stopWatch = new StopWatch();
	
	private int[] bestArmorDamageReducment, bestArmorSlot;

	@Override
	public void onDisable() {
		InventoryUtil.closeInv(mode.getValue().getName());
	}

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (mode.is("Open Inv")) {
			if (mc.currentScreen == null) {
				startTimer.reset();
			}

			if (!startTimer.elapse(startDelay.getValue().doubleValue(), false)) {
				return;
			}
		}

		if (mode.is("Spoof") && (mc.currentScreen != null)) {
			InventoryUtil.closeInv(mode.getValue().getName());
		} else if (!mode.is("Open Inv") || mc.currentScreen instanceof GuiInventory) {
			for (int i = 9; i < 45; ++i) {
				if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
					if (stopWatch.elapse(speed.getValue().doubleValue(), false)) {
						if (swordSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemSword && is == InventoryUtil.bestSword() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestSword()) && mc.player.inventoryContainer.getSlot((int) (35 + swordSlot.getValue().intValue())).getStack() != is && sword.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (swordSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							stopWatch.reset();
							
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						
						} else if (bowSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemBow && is == InventoryUtil.bestBow() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestBow()) && mc.player.inventoryContainer.getSlot((int) (35 + bowSlot.getValue().intValue())).getStack() != is && bow.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (bowSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (goldenAppleSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemAppleGold && is == InventoryUtil.getGoldenAppleSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getGoldenAppleSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + goldenAppleSlot.getValue().intValue())).getStack() != is && goldenApple.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (bowSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (pickaxeSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemPickaxe && is == InventoryUtil.bestPick() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestPick()) && mc.player.inventoryContainer.getSlot((int) (35 + pickaxeSlot.getValue().intValue())).getStack() != is && pickaxe.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (pickaxeSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (axeSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemAxe && is == InventoryUtil.bestAxe() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestAxe()) && mc.player.inventoryContainer.getSlot((int) (35 + axeSlot.getValue().intValue())).getStack() != is && axe.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (axeSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (shovelSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemSpade && is == InventoryUtil.bestShovel() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestShovel()) && mc.player.inventoryContainer.getSlot((int) (35 + shovelSlot.getValue().intValue())).getStack() != is && shovel.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (shovelSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (blockSlot.getValue().intValue() != 0 && is.getItem() instanceof ItemBlock && is == InventoryUtil.getBlockSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getBlockSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack() != is && blocks.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack() != null && mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack().getItem() instanceof ItemBlock && !InventoryUtil.invalidBlocks.contains(((ItemBlock) mc.player.inventoryContainer.getSlot((int) (35 + blockSlot.getValue().intValue())).getStack().getItem()).getBlock())) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (blockSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (projectileSlot.getValue().intValue() != 0 && is == InventoryUtil.getProjectileSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getProjectileSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + projectileSlot.getValue().intValue())).getStack() != is && projectiles.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35 + projectileSlot.getValue().intValue())).getStack() != null && (mc.player.inventoryContainer.getSlot((int) (35 + projectileSlot.getValue().intValue())).getStack().getItem() instanceof ItemSnowball || mc.player.inventoryContainer.getSlot((int) (35 + projectileSlot.getValue().intValue())).getStack().getItem() instanceof ItemEgg || mc.player.inventoryContainer.getSlot((int) (35 + projectileSlot.getValue().intValue())).getStack().getItem() instanceof ItemFishingRod)) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (projectileSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (waterBucketSlot.getValue().intValue() != 0 && is.getItem() == Items.water_bucket && is == InventoryUtil.getBucketSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getBucketSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35 + shovelSlot.getValue().intValue())).getStack() != is && waterBucket.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35 + waterBucketSlot.getValue().intValue())).getStack() != null && mc.player.inventoryContainer.getSlot((int) (35 + waterBucketSlot.getValue().intValue())).getStack().getItem() == Items.water_bucket) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (waterBucketSlot.getValue().intValue() - 1), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						} else if (InventoryUtil.isBadStack(is, true, true) && throwGarbage.getValue()) {
	                        InventoryUtil.openInv(mode.getValue().getName());
	                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 1, 4, mc.player);
	                        InventoryUtil.closeInv(mode.getValue().getName());

							stopWatch.reset();
							if (speed.getValue().doubleValue() != 0) {
								break;
							}
						}

						if (stopWatch.elapse(55, false)) {
							InventoryUtil.closeInv(mode.getValue().getName());
						}
					}
				}
			}
		}
	};
	
    private int armorReduction(final ItemStack stack) {
        final ItemArmor armor = (ItemArmor) stack.getItem();
        return armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{stack}, DamageSource.generic);
    }
}
