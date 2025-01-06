package cc.unknown.module.impl.player;

import java.io.IOException;
import java.util.ArrayList;
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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
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
							itemPos.add(i);
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

	private int calculateDistance(int slot1, int slot2) {
		int rowLength = 9;
		int row1 = slot1 / rowLength;
		int col1 = slot1 % rowLength;
		int row2 = slot2 / rowLength;
		int col2 = slot2 % rowLength;
		int distance = (int) Math.sqrt(Math.pow(row1 - row2, 2) + Math.pow(col1 - col2, 2));
		return distance;
	}

	private long getRandomDelay() {
		long min = (long) speed.getValue().longValue();
		long max = (long) speed.getSecondValue().longValue();
		return min + MathUtil.getSafeRandom(min, max);
	}

	private long getRandomStartDelay() {
		long min = startdelay.getValue().longValue();
		long max = startdelay.getSecondValue().longValue();
		return min + MathUtil.getSafeRandom(min, max);
	}

	public boolean isBestChestItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemSpade || itemStack.getItem() instanceof ItemFishingRod) {
			ItemStack bestItem = null;
			GuiChest chest = (GuiChest) mc.currentScreen;

			for (int i = 0; i < chest.inventorySlots.inventorySlots.size() - 36; ++i) {
				ItemStack chestItem = chest.inventorySlots.getSlot(i).getStack();
				if (chestItem != null) {
					if (itemStack.getItem() instanceof ItemSword && chestItem.getItem() instanceof ItemSword) {
						if (getDamageSword(itemStack) < getDamageSword(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemBow && chestItem.getItem() instanceof ItemBow) {
						if (getDamageBow(itemStack) < getDamageBow(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemArmor && chestItem.getItem() instanceof ItemArmor) {
						if (((ItemArmor) itemStack.getItem()).armorType == ((ItemArmor) chestItem.getItem()).armorType && getDamageReduceAmount(itemStack) < getDamageReduceAmount(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemFishingRod && chestItem.getItem() instanceof ItemFishingRod) {
						if (getBestRod(itemStack) < getBestRod(chestItem)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemAxe && chestItem.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe && chestItem.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemSpade) {
						if (getToolSpeed(itemStack) < getToolSpeed(chestItem)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public boolean isBestItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemSpade || itemStack.getItem() instanceof ItemFishingRod) {
			for (int i = 0; i < mc.player.inventoryContainer.inventorySlots.size(); ++i) {
				ItemStack inventoryStack = mc.player.inventoryContainer.getSlot(i).getStack();
				if (inventoryStack != null) {
					if (itemStack.getItem() instanceof ItemSword && inventoryStack.getItem() instanceof ItemSword) {
						if (getDamageSword(itemStack) <= getDamageSword(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemBow && inventoryStack.getItem() instanceof ItemBow) {
						if (getDamageBow(itemStack) <= getDamageBow(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemArmor && inventoryStack.getItem() instanceof ItemArmor) {
						if (((ItemArmor) itemStack.getItem()).armorType == ((ItemArmor) inventoryStack.getItem()).armorType && getDamageReduceAmount(itemStack) <= getDamageReduceAmount(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemFishingRod && inventoryStack.getItem() instanceof ItemFishingRod) {
						if (getBestRod(itemStack) <= getBestRod(inventoryStack)) {
							return false;
						}
					} else if (itemStack.getItem() instanceof ItemPickaxe && inventoryStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemAxe && inventoryStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemSpade && inventoryStack.getItem() instanceof ItemSpade && getToolSpeed(itemStack) <= getToolSpeed(inventoryStack)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private double getDamageSword(ItemStack itemStack) {
		double damage = 0.0;
		if (itemStack.getItem() instanceof ItemSword) {
			damage += (double) (((ItemSword) itemStack.getItem()).getMaxDamage() + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F);
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damage;
	}

	private double getDamageBow(ItemStack itemStack) {
		double damage = 0.0;
		if (itemStack.getItem() instanceof ItemBow) {
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) / 8.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) / 8.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damage;
	}

	private double getToolSpeed(ItemStack itemStack) {
		double damage = 0.0;
		if (itemStack.getItem() instanceof ItemTool) {
			if (itemStack.getItem() instanceof ItemAxe) {
				damage += (double) (itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.wood, MapColor.woodColor)) + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
			} else if (itemStack.getItem() instanceof ItemPickaxe) {
				damage += (double) (itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.rock, MapColor.stoneColor)) + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
			} else if (itemStack.getItem() instanceof ItemSpade) {
				damage += (double) (itemStack.getItem().getStrVsBlock(itemStack, new Block(Material.sand, MapColor.sandColor)) + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
			}

			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damage;
	}

	private double getDamageReduceAmount(ItemStack itemStack) {
		double damageReduceAmount = 0.0;
		if (itemStack.getItem() instanceof ItemArmor) {
			damageReduceAmount += (double) ((float) ((ItemArmor) itemStack.getItem()).damageReduceAmount
					+ (float) (6 + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack)
							* EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack))
							/ 3.0F);
			damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId,
					itemStack) / 11.0;
			damageReduceAmount += (double) EnchantmentHelper
					.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) / 11.0;
			damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId,
					itemStack) / 11.0;
			damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
					itemStack) / 11.0;
			damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack)
					/ 11.0;
			damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId,
					itemStack) / 11.0;
			if (((ItemArmor) itemStack.getItem()).armorType == 0
					&& ((ItemArmor) itemStack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD) {
				damageReduceAmount -= 0.01;
			}

			damageReduceAmount -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damageReduceAmount;
	}

	private double getBestRod(ItemStack itemStack) {
		double damage = 0.0;
		if (itemStack.getItem() instanceof ItemFishingRod) {
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.lure.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
			damage += (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, itemStack)
					/ 33.0;
			damage -= (double) itemStack.getItemDamage() / 10000.0;
		}

		return damage;
	}
}