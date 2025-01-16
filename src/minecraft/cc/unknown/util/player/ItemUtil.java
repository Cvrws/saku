package cc.unknown.util.player;

import java.util.Arrays;
import java.util.List;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class ItemUtil implements Accessor {

    public int getMaxDamageSlot(){
        int index = -1;
        double damage = -1;

        for (int slot = 0; slot <= 8; slot++) {
           ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
           if(itemInSlot == null)
              continue;
           for (AttributeModifier mooommHelp :itemInSlot.getAttributeModifiers().values()){
              if(mooommHelp.getAmount() > damage) {
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
}
