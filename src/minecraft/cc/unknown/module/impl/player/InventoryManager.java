package cc.unknown.module.impl.player;

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
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
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

@ModuleInfo(aliases = { "Inventory Manager", "Inv Manager",
		"Manager" }, description = "Sorts your inventory for you and throws out useless items", category = Category.PLAYER)
public class InventoryManager extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Spoof"))
			.add(new SubMode("Open Inv"))
			.setDefault("Open Inv");
	
	private final BooleanValue throwGarbage = new BooleanValue("Throw Garbage", this, true);
	private final NumberValue startDelay = new NumberValue("Start Delay", this, 150.0D, 0.0D, 1000.0D, 1.0D);
	private final NumberValue speed = new NumberValue("Speed", this, 150.0D, 0.0D, 1000.0D, 1.0D);
	private final BooleanValue sword = new BooleanValue("Sword", this, true);
	private final NumberValue swordSlot = new NumberValue("sword Slot", this, 1.0D, 1.0D, 9.0D, 1.0D, () -> !sword.getValue());
	private final BooleanValue axe = new BooleanValue("Axe", this, false);
	private final NumberValue axeSlot = new NumberValue("Axe Slot", this, 2.0D, 1.0D, 9.0D, 1.0D, () -> !axe.getValue());
	private final BooleanValue pickaxe = new BooleanValue("Pickaxe", this, false);
	private final NumberValue pickaxeSlot = new NumberValue("Pickaxe", this, 3.0D, 1.0D, 9.0D, 1.0D, () -> !pickaxe.getValue());
	private final BooleanValue shovel = new BooleanValue("Shovel", this, false);
	private final NumberValue shovelSlot = new NumberValue("Shovel Slot", this, 4.0D, 1.0D, 9.0D, 1.0D, () -> !shovel.getValue());
	private final BooleanValue bow = new BooleanValue("Bow", this, false);
	private final NumberValue bowSlot = new NumberValue("Bow Slot", this, 5.0D, 1.0D, 9.0D, 1.0D, () -> !bow.getValue());
	private final BooleanValue goldenApple = new BooleanValue("Bow", this, false);
	private final NumberValue goldenAppleSlot = new NumberValue("Bow Slot", this, 5.0D, 1.0D, 9.0D, 1.0D, () -> !goldenApple.getValue());
	private final BooleanValue blocks = new BooleanValue("Blocks", this, true);
	private final NumberValue blockSlot = new NumberValue("Block Slot", this, 6.0D, 1.0D, 9.0D, 1.0D, () -> !blocks.getValue());
	private final BooleanValue projectiles = new BooleanValue("Projectiles", this, true);
	private final NumberValue projectileSlot = new NumberValue("Projectile Slot", this, 7.0D, 1.0D, 9.0D, 1.0D, () -> !projectiles.getValue());
	private final BooleanValue waterBucket = new BooleanValue("Water Bucket", this, true);
	private final NumberValue waterBucketSlot = new NumberValue("Water Bucket Slot", this, 8.0D, 1.0D, 9.0D, 1.0D, () -> !waterBucket.getValue());
	private final KeyBinding[] moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak };

	private final StopWatch startTimer = new StopWatch();
	private final StopWatch timer = new StopWatch();

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
					if (InventoryUtil.timer.elapse(speed.getValue().doubleValue(), false)) {
						if (swordSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemSword && is == InventoryUtil.bestSword() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestSword()) && mc.player.inventoryContainer.getSlot((int) (35.0D + swordSlot.getValue().doubleValue())).getStack() != is && sword.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (swordSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							InventoryUtil.timer.reset();
							
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						
						} else if (bowSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemBow && is == InventoryUtil.bestBow() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestBow()) && mc.player.inventoryContainer.getSlot((int) (35.0D + bowSlot.getValue().doubleValue())).getStack() != is && bow.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (bowSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (goldenAppleSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemAppleGold && is == InventoryUtil.getGoldenAppleSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getGoldenAppleSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35.0D + goldenAppleSlot.getValue().doubleValue())).getStack() != is && goldenApple.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (bowSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							
							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (pickaxeSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemPickaxe && is == InventoryUtil.bestPick() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestPick()) && mc.player.inventoryContainer.getSlot((int) (35.0D + pickaxeSlot.getValue().doubleValue())).getStack() != is && pickaxe.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (pickaxeSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (axeSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemAxe && is == InventoryUtil.bestAxe() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestAxe()) && mc.player.inventoryContainer.getSlot((int) (35.0D + axeSlot.getValue().doubleValue())).getStack() != is && axe.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (axeSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (shovelSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemSpade && is == InventoryUtil.bestShovel() && is != InventoryUtil.bestWeapon() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.bestShovel()) && mc.player.inventoryContainer.getSlot((int) (35.0D + shovelSlot.getValue().doubleValue())).getStack() != is && shovel.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (shovelSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (blockSlot.getValue().doubleValue() != 0.0D && is.getItem() instanceof ItemBlock && is == InventoryUtil.getBlockSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getBlockSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35.0D + blockSlot.getValue().doubleValue())).getStack() != is && blocks.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35.0D + blockSlot.getValue().doubleValue())).getStack() != null && mc.player.inventoryContainer.getSlot((int) (35.0D + blockSlot.getValue().doubleValue())).getStack().getItem() instanceof ItemBlock && !InventoryUtil.invalidBlocks.contains(((ItemBlock) mc.player.inventoryContainer.getSlot((int) (35.0D + blockSlot.getValue().doubleValue())).getStack().getItem()).getBlock())) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (blockSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (projectileSlot.getValue().doubleValue() != 0.0D && is == InventoryUtil.getProjectileSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getProjectileSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35.0D + projectileSlot.getValue().doubleValue())).getStack() != is && projectiles.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35.0D + projectileSlot.getValue().doubleValue())).getStack() != null && (mc.player.inventoryContainer.getSlot((int) (35.0D + projectileSlot.getValue().doubleValue())).getStack().getItem() instanceof ItemSnowball || mc.player.inventoryContainer.getSlot((int) (35.0D + projectileSlot.getValue().doubleValue())).getStack().getItem() instanceof ItemEgg || mc.player.inventoryContainer.getSlot((int) (35.0D + projectileSlot.getValue().doubleValue())).getStack().getItem() instanceof ItemFishingRod)) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (projectileSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (waterBucketSlot.getValue().doubleValue() != 0.0D && is.getItem() == Items.water_bucket && is == InventoryUtil.getBucketSlotInventory() && mc.player.inventoryContainer.getInventory().contains(InventoryUtil.getBucketSlotInventory()) && mc.player.inventoryContainer.getSlot((int) (35.0D + shovelSlot.getValue().doubleValue())).getStack() != is && waterBucket.getValue()) {
							if (mc.player.inventoryContainer.getSlot((int) (35.0D + waterBucketSlot.getValue().doubleValue())).getStack() != null && mc.player.inventoryContainer.getSlot((int) (35.0D + waterBucketSlot.getValue().doubleValue())).getStack().getItem() == Items.water_bucket) {
								return;
							}

							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, (int) (waterBucketSlot.getValue().doubleValue() - 1.0D), 2, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						} else if (InventoryUtil.isBadStack(is, true, true) && throwGarbage.getValue()) {
							InventoryUtil.openInv(mode.getValue().getName());
							mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 1, 4, mc.player);
							InventoryUtil.closeInv(mode.getValue().getName());
							

							InventoryUtil.timer.reset();
							if (speed.getValue().doubleValue() != 0.0D) {
								break;
							}
						}

						if (InventoryUtil.timer.elapse(55.0D, false)) {
							InventoryUtil.closeInv(mode.getValue().getName());
						}
					}
				}
			}
		}
	};
}
