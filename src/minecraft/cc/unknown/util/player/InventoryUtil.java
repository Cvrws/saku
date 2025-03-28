package cc.unknown.util.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cc.unknown.util.Accessor;
import cc.unknown.util.netty.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@UtilityClass
public class InventoryUtil implements Accessor {

	public final List<Block> blacklist = Arrays.asList(Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
			Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot,
			Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.light_weighted_pressure_plate,
			Blocks.heavy_weighted_pressure_plate, Blocks.jukebox, Blocks.air, Blocks.iron_bars,
			Blocks.stained_glass_pane, Blocks.ladder, Blocks.glass_pane, Blocks.carpet, Blocks.enchanting_table,
			Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch,
			Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.iron_door, Blocks.dropper, Blocks.tnt,
			Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch, Blocks.oak_door);

	public int getBlockSlot(boolean hypixel) {
		int item = -1;
		int stacksize = 0;
		if (!hypixel && mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() != null
				&& mc.player.getHeldItem().getItem() instanceof ItemBlock
				&& !blacklist.contains(((ItemBlock) mc.player.getHeldItem().getItem()).getBlock())) {
			return mc.player.inventory.currentItem;
		} else {
			for (int i = 36; i < 45; ++i) {
				if (mc.player.inventoryContainer.getSlot(i).getStack() != null
						&& mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock
						&& !blacklist.contains(
								((ItemBlock) mc.player.inventoryContainer.getSlot(i).getStack().getItem()).getBlock())
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
				if (block != null && blacklist != null && !blacklist.contains(block)) {
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
						if (block != null && blacklist != null && !blacklist.contains(block)) {
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
				if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
					if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
						return false;
					}
				}
			}

			return true;
		}
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
					&& mc.player.inventoryContainer.getSlot(4 + type).getStack().getUnlocalizedName().contains(strType)
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
			} else {
				if (is.getItem() instanceof ItemAxe && (preferSword || is != bestWeapon())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && (preferSword || is != bestWeapon())) {
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
					&& mc.player.inventoryContainer.getSlot(4 + type).getStack().getUnlocalizedName().contains(strType)
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
			} else {
				if (is.getItem() instanceof ItemAxe && (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
					return true;
				}

				if (is.getItem() instanceof ItemPickaxe && (preferSword || getWeaponSkill(is) <= bestWeaponSkill())) {
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

	public int findTool(final BlockPos blockPos) {
		float bestSpeed = 1;
		int bestSlot = -1;

		final IBlockState blockState = mc.world.getBlockState(blockPos);

		for (int i = 0; i < 9; i++) {
			final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

			if (itemStack == null) {
				continue;
			}

			final float speed = itemStack.getStrVsBlock(blockState.getBlock());

			if (speed > bestSpeed) {
				bestSpeed = speed;
				bestSlot = i;
			}
		}

		return bestSlot;
	}

	public int findItem(final Item item) {
		for (int i = 0; i < 9; i++) {
			final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

			if (itemStack == null) {
				if (item == null) {
					return i;
				}
				continue;
			}

			if (itemStack.getItem() == item) {
				return i;
			}
		}

		return -1;
	}

	public int findBlock(final Block block) {
		for (int i = 0; i < 9; i++) {
			final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

			if (itemStack == null) {
				if (block == null) {
					return i;
				}
				continue;
			}

			if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == block) {
				return i;
			}
		}

		return -1;
	}

	public int findBlock() {
		int slot = -1;
		int highestStack = -1;
		for (int i = 0; i < 9; ++i) {
			final ItemStack itemStack = mc.player.inventory.mainInventory[i];
			if (itemStack != null && itemStack.getItem() instanceof ItemBlock
					&& blacklist.stream().noneMatch(block -> block.equals(((ItemBlock) itemStack.getItem()).getBlock()))
					&& itemStack.stackSize > 0) {
				if (mc.player.inventory.mainInventory[i].stackSize > highestStack) {
					highestStack = mc.player.inventory.mainInventory[i].stackSize;
					slot = i;
				}
			}
		}
		return slot;
	}

	public int getMaxDamageSlot() {
		int index = -1;
		double damage = -1;

		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
			if (itemInSlot == null)
				continue;
			for (AttributeModifier mooommHelp : itemInSlot.getAttributeModifiers().values()) {
				if (mooommHelp.getAmount() > damage) {
					damage = mooommHelp.getAmount();
					index = slot;
				}
			}

		}
		return index;
	}

	public double getSlotDamage(int slot) {
		ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
		if (itemInSlot == null)
			return -1;
		for (AttributeModifier mooommHelp : itemInSlot.getAttributeModifiers().values()) {
			return mooommHelp.getAmount();
		}
		return -1;
	}

	public boolean isSword() {
		if (mc.player.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.player.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
	}

	public boolean canBePlaced(ItemBlock itemBlock) {
		Block block = itemBlock.getBlock();
		if (block == null) {
			return false;
		}
		return !isInteractable(block) && !(block instanceof BlockTNT) && !(block instanceof BlockSlab)
				&& !(block instanceof BlockWeb) && !(block instanceof BlockLever) && !(block instanceof BlockButton)
				&& !(block instanceof BlockSkull) && !(block instanceof BlockLiquid) && !(block instanceof BlockCactus)
				&& !(block instanceof BlockCarpet) && !(block instanceof BlockTripWire)
				&& !(block instanceof BlockTripWireHook) && !(block instanceof BlockTallGrass)
				&& !(block instanceof BlockFlower) && !(block instanceof BlockFlowerPot)
				&& !(block instanceof BlockSign) && !(block instanceof BlockLadder) && !(block instanceof BlockTorch)
				&& !(block instanceof BlockRedstoneTorch) && !(block instanceof BlockFence)
				&& !(block instanceof BlockPane) && !(block instanceof BlockStainedGlassPane)
				&& !(block instanceof BlockGravel) && !(block instanceof BlockClay) && !(block instanceof BlockSand)
				&& !(block instanceof BlockSoulSand);
	}

	private boolean isInteractable(Block block) {
		return block instanceof BlockFurnace || block instanceof BlockFenceGate || block instanceof BlockChest
				|| block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable
				|| block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDropper
				|| block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil
				|| block == Blocks.crafting_table;
	}

	public float getToolDigEfficiency(final Block blockIn, final int slot) {
		float f = getStrVsBlock(blockIn, slot);

		if (f > 1.0F) {
			final int i = EnchantmentHelper.getEfficiencyModifier(mc.player);
			final ItemStack itemstack = getCurrentItemInSlot(slot);

			if (i > 0 && itemstack != null) {
				f += (float) (i * i + 1);
			}
		}

		if (mc.player.isPotionActive(Potion.digSpeed)) {
			f *= 1.0F + (float) (mc.player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if (mc.player.isPotionActive(Potion.digSlowdown)) {
			final float f1;

			switch (mc.player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
			case 0:
				f1 = 0.3F;
				break;

			case 1:
				f1 = 0.09F;
				break;

			case 2:
				f1 = 0.0027F;
				break;

			case 3:
			default:
				f1 = 8.1E-4F;
			}

			f *= f1;
		}

		if (mc.player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
			f /= 5.0F;
		}

		if (!mc.player.onGround) {
			f /= 5.0F;
		}

		return f;
	}
	
	private ItemStack getCurrentItemInSlot(final int slot) {
		return slot < 9 && slot >= 0 ? mc.player.inventory.mainInventory[slot] : null;
	}
	
	public float getPlayerRelativeBlockHardness(final EntityPlayer playerIn, final World worldIn, final BlockPos pos,
			final int slot) {
		final Block block = mc.world.getBlockState(pos).getBlock();
		final float f = block.getBlockHardness(worldIn, pos);
		return f < 0.0F ? 0.0F
				: (!canHeldItemHarvest(block, slot) ? getToolDigEfficiency(block, slot) / f / 100.0F
						: getToolDigEfficiency(block, slot) / f / 30.0F);
	}
	
	private boolean canHeldItemHarvest(final Block blockIn, final int slot) {
		if (blockIn.getMaterial().isToolNotRequired()) {
			return true;
		} else {
			final ItemStack itemstack = mc.player.inventory.getStackInSlot(slot);
			return itemstack != null && itemstack.canHarvestBlock(blockIn);
		}
	}
	
	public float getBlockBreakTime(World worldIn, Block block, BlockPos blockIn, int slot, Item item,
			boolean onGround) {
		int speedMultiplier = 1;
		boolean stop = false;
		if (item == null) {
			speedMultiplier = 1;
			stop = true;
		}
		int id;
		if (!stop) {
			id = Item.getIdFromItem(item);
			if (id == 269 || id == 270 || id == 271) {
				speedMultiplier = 2;
			}
			if (id == 273 || id == 274 || id == 275) {
				speedMultiplier = 4;
			}
			if (id == 256 || id == 257 || id == 258) {
				speedMultiplier = 6;
			}
			if (id == 277 || id == 278 || id == 279) {
				speedMultiplier = 8;
			}
			if (id == 284 || id == 285 || id == 286) {
				speedMultiplier = 12;
			}
			if ((id == 267 || id == 268 || id == 272 || id == 276 || id == 283) && block instanceof BlockWeb) {
				speedMultiplier = (int) 1.5;
			}
			if (id == 359 && block instanceof BlockVine) {
				speedMultiplier = 1;
			} else if (id == 359 && (block instanceof BlockWeb || block instanceof BlockLeaves)) {
				speedMultiplier = 15;
			} else if (id == 359 && block.getBlockHardness(mc.world, blockIn) == 0.8) {
				speedMultiplier = 5;
			}
			if (!canHeldItemHarvest(block, slot)) {
				speedMultiplier = 1;
			} else if (EnchantmentHelper.getEfficiencyModifier(mc.player) != 0) {
				speedMultiplier += EnchantmentHelper.getEfficiencyModifier(mc.player) ^ 2 + 1;
			}
		}
		if (mc.player.isPotionActive(Potion.digSpeed))
			speedMultiplier *= 0.2 * mc.player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1;

		if (mc.player.isPotionActive(Potion.digSlowdown)) {
			final float f1;

			switch (mc.player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
			case 0:
				f1 = 0.3F;
				break;

			case 1:
				f1 = 0.09F;
				break;

			case 2:
				f1 = 0.0027F;
				break;

			case 3:
			default:
				f1 = 8.1E-4F;
			}
			speedMultiplier *= f1;
		}
		if (mc.player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
			speedMultiplier /= 5.0F;
		}

		if (!onGround) {
			speedMultiplier /= 5.0F;
		}

		float damage = speedMultiplier / block.getBlockHardness(worldIn, blockIn);

		if (canHeldItemHarvest(block, slot)) {
			damage /= 30;
		} else {
			damage /= 100;
		}

		if (damage > 1) {
			return 0;
		}

		int ticks = (int) Math.ceil(1 / damage);

		return ticks;
	}
	
	public float getStrVsBlock(final Block blockIn, final int slot) {
		float f = 1.0F;

		if (mc.player.inventory.mainInventory[slot] != null) {
			f *= mc.player.inventory.mainInventory[slot].getStrVsBlock(blockIn);
		}
		return f;
	}
}
