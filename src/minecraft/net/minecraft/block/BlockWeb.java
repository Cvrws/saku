package net.minecraft.block;

import java.util.Random;

import cc.unknown.Sakura;
import cc.unknown.event.impl.player.BlockWebEvent;
import cc.unknown.util.Accessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockWeb extends Block
{
    public BlockWeb()
    {
        super(Material.web);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (Accessor.mc.player != null && Accessor.mc.world != null && entityIn == Accessor.mc.player) {
            BlockWebEvent event = new BlockWebEvent(pos, state);
            Sakura.instance.getEventBus().handle(event);

            if (event.isCancelled())
                return;
        }
    	
    	entityIn.setInWeb();
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    public boolean isFullCube()
    {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.string;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }
}
