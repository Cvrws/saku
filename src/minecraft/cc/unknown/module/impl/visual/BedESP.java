package cc.unknown.module.impl.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@ModuleInfo(aliases = "Bed ESP", description = "Renderiza las camas", category = Category.VISUALS)
public final class BedESP extends Module {

    private final NumberValue range = new NumberValue("Range", this, 15, 2, 30, 1);
    private final NumberValue rate = new NumberValue("Rate", this, 0.4D, 0.1D, 3D, 0.1D);
	
    private BlockPos[] bed = null;
    private final List<BlockPos[]> beds = new ArrayList<>();
    private long lastCheck = 0L;

    @Override
    public void onDisable() {
        bed = null;
        beds.clear();
    }
    
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (System.currentTimeMillis() - lastCheck >= rate.getValue().doubleValue() * 1000.0) {
            lastCheck = System.currentTimeMillis();

            int rangeValue = range.getValue().intValue();
            for (int i = -rangeValue; i <= rangeValue; ++i) {
                for (int j = -rangeValue; j <= rangeValue; ++j) {
                    for (int k = -rangeValue; k <= rangeValue; ++k) {
                        BlockPos blockPos = new BlockPos(mc.player.posX + j, mc.player.posY + i, mc.player.posZ + k);
                        IBlockState getBlockState = mc.world.getBlockState(blockPos);
                        if (getBlockState.getBlock() == Blocks.bed && getBlockState.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
                            for (BlockPos[] bedPair : beds) {
                                if (BlockPos.isSamePos(blockPos, bedPair[0])) {
                                    continue;
                                }
                            }
                            beds.add(new BlockPos[]{blockPos, blockPos.offset(getBlockState.getValue(BlockBed.FACING))});
                        }
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	if (isInGame() && !beds.isEmpty()) {
    		Iterator<BlockPos[]> iterator = beds.iterator();
    		while (iterator.hasNext()) {
    			BlockPos[] blockPos = iterator.next();
    			if (mc.world.getBlockState(blockPos[0]).getBlock() instanceof BlockBed) {
    				renderBed(blockPos);
    			} else {
    				iterator.remove();
    			}
    		}
    	}
    };
    
    private void renderBed(BlockPos[] array) {
        final double n = array[0].getX() - mc.getRenderManager().viewerPosX;
        final double n2 = array[0].getY() - mc.getRenderManager().viewerPosY;
        final double n3 = array[0].getZ() - mc.getRenderManager().viewerPosZ;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderUtil.color(ColorUtil.withAlpha(Sakura.instance.getThemeManager().getTheme().getFirstColor(), (int) (255 * 0.25)));
        AxisAlignedBB axisAlignedBB;
        if (array[0].getX() != array[1].getX()) {
            if (array[0].getX() > array[1].getX()) {
                axisAlignedBB = new AxisAlignedBB(n - 1.0, n2, n3, n + 1.0, n2 + 0.5625F, n3 + 1.0);
            } else {
                axisAlignedBB = new AxisAlignedBB(n, n2, n3, n + 2.0, n2 + 0.5625F, n3 + 1.0);
            }
        } else if (array[0].getZ() > array[1].getZ()) {
            axisAlignedBB = new AxisAlignedBB(n, n2, n3 - 1.0, n + 1.0, n2 + 0.5625F, n3 + 1.0);
        } else {
            axisAlignedBB = new AxisAlignedBB(n, n2, n3, n + 1.0, n2 + 0.5625F, n3 + 2.0);
        }
        RenderUtil.renderHitbox(axisAlignedBB, 7);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
}
