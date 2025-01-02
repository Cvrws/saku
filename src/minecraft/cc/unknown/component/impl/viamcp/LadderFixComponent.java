package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public final class LadderFixComponent extends Component {

    @EventLink
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            final Block block = event.getBlock();

            if (block instanceof BlockLadder) {
                final BlockPos blockPos = event.getBlockPos();
                final IBlockState iblockstate = mc.theWorld.getBlockState(blockPos);

                if (iblockstate.getBlock() == block) {
                    final float f = 0.125F + 0.0625f;

                    switch (iblockstate.getValue(BlockLadder.FACING)) {
                        case NORTH:
                            event.setBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F)
                                    .offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                            break;

                        case SOUTH:
                            event.setBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f)
                                    .offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                            break;

                        case WEST:
                            event.setBoundingBox(new AxisAlignedBB(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F)
                                    .offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                            break;

                        case EAST:
                        default:
                            event.setBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F)
                                    .offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    }
                }
            }
        }
    };
}