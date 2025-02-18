package cc.unknown.util.player;

import java.util.Arrays;
import java.util.List;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@UtilityClass
public class SlotUtil implements Accessor {
	private boolean holdWaterBucket() {
		if (containsItem(mc.player.getHeldItem(), Items.water_bucket)) {
			return true;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (containsItem(mc.player.inventory.mainInventory[i], Items.water_bucket)) {
					mc.player.inventory.currentItem = i;
					return true;
				}
			}
			return false;
		}
	}

	private boolean containsItem(ItemStack itemStack, Item item) {
		return itemStack != null && itemStack.getItem() == item;
	}
}