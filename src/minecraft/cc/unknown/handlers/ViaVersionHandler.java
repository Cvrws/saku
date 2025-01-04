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
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.MinimumMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.Accessor;
import cc.unknown.util.netty.packet.PlayPongC2SPacket;
import cc.unknown.util.player.PlayerUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ViaVersionHandler implements Accessor {
	
    private boolean lastGround;

	/*
	 * Block Fix
	 */
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
			if (PlayerUtil.getItemStack() != null && PlayerUtil.getItemStack().getItem() instanceof ItemSword && (mc.gameSettings.keyBindUseItem.isPressed() || getModule(KillAura.class).blocking)) {
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
        
        
        /*
         * Flying Packet Fix
         */
        
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            final Packet<?> packet = event.getPacket();

            if (packet instanceof C03PacketPlayer) {
                final C03PacketPlayer wrapper = ((C03PacketPlayer) packet);

                if (!wrapper.isMoving() && !wrapper.isRotating() && wrapper.onGround == this.lastGround) {
                    event.setCancelled();
                }

                this.lastGround = wrapper.onGround;
            }
        }
        
        /*
         * Interact Fix
         */
        
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {

        	if (!event.isCancelled()) {
	            if (event.getPacket() instanceof C02PacketUseEntity) {
	                C02PacketUseEntity use = ((C02PacketUseEntity) event.getPacket());
	
	                event.setCancelled(event.isCancelled() || !use.getAction().equals(C02PacketUseEntity.Action.ATTACK));
	            }
        	}
        }
        
        /*
         * Transaction Fix
         */
        
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
        	if (!event.isCancelled()) {
                if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                    C0FPacketConfirmTransaction transaction = (C0FPacketConfirmTransaction) event.getPacket();

                    cc.unknown.util.netty.PacketUtil.send(new PlayPongC2SPacket(transaction.getUid()));

                    event.setCancelled();
                }
            }
        }
    };
    
    /*
     * Bounding Box Fix
     */
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            mc.player.setEntityBoundingBox(new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY,
                    mc.player.posZ - 0.3, mc.player.posX + 0.3, mc.player.posY + 1.8,
                    mc.player.posZ + 0.3));
        }
        
        /*
         * Post Fix
         */
        
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            NetworkingHandler.spoof(1, true, true, false, true);
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
     * Post Fix
     */
    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            NetworkingHandler.dispatch();
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
