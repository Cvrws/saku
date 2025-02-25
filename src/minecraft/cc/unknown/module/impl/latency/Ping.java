package cc.unknown.module.impl.latency;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.TimedPacket;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

@ModuleInfo(aliases = "Ping", description = "Modifica tu ping [Experimental]", category = Category.LATENCY)
public class Ping extends Module {
    
    private NumberValue delay = new NumberValue("Ping Size", this, 0, 0, 2000, 1);
    private BooleanValue playerModel = new BooleanValue("Player Model", this, true);
    private BooleanValue onlyCombat = new BooleanValue("Only Combat", this, false);
    private BooleanValue autoRemoveTarget = new BooleanValue("Auto Remove Target", this, false, () -> !onlyCombat.getValue());
    private BooleanValue resetVelocity = new BooleanValue("Reset on Velocity", this, false);
    private final ConcurrentLinkedQueue<TimedPacket> packetList = new ConcurrentLinkedQueue<>();

    private EntityPlayer target = null;
    private Animation animationX, animationY, animationZ = null;
    private double realPosX, realPosY, realPosZ = 0.0;
	
    @Override
    public void onDisable() {
    	reset();
    }
	
    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (mc.player == null || (onlyCombat.getValue() && target == null)) return;

    	Packet<?> packet = event.getPacket();

        if (!event.isCancelled()) {
            packetList.add(new TimedPacket(packet, System.currentTimeMillis()));
            event.setCancelled();

            if (packet instanceof S14PacketEntity) {
                S14PacketEntity entityPacket = (S14PacketEntity) packet;
                if (target != null && entityPacket.getEntity(mc.world).getEntityId() == target.getEntityId()) {
                    realPosX += entityPacket.func_149062_c() / 32.0;
                    realPosY += entityPacket.func_149061_d() / 32.0;
                    realPosZ += entityPacket.func_149064_e() / 32.0;
                }
            } else if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) packet;
                if (resetVelocity.getValue() && velocityPacket.getEntityID() == mc.player.getEntityId()) {
                    clearPacket(0);
                }
            } else if (packet instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport teleportPacket = (S18PacketEntityTeleport) packet;
                if (target != null && teleportPacket.getEntityId() == target.getEntityId()) {
                    realPosX = teleportPacket.getX() / 32.0;
                    realPosY = teleportPacket.getY() / 32.0;
                    realPosZ = teleportPacket.getZ() / 32.0;
                }
            }
        }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	if (target != null) {
    	    startAnimation();

    	    if (playerModel.getValue()) {
    	        GL11.glPushMatrix();
    	        mc.getRenderManager().doRenderEntity(
    	            target,
    	            animationX.getValue() - mc.getRenderManager().renderPosX,
    	            animationY.getValue() - mc.getRenderManager().renderPosY,
    	            animationZ.getValue() - mc.getRenderManager().renderPosZ,
    	            target.rotationYaw,
    	            event.getPartialTicks(),
    	            true
    	        );
    	        GL11.glPopMatrix();
    	        GlStateManager.resetColor();
    	    } else {
    	        startDrawing();
    	        drawEsp();
    	        stopDrawing();
    	    }
    	}

    };
    
    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
    	if (mc.player == null || mc.world == null) return;

    	Entity entity = event.getTarget();
    	if (entity instanceof EntityPlayer) {
    	    if (target != entity) {
    	        target = (EntityPlayer) entity;
    	        realPosX = entity.posX;
    	        realPosY = entity.posY;
    	        realPosZ = entity.posZ;
    	        startAnimation();
    	    }
    	}
    };
    
    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
   	    clearPacket(0);

	    if (target != null) {
	        if (autoRemoveTarget.getValue() && onlyCombat.getValue()) {
	            if (mc.player.getDistanceToEntity(target) >= 7F) {
	                clearPacket(0);
	                target = null;
	            }
	        }
	    }
    };
    
    @EventLink
    public final Listener<WorldChangeEvent> onWorld = event -> clearPacket(0);
    
    private void reset() {
        target = null;
        realPosX = 0.0;
        realPosY = 0.0;
        realPosZ = 0.0;
        animationX = null;
        animationY = null;
        animationZ = null;
        clearPacket(0);
        packetList.clear();
    }
    
    private void clearPacket(int time) {
        if (time == 0) {
            time = delay.getValue().intValue();
        }
        
        Iterator<TimedPacket> iterator = packetList.iterator();
        while (iterator.hasNext()) {
        	TimedPacket packet = iterator.next();
            
        	if (System.currentTimeMillis() > packet.getStopWatch().getMillis() + time) {
            	Packet p = packet.getPacket();
            	p.processPacket(mc.getNetHandler().getNetworkManager().packetListener);
                iterator.remove();
            }
        }
    }
    
    private void startDrawing() {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
    }

    private void stopDrawing() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
    
    private void startAnimation() {
        if (animationX == null || animationY == null || animationZ == null) {
            animationX = new Animation(Easing.LINEAR, 150);
            animationY = new Animation(Easing.LINEAR, 150);
            animationZ = new Animation(Easing.LINEAR, 150);
        }

        animationX.animate(realPosX);
        animationY.animate(realPosY);
        animationZ.animate(realPosZ);
    }
    
    private void drawEsp() {
        ColorUtil.glColor(getTheme().getFirstColor().getRGB());
        RenderUtil.drawBoundingBlock(
            mc.player.getEntityBoundingBox().offset(-mc.player.posX, -mc.player.posY, -mc.player.posZ)
                .offset(animationX.getValue(), animationY.getValue(), animationZ.getValue()).expand(0.08, 0.08, 0.08)
        );
    }
}
