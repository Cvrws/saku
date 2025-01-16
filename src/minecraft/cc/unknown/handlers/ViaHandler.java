package cc.unknown.handlers;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.MinimumMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.ghost.BlockHit;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ViaHandler implements Accessor {
	
    private boolean lastGround;

	/*
	 * Block Fix
	 */
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
			if (PlayerUtil.getItemStack() != null && PlayerUtil.getItemStack().getItem() instanceof ItemSword && (mc.gameSettings.keyBindUseItem.isPressed() || getModule(KillAura.class).blocking || getModule(BlockHit.class).block)) {
                PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem.write(Type.VAR_INT, 1);
                PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
			}
		}
	};
	
	/*
	 * Placement Fix
	 */
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketSendEvent> onSend = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_11)) {
            final Packet<?> packet = event.getPacket();
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                ((C08PacketPlayerBlockPlacement) packet).facingX = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingY = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingZ = 0.5F;
            }
        }
    };
    
    /*
     * Ladder Fix
     */
    @EventLink
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            final Block block = event.getBlock();

            if (block instanceof BlockLadder) {
                final BlockPos blockPos = event.getBlockPos();
                final IBlockState iblockstate = mc.world.getBlockState(blockPos);

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
    
    /*
     * Friction Fix
     */
    @EventLink(value = Priority.LOW)
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            if (!mc.player.isPotionActive(Potion.moveSpeed)) return;

            float[][] friction = {new float[]{0.11999998f, 0.15599997f}, new float[]{0.13999997f, 0.18199998f}};

            int speed = Math.min(mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier(), 1);
            boolean ground = mc.player.onGround;
            boolean sprinting = mc.player.isSprinting();

            if (ground) event.setFriction(friction[speed][sprinting ? 1 : 0]);
        }
    };
    
    /*
     * Motion Fix
     */
    @EventLink
    public final Listener<MinimumMotionEvent> onMinimumMotion = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            event.setMinimumMotion(0.003D);
        }
    };
}