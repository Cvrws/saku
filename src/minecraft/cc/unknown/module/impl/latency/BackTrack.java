package cc.unknown.module.impl.latency;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Back Track", description = "Utiliza la latencia para atacar desde más lejos", category = Category.LATENCY)
public final class BackTrack extends Module {

    private final ArrayList<Packet> packets = new ArrayList<>();

    private EntityLivingBase entity = null;
    private INetHandler packetListener = null;
    private WorldClient lastWorld;
    
    private NumberValue delay = new NumberValue("Delay", this, 300, 25, 1000, 10);
    private NumberValue reach = new NumberValue("Reach", this, 4.7, 3.0, 6, 0.1);

    StopWatch timerHelper = new StopWatch();
    private boolean timeUpdate = true;
    private boolean keepAlive = true;
    private boolean knockback = true;

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	if(entity == null){
            return;
        }
        try{
            if (entity != null && mc.player != null && this.packetListener != null && mc.world != null) {
                double d0 = (double) this.entity.realPosX / 32.0D;
                double d1 = (double) this.entity.realPosY / 32.0D;
                double d2 = (double) this.entity.realPosZ / 32.0D;
                double d3 = (double) this.entity.serverPosX / 32.0D;
                double d4 = (double) this.entity.serverPosY / 32.0D;
                double d5 = (double) this.entity.serverPosZ / 32.0D;
                AxisAlignedBB alignedBB = new AxisAlignedBB(d3 - (double) this.entity.width, d4, d5 - (double) this.entity.width, d3 + (double) this.entity.width, d4 + (double) this.entity.height, d5 + (double) this.entity.width);
                Vec3 positionEyes = mc.player.getPositionEyes(mc.timer.renderPartialTicks);
                double currentX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB.minX, alignedBB.maxX);
                double currentY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB.minY, alignedBB.maxY);
                double currentZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB.minZ, alignedBB.maxZ);
                AxisAlignedBB alignedBB2 = new AxisAlignedBB(d0 - (double) this.entity.width, d1, d2 - (double) this.entity.width, d0 + (double) this.entity.width, d1 + (double) this.entity.height, d2 + (double) this.entity.width);
                double realX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB2.minX, alignedBB2.maxX);
                double realY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB2.minY, alignedBB2.maxY);
                double realZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB2.minZ, alignedBB2.maxZ);
                double distance = reach.getValue().doubleValue();
                if (!mc.player.canEntityBeSeen(this.entity)) {
                    distance = distance > 3 ? 3 : distance;
                }
                double bestX = MathHelper.clamp_double(positionEyes.xCoord, this.entity.getEntityBoundingBox().minX, this.entity.getEntityBoundingBox().maxX);
                double bestY = MathHelper.clamp_double(positionEyes.yCoord, this.entity.getEntityBoundingBox().minY, this.entity.getEntityBoundingBox().maxY);
                double bestZ = MathHelper.clamp_double(positionEyes.zCoord, this.entity.getEntityBoundingBox().minZ, this.entity.getEntityBoundingBox().maxZ);
                boolean b = false;
                boolean suwi = true;
                if ((positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.9)) {
                    b = true;
                }
                if (!suwi) {
                    b = true;
                }
                if (!(b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) + 0.05) || (mc.player.getDistance(d0, d1, d2) > distance) || (mc.player.getDistance(d0, d1, d2) < 2.3) || timerHelper.reached(delay.getValue().longValue())) {
                    this.resetPackets(this.packetListener);
                    timerHelper.reset();
                }

            }
        } catch (Exception e) {
            entity = null;
        }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (this.entity != null && this.entity != mc.player && !this.entity.isInvisible()) {
            try {
                final double x = this.entity.realPosX / 32D - mc.getRenderManager().renderPosX;
                final double y = this.entity.realPosY / 32D - mc.getRenderManager().renderPosY;
                final double z = this.entity.realPosZ / 32D - mc.getRenderManager().renderPosZ;

                GL11.glPushMatrix();
                GL11.glTranslated(x, y + this.entity.height, z);
                GL11.glNormal3d(0.0, 1.0, 0.0);
                GL11.glRotated(90, 1, 0, 0);

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);

                GL11.glDisable(GL11.GL_DEPTH_TEST);

                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glLineWidth(1);
                RenderUtil.color(new Color(0, 255, 0));
                final Cylinder cylinder = new Cylinder();

                cylinder.setDrawStyle(GLU.GLU_LINE);
                cylinder.setOrientation(GLU.GLU_INSIDE);
                cylinder.draw(0.62f, 0.62f, this.entity.height, 8, 1);

                RenderUtil.color(new Color(0, 255, 0, 150));
                cylinder.setDrawStyle(GLU.GLU_FILL);
                cylinder.setOrientation(GLU.GLU_INSIDE);
                cylinder.draw(0.62f, 0.65f, this.entity.height, 8, 1);

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            } catch (NullPointerException exception) {
            }
        }
    };

    @EventLink
    public final Listener<PacketReceiveEvent> onPacket = event -> {
        if (mc.world == null) return;

        this.packetListener = mc.getNetHandler();

        synchronized (BackTrack.class) {
        	if (event.getPacket() instanceof S14PacketEntity) {
        		handleS14PacketEntity((S14PacketEntity) event.getPacket());
        	} else if (event.getPacket() instanceof S18PacketEntityTeleport) {
        		handleS18PacketEntityTeleport((S18PacketEntityTeleport) event.getPacket());
        	} else if (event.getPacket() instanceof S08PacketPlayerPosLook) {
        		resetPackets(mc.getNetHandler());
        	}

        	this.entity = getModule(KillAura.class).isEnabled() && getModule(KillAura.class).target != null ? (getModule(AimAssist.class).isEnabled() && getModule(AimAssist.class).target != null ? getModule(AimAssist.class).target : getModule(KillAura.class).target) : null;
        	
        	if (this.entity == null) {
        		resetPackets(mc.getNetHandler());
        		return;
        	}
        	
        	if (mc.world != null && mc.player != null) {
        		if (this.lastWorld != mc.world) {
        			resetPackets(mc.getNetHandler());
        			this.lastWorld = mc.world;
        			return;
        		}
        	
        		addPackets(event.getPacket(), event);
        		this.lastWorld = mc.world;
        	}       
        }
    };

    private void handleS14PacketEntity(S14PacketEntity packetEntity) {
        Entity entity = mc.world.getEntityByID(packetEntity.entityId);
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            entityLivingBase.realPosX += packetEntity.posX;
            entityLivingBase.realPosY += packetEntity.posY;
            entityLivingBase.realPosZ += packetEntity.posZ;
        }
    }

    private void handleS18PacketEntityTeleport(S18PacketEntityTeleport teleportPacket) {
        Entity entity = mc.world.getEntityByID(teleportPacket.getEntityId());
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            entityLivingBase.realPosX = teleportPacket.getX();
            entityLivingBase.realPosY = teleportPacket.getY();
            entityLivingBase.realPosZ = teleportPacket.getZ();
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (this.packets.size() > 0) {
            synchronized (this.packets) {
                while (this.packets.size() != 0) {
                    try {
                        this.packets.get(0).processPacket(mc.getNetHandler());
                    } catch (Exception ignored) {
                    }
                    this.packets.remove(this.packets.get(0));
                }

            }
        }
    }

    private void addPackets(Packet packet, PacketReceiveEvent event) {
        synchronized (this.packets) {
            if (this.blockPacket(packet)) {
                this.packets.add(packet);
                event.setCancelled(true);
            }
        }
    }

    private boolean blockPacket(Packet packet) {
        if (packet instanceof S03PacketTimeUpdate) {
            return timeUpdate;
        } else if (packet instanceof S00PacketKeepAlive) {
            return keepAlive;
        } else if (packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) {
            return knockback;
        } else {
            return packet instanceof S32PacketConfirmTransaction || packet instanceof S14PacketEntity || packet instanceof S19PacketEntityStatus || packet instanceof S19PacketEntityHeadLook || packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
        }
    }

}