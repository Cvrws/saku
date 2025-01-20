package cc.unknown.module.impl.player;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.RenderContainerEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

@ModuleInfo(aliases = { "Chest Stealer",
		"Stealer" }, description = "Saca todos los objectos de los cofres.", category = Category.PLAYER)
public class ChestStealer extends Module {

	public BoundsNumberValue startdelay = new BoundsNumberValue("Start Delay", this, 200, 400, 0, 1000, 1);
	public BoundsNumberValue speed = new BoundsNumberValue("Delay", this, 100, 400, 30, 800, 1);
	public BooleanValue silent = new BooleanValue("Silent", this, false);
	public BooleanValue autoClose = new BooleanValue("AutoClose", this, true);

	private final StopWatch delayTime = new StopWatch();
	private final StopWatch startDelayTime = new StopWatch();

	public static int slot = 0;
	private int lastItemPos = Integer.MIN_VALUE;
	private List<Item> excludedItems = Arrays.asList(Items.fishing_rod, Items.bucket);
	private Random RANDOM = new Random();

	@EventLink
	public final Listener<RenderContainerEvent> onRenderContainer = event -> {
		if (silent.getValue())
			event.setCancelled();
	};

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		if (mc.currentScreen instanceof GuiChest) {
			if (startDelayTime.finished((long) (getRandomStartDelay()))) {
				ArrayList<Integer> itemPos = new ArrayList<>();
				GuiChest chest = (GuiChest) mc.currentScreen;

				for (int i = 0; i < chest.inventorySlots.inventorySlots.size() - 36; ++i) {
					ItemStack itemStack = chest.inventorySlots.getSlot(i).getStack();
					if (itemStack != null) {
						if (isBestChestItem(itemStack) && isBestItem(itemStack)) {
						    if (!excludedItems.contains(itemStack.getItem())) {
						        itemPos.add(i);
						    }
						}
					}
				}

				if (delayTime.elapse(getRandomDelay(), false)) {
					boolean b = false;
					for (Integer integer : itemPos) {
						stealItem(integer);
						lastItemPos = integer;
						b = true;
						
						if (speed.getValue().doubleValue() != 0.0) {
							break;
						}
					}

					if (!b && autoClose.getValue()) {
						startDelayTime.reset();
						mc.player.closeScreen();
					}
				} else if (lastItemPos != Integer.MIN_VALUE) {
					mc.playerController.windowClick(chest.inventorySlots.windowId, lastItemPos, 0, 1, mc.player);
					mc.mouseHelper.mouseXYChange();
					Mouse.setCursorPosition(Display.getWidth() / 3, Display.getHeight() / 2);
					Mouse.setGrabbed(true);
				}
			}
		} else {
			startDelayTime.reset();
			lastItemPos = Integer.MIN_VALUE;
		}

	};
	
	private void stealItem(int slot) {
		this.slot = slot;
		GuiChest chest = (GuiChest) mc.currentScreen;
		Slot slot1 = chest.inventorySlots.getSlot(slot);

		try {
			chest.forceShift = true;
			chest.mouseClickMove(slot1.xDisplayPosition + 2 + chest.guiLeft, slot1.yDisplayPosition + 2 + chest.guiTop, 0, 0);
			chest.allowUserInput = true;
			chest.handleInput();
			chest.handleMouseInput();
		} catch (IOException var5) {
			var5.printStackTrace();
		}
		
		delayTime.reset();
	}

	private long getRandomDelay() {
		long min = speed.getValue().longValue();
		long max = speed.getSecondValue().longValue();
		return min + MathUtil.getSafeRandom(min, max);
	}

	private long getRandomStartDelay() {
		long min = startdelay.getValue().longValue();
		long max = startdelay.getSecondValue().longValue();
		return min + MathUtil.getSafeRandom(min, max);
	}

	public boolean isBestChestItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe) {
			ItemStack bestItem = null;
			GuiChest chest = (GuiChest) mc.currentScreen;

			for (int i = 0; i < chest.inventorySlots.inventorySlots.size() - 36; ++i) {
				ItemStack chestItem = chest.inventorySlots.getSlot(i).getStack();
				if (chestItem != null) {
					if (itemStack.getItem() instanceof ItemSword && chestItem.getItem() instanceof ItemSword) {
						if (getDamage(itemStack) < getDamage(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemBow && chestItem.getItem() instanceof ItemBow) {
						if (getDamage(itemStack) < getDamage(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemArmor && chestItem.getItem() instanceof ItemArmor) {
						if (((ItemArmor) itemStack.getItem()).armorType == ((ItemArmor) chestItem.getItem()).armorType && getDamage(itemStack) < getDamage(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemAxe && chestItem.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe && chestItem.getItem() instanceof ItemPickaxe) {
						if (getDamage(itemStack) < getDamage(chestItem)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public boolean isBestItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe) {
			for (int i = 0; i < mc.player.inventoryContainer.inventorySlots.size(); ++i) {
				ItemStack inventoryStack = mc.player.inventoryContainer.getSlot(i).getStack();
				if (inventoryStack != null) {
					if (itemStack.getItem() instanceof ItemSword && inventoryStack.getItem() instanceof ItemSword) {
						if (getDamage(itemStack) <= getDamage(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemBow && inventoryStack.getItem() instanceof ItemBow) {
						if (getDamage(itemStack) <= getDamage(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemArmor && inventoryStack.getItem() instanceof ItemArmor) {
						if (((ItemArmor) itemStack.getItem()).armorType == ((ItemArmor) inventoryStack.getItem()).armorType && getDamage(itemStack) <= getDamage(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemPickaxe && inventoryStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemAxe && inventoryStack.getItem() instanceof ItemAxe) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private double getDamage(ItemStack itemStack) {
		double damage = 0.0;
		if (itemStack.getItem() instanceof ItemSword) {
			damage += (double) (((ItemSword) itemStack.getItem()).getMaxDamage() + (float) getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F);
			damage += (double) getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.knockback.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}
		
		if (itemStack.getItem() instanceof ItemBow) {
			damage += (double) getEnchantmentLevel(Enchantment.flame.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.punch.effectId, itemStack) / 8.0;
			damage += (double) getEnchantmentLevel(Enchantment.power.effectId, itemStack) / 8.0;
			damage += (double) getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}
		
		if (itemStack.getItem() instanceof ItemTool) {
			if (itemStack.getItem() instanceof ItemAxe) {
				damage += (double) (itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.wood, MapColor.woodColor)) + (float) getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
			} else if (itemStack.getItem() instanceof ItemPickaxe) {
				damage += (double) (itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.rock, MapColor.stoneColor)) + (float) getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
			}

			damage += (double) getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}
		
		if (itemStack.getItem() instanceof ItemArmor) {
			damage += (double) ((float) ((ItemArmor) itemStack.getItem()).damageReduceAmount + (float) (6 + getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * getEnchantmentLevel(Enchantment.protection.effectId, itemStack)) / 3.0F);
			damage += (double) getEnchantmentLevel(Enchantment.blastProtection.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.thorns.effectId, itemStack) / 11.0;
			damage += (double) getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack) / 11.0;
			if (((ItemArmor) itemStack.getItem()).armorType == 0 && ((ItemArmor) itemStack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD) {
				damage -= 0.01;
			}

			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damage;
	}
}