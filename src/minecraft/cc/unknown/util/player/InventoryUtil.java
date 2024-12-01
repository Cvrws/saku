package cc.unknown.util.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

@UtilityClass
public class InventoryUtil implements Accessor {
	public boolean isInventoryOpen;
	public StopWatch timer = new StopWatch();
	public List<Block> invalidBlocks;
	KeyBinding[] moveKeys;

	{
		invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.ladder,
				Blocks.web, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.air, Blocks.water, Blocks.flowing_water,
				Blocks.lava, Blocks.ladder, Blocks.soul_sand, Blocks.ice, Blocks.packed_ice, Blocks.sand,
				Blocks.flowing_lava, Blocks.snow_layer, Blocks.chest, Blocks.ender_chest, Blocks.torch, Blocks.anvil,
				Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.wooden_pressure_plate,
				Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
				Blocks.stone_button, Blocks.tnt, Blocks.wooden_button, Blocks.lever, Blocks.crafting_table,
				Blocks.furnace, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.brown_mushroom,
				Blocks.red_mushroom, Blocks.gold_block, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot);
		moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
				mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
				mc.gameSettings.keyBindSneak };
	}

	public int getBlockSlot(boolean hypixel) {
		int item = -1;
		int stacksize = 0;
		if (!hypixel && mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() != null
				&& mc.player.getHeldItem().getItem() instanceof ItemBlock
				&& !invalidBlocks.contains(((ItemBlock) mc.player.getHeldItem().getItem()).getBlock())) {
			return mc.player.inventory.currentItem;
		} else {
			for (int i = 36; i < 45; ++i) {
				if (mc.player.inventoryContainer.getSlot(i).getStack() != null
						&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock
						&& !invalidBlocks
								.contains(((ItemBlock) mc.player.inventoryContainer.getSlot(i).getStack().getItem())
										.getBlock())
						&& mc.player.inventoryContainer.getSlot(i).getStack().stackSize >= stacksize) {
					item = i - 36;
					stacksize = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
				}
			}

			return item;
		}
	}

	public ItemStack getBlockSlotInventory() {
	    ItemStack item = null;
	    int stacksize = 0;

	    if (mc.player != null && mc.player.getHeldItem() != null) {
	        ItemStack heldItem = mc.player.getHeldItem();
	        if (heldItem.getItem() instanceof ItemBlock) {
	            ItemBlock itemBlock = (ItemBlock) heldItem.getItem();
	            Block block = itemBlock.getBlock();
	            if (block != null && invalidBlocks != null && !invalidBlocks.contains(block)) {
	                return heldItem;
	            }
	        }
	    }

	    if (mc.player != null && mc.player.inventoryContainer != null) {
	        for (int i = 9; i < 45; ++i) {
	            Slot slot = mc.player.inventoryContainer.getSlot(i);
	            if (slot != null) {
	                ItemStack stack = slot.getStack();
	                if (stack != null && stack.getItem() instanceof ItemBlock) {
	                    ItemBlock itemBlock = (ItemBlock) stack.getItem();
	                    Block block = itemBlock.getBlock();
	                    if (block != null && invalidBlocks != null && !invalidBlocks.contains(block)) {
	                        if (stack.stackSize > stacksize) {
	                            item = stack;
	                            stacksize = stack.stackSize;
	                        }
	                    }
	                }
	            }
	        }
	    }

	    return item;
	}
	
	public ItemStack getGoldenAppleSlotInventory() {
	    ItemStack item = null;
	    int stacksize = 0;

	    if (mc.player != null && mc.player.getHeldItem() != null) {
	        ItemStack heldItem = mc.player.getHeldItem();
	        if (heldItem.getItem() instanceof ItemAppleGold) {
	            return heldItem;
	        }
	    }

	    if (mc.player != null && mc.player.inventoryContainer != null) {
	        for (int i = 9; i < 45; ++i) {
	            Slot slot = mc.player.inventoryContainer.getSlot(i);
	            if (slot != null) {
	                ItemStack stack = slot.getStack();
	                if (stack != null && stack.getItem() instanceof ItemAppleGold) {
	                    if (stack.stackSize > stacksize) {
	                        item = stack;
	                        stacksize = stack.stackSize;
	                    }
	                }
	            }
	        }
	    }

	    return item;
	}

	public int getCobwebSlot() {
		int item = -1;

		for (int i = 36; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock) {
				ItemBlock block = (ItemBlock) mc.player.inventoryContainer.getSlot(i).getStack().getItem();
				if (block.getBlock() == Blocks.web) {
					item = i - 36;
					int var4 = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
				}
			}
		}

		return item;
	}

	public int getBucketSlot() {
		int item = -1;

		for (int i = 36; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.water_bucket) {
				item = i - 36;
				int var3 = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
			}
		}

		return item;
	}

	public int getEmptyBucketSlot() {
		int item = -1;

		for (int i = 36; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.bucket) {
				item = i - 36;
				int var3 = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
			}
		}

		return item;
	}

	public ItemStack getBucketSlotInventory() {
		ItemStack item = null;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.water_bucket) {
				item = mc.player.inventoryContainer.getSlot(i).getStack();
				int var3 = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
			}
		}

		return item;
	}

	public int getProjectileSlot() {
		int item = -1;
		int stacksize = 0;

		for (int i = 36; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemSnowball
							|| mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemEgg
							|| mc.player.inventoryContainer.getSlot(i).getStack()
									.getItem() instanceof ItemFishingRod)
					&& mc.player.inventoryContainer.getSlot(i).getStack().stackSize >= stacksize) {
				item = i - 36;
				stacksize = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
			}
		}

		return item;
	}

	public ItemStack getProjectileSlotInventory() {
		ItemStack item = null;
		int stacksize = 0;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getStack() != null
					&& (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemSnowball
							|| mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemEgg
							|| mc.player.inventoryContainer.getSlot(i).getStack()
									.getItem() instanceof ItemFishingRod)
					&& mc.player.inventoryContainer.getSlot(i).getStack().stackSize >= stacksize) {
				item = mc.player.inventoryContainer.getSlot(i).getStack();
				stacksize = mc.player.inventoryContainer.getSlot(i).getStack().stackSize;
			}
		}

		return item;
	}

	public float getProtection(ItemStack stack) {
		float prot = 0.0F;
		if (stack.getItem() instanceof ItemArmor) {
			ItemArmor armor = (ItemArmor) stack.getItem();
			prot = (float) ((double) (prot + (float) armor.damageReduceAmount)
					+ (double) ((100 - armor.damageReduceAmount)
							* EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
			prot = (float) ((double) prot
					+ (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack)
							/ 100.0D);
			prot = (float) ((double) prot
					+ (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack)
							/ 100.0D);
			prot = (float) ((double) prot
					+ (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
			prot = (float) ((double) prot
					+ (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
			prot = (float) ((double) prot
					+ (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) / 100.0D);
		}

		return prot;
	}

	public boolean isBestArmor(ItemStack stack, int type) {
		float prot = getProtection(stack);
		String strType = "";
		if (type == 1) {
			strType = "helmet";
		} else if (type == 2) {
			strType = "chestplate";
		} else if (type == 3) {
			strType = "leggings";
		} else if (type == 4) {
			strType = "boots";
		}

		if (!stack.getUnlocalizedName().contains(strType)) {
			return false;
		} else {
			for (int i = 5; i < 45; ++i) {
				if (Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
					if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
						return false;
					}
				}
			}

			return true;
		}
	}

	public void drop(int slot) {
		Minecraft.getMinecraft().playerController.windowClick(
				Minecraft.getMinecraft().player.inventoryContainer.windowId, slot, 1, 4,
				Minecraft.getMinecraft().player);
	}

	public void shiftClick(int slot) {
		Minecraft.getMinecraft().playerController.windowClick(
				Minecraft.getMinecraft().player.inventoryContainer.windowId, slot, 0, 1,
				Minecraft.getMinecraft().player);
	}

	public boolean isBadStack(ItemStack is, boolean preferSword, boolean keepTools) {
		for (int type = 1; type < 5; ++type) {
			String strType = "";
			if (type == 1) {
				strType = "helmet";
			} else if (type == 2) {
				strType = "chestplate";
			} else if (type == 3) {
				strType = "leggings";
			} else if (type == 4) {
				strType = "boots";
			}

			if (is.getItem() instanceof ItemArmor && !isBestArmor(is, type)
					&& is.getUnlocalizedName().contains(strType)) {
				return true;
			}

			if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()
					&& isBestArmor(mc.player.inventoryContainer.getSlot(4 + type).getStack(), type)
					&& mc.player.inventoryContainer.getSlot(4 + type).getStack().getUnlocalizedName()
							.contains(strType)
					&& is.getUnlocalizedName().contains(strType)) {
				return true;
			}
		}

		if (is.getItem() instanceof ItemSword && is != bestWeapon() && !preferSword) {
			return true;
		} else if (is.getItem() instanceof ItemSword && is != bestSword() && preferSword) {
			return true;
		} else if (is.getItem() instanceof ItemBow && is != bestBow()) {
			return true;
		} else {
			if (keepTools) {
				if (is.getItem() instanceof ItemAxe && is != bestAxe() && (preferSword || is != bestWeapon())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && is != bestPick() && (preferSword || is != bestWeapon())) {
					return true;
				}

				if (is.getItem() instanceof ItemSpade && is != bestShovel()) {
					return true;
				}
			} else {
				if (is.getItem() instanceof ItemAxe && (preferSword || is != bestWeapon())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && (preferSword || is != bestWeapon())) {
					return true;
				}

				if (is.getItem() instanceof ItemSpade) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isBadStackStealer(ItemStack is, boolean preferSword, boolean keepTools) {
		for (int type = 1; type < 5; ++type) {
			String strType = "";
			if (type == 1) {
				strType = "helmet";
			} else if (type == 2) {
				strType = "chestplate";
			} else if (type == 3) {
				strType = "leggings";
			} else if (type == 4) {
				strType = "boots";
			}

			if (is.getItem() instanceof ItemArmor && !isBestArmor(is, type)
					&& is.getUnlocalizedName().contains(strType)) {
				return true;
			}

			if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()
					&& isBestArmor(mc.player.inventoryContainer.getSlot(4 + type).getStack(), type)
					&& mc.player.inventoryContainer.getSlot(4 + type).getStack().getUnlocalizedName()
							.contains(strType)
					&& is.getUnlocalizedName().contains(strType)) {
				return true;
			}
		}

		if (is.getItem() instanceof ItemSword && getWeaponSkill(is) <= bestWeaponSkill() && !preferSword) {
			return true;
		} else if (is.getItem() instanceof ItemSword && getWeaponSkill(is) <= bestSwordSkill() && preferSword) {
			return true;
		} else if (is.getItem() instanceof ItemBow && getBowSkill(is) <= bestBowSkill()) {
			return true;
		} else {
			if (keepTools) {
				if (is.getItem() instanceof ItemAxe && getToolSkill(is) <= bestAxeSkill()
						&& (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && getToolSkill(is) <= bestPickSkill()
						&& (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}

				if (is.getItem() instanceof ItemSpade && getToolSkill(is) <= bestShovelSkill()
						&& (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}
			} else {
				if (is.getItem() instanceof ItemAxe && (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}

				if (is.getItem() instanceof ItemSpade && (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}
			}

			return false;
		}
	}

	public float getWeaponSkill(ItemStack is) {
		return getItemDamage(is);
	}

	public float getBowSkill(ItemStack is) {
		return getBowDamage(is);
	}

	public float getToolSkill(ItemStack is) {
		return getToolRating(is);
	}

	public float bestWeaponSkill() {
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				float toolDamage = getItemDamage(is);
				if (toolDamage >= itemDamage) {
					itemDamage = getItemDamage(is);
				}
			}
		}

		return itemDamage;
	}

	public float bestSwordSkill() {
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSword) {
					float swordDamage = getItemDamage(is);
					if (swordDamage >= itemDamage) {
						itemDamage = getItemDamage(is);
					}
				}
			}
		}

		return itemDamage;
	}

	public float bestBowSkill() {
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemBow) {
					float bowDamage = getBowDamage(is);
					if (bowDamage >= itemDamage) {
						itemDamage = getBowDamage(is);
					}
				}
			}
		}

		return itemDamage;
	}

	public float bestAxeSkill() {
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemAxe) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
					}
				}
			}
		}

		return itemSkill;
	}

	public float bestPickSkill() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemPickaxe) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
					}
				}
			}
		}

		return itemSkill;
	}

	public float bestShovelSkill() {
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSpade) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
					}
				}
			}
		}

		return itemSkill;
	}

	public ItemStack bestWeapon() {
		ItemStack bestWeapon = null;
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe
						|| is.getItem() instanceof ItemPickaxe) {
					float toolDamage = getItemDamage(is);
					if (toolDamage >= itemDamage) {
						itemDamage = getItemDamage(is);
						bestWeapon = is;
					}
				}
			}
		}

		return bestWeapon;
	}

	public ItemStack bestSword() {
		ItemStack bestSword = null;
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSword) {
					float swordDamage = getItemDamage(is);
					if (swordDamage >= itemDamage) {
						itemDamage = getItemDamage(is);
						bestSword = is;
					}
				}
			}
		}

		return bestSword;
	}

	public ItemStack bestBow() {
		ItemStack bestBow = null;
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemBow) {
					float bowDamage = getBowDamage(is);
					if (bowDamage >= itemDamage) {
						itemDamage = getBowDamage(is);
						bestBow = is;
					}
				}
			}
		}

		return bestBow;
	}

	public ItemStack bestAxe() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemAxe) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
						bestTool = is;
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack bestPick() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemPickaxe) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
						bestTool = is;
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack bestShovel() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSpade) {
					float toolSkill = getToolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = getToolRating(is);
						bestTool = is;
					}
				}
			}
		}

		return bestTool;
	}

	public float getToolRating(ItemStack itemStack) {
		float damage = getToolMaterialRating(itemStack, false);
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.0F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.1F;
		damage += (float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 1.0E-12F;
		return damage;
	}

	public float getItemDamage(ItemStack itemStack) {
		float damage = getToolMaterialRating(itemStack, true);
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
		damage += (float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 1.0E-12F;
		if (itemStack.getItem() instanceof ItemSword) {
			damage = (float) ((double) damage + 0.2D);
		}

		return damage;
	}

	public float getBowDamage(ItemStack itemStack) {
		float damage = 5.0F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.1F;
		damage += (float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage() * 0.001F;
		return damage;
	}

	public float getToolMaterialRating(ItemStack itemStack, boolean checkForDamage) {
	    if (itemStack == null || itemStack.getItem() == null) {
	        return 0.0F;
	    }

	    Item item = itemStack.getItem();
	    String materialName = null;

	    if (item instanceof ItemTool) {
	        materialName = ((ItemTool) item).getToolMaterialName();
	    } else if (item instanceof ItemSword) {
	        materialName = ((ItemSword) item).getToolMaterialName();
	    }

	    if (materialName == null) {
	        return 0.0F;
	    }

	    Map<String, Float> materialRatings = new HashMap<>();
	    materialRatings.put("WOOD", 2.0F);
	    materialRatings.put("STONE", 3.0F);
	    materialRatings.put("IRON", 4.0F);
	    materialRatings.put("GOLD", 2.0F);
	    materialRatings.put("EMERALD", 5.0F);

	    float baseRating = materialRatings.getOrDefault(materialName, 0.0F);

	    if (item instanceof ItemSword) {
	        baseRating += 2.0F;
	    } else if (item instanceof ItemPickaxe || item instanceof ItemSpade) {
	        baseRating = checkForDamage ? baseRating : baseRating * 10;
	    } else if (item instanceof ItemAxe) {
	        baseRating += 1.0F;
	    }

	    return baseRating;
	}

	public void openInv(String mode) {
		if (mode.equalsIgnoreCase("Spoof") && !isInventoryOpen && !(mc.currentScreen instanceof GuiInventory)) {
			PacketUtil.sendNoEvent(
					new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
			isInventoryOpen = true;
		}

	}

	public void closeInv(String mode) {
	    if (mode != null && mode.equalsIgnoreCase("Spoof") && isInventoryOpen && mc != null && mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) && mc.player != null && mc.player.inventoryContainer != null) {

	        PacketUtil.sendNoEvent(new C0DPacketCloseWindow(mc.player.inventoryContainer.windowId));

	        if (moveKeys != null) {
	            for (KeyBinding bind : moveKeys) {
	                if (bind != null) {
	                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
	                }
	            }
	        }

	        isInventoryOpen = false;
	    }
	}
}
