package cc.unknown.module.impl.latency;

import java.util.ArrayDeque;

import com.google.common.collect.Queues;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.Blink;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.netty.TimedPacket;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Fake Lag", description = "Bad Wifi", category = Category.LATENCY)
public final class FakeLag extends Module {
	
    private final NumberValue delay = new NumberValue("Delay", this, 550, 0, 1000, 50);
    private final NumberValue recoilTime = new NumberValue("Time", this, 750, 0, 2000, 50);
    private final BoundsNumberValue allowedDistToEnemy = new BoundsNumberValue("Enemy Distance", this, 1.5, 3.5, 0, 6, 0.1);
    
    private final BooleanValue blinkOnAction = new BooleanValue("Blink on Action", this, true);
    private final BooleanValue pauseOnNoMove = new BooleanValue("Pause on No Move", this, true);
    private final BooleanValue pauseOnChest = new BooleanValue("Pause on Chest", this, true);

    private final ArrayDeque<TimedPacket> packetQueue = Queues.newArrayDeque();
    private final ArrayDeque<PositionData> positions = Queues.newArrayDeque();
    private final StopWatch resetTimer = new StopWatch();
    private boolean wasNearEnemy = false;
    private boolean ignoreWholeTick = false;
    
    @Override
    public void onDisable() {
        if (mc.player == null) return;

        blink(false);
    }
    
    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        final EntityPlayerSP player = mc.player;
        if (player == null) return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S01PacketPong) {
            return;
        }

        if (packet instanceof S08PacketPlayerPosLook) {
        	blink(false);
            return;
        }

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) packet;
            if (player.getEntityId() == velocityPacket.getEntityID()) {
            	blink(false);
                return;
            }
        }

        if (packet instanceof S27PacketExplosion) {
            S27PacketExplosion explosionPacket = (S27PacketExplosion) packet;
            if (explosionPacket.field_149153_g != 0f || explosionPacket.field_149152_f != 0f || explosionPacket.field_149159_h != 0f) {
            	blink(false);
                return;
            }
        }

        if (!resetTimer.finished(recoilTime.getValue().intValue())) return;

        if (mc.isSingleplayer() || mc.currentServerData == null) {
        	blink(false);
            return;
        }
    };
    
    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
    	 blink(false);
    };
    
    @EventLink
    public final Listener<PacketSendEvent> onSend = event -> {
        final Packet<?> packet = event.getPacket();
        
        if (packet instanceof S01PacketPong) {
            return;
        }

        if (packet instanceof S08PacketPlayerPosLook) {
        	blink(false);
            return;
        }

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) packet;
            if (mc.player.getEntityId() == velocityPacket.getEntityID()) {
            	blink(false);
                return;
            }
        }

        if (packet instanceof S27PacketExplosion) {
            S27PacketExplosion explosionPacket = (S27PacketExplosion) packet;
            if (explosionPacket.field_149153_g != 0f || explosionPacket.field_149152_f != 0f || explosionPacket.field_149159_h != 0f) {
            	blink(false);
                return;
            }
        }

        if (!resetTimer.finished(recoilTime.getValue().intValue())) return;

        if (mc.isSingleplayer() || mc.currentServerData == null) {
        	blink(false);
            return;
        }
        
        if (mc.player.isDead || event.isCancelled() || allowedDistToEnemy.getSecondValue().floatValue() > 0.0 && wasNearEnemy || ignoreWholeTick) {
            return;
        }

        if (pauseOnNoMove.getValue() && !MoveUtil.isMoving()) {
            blink(false);
            return;
        }

        if (mc.player.getHealth() < mc.player.getMaxHealth()) {
            if (mc.player.hurtTime != 0) {
            	blink(false);
                return;
            }
        }

        if (getModule(Scaffold.class).isEnabled() || getModule(LegitScaffold.class).isEnabled()) {
        	blink(false);
            return;
        }

        
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof RiceGui) {
        	blink(false);
        	return;
        }
        
        if (pauseOnChest.getValue() && mc.currentScreen instanceof GuiContainer) {
        	blink(false);
            return;
        }

        if (blinkOnAction.getValue() && packet instanceof C02PacketUseEntity || mc.player.isEating()) {
        	blink(false);
            return;
        }
        
        if (packet instanceof C00Handshake || packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing || 
                packet instanceof C01PacketChatMessage) {
                return;
        }
        
        if (packet instanceof C0EPacketClickWindow || packet instanceof C0DPacketCloseWindow) {
        	blink(false);
            return;
        }

        if (packet instanceof C08PacketPlayerBlockPlacement || 
            packet instanceof C07PacketPlayerDigging || packet instanceof C12PacketUpdateSign || 
            packet instanceof C19PacketResourcePackStatus) {
        	blink(false);
            return;
        }

        event.setCancelled(true);
        
        if (packet instanceof C03PacketPlayer && ((C03PacketPlayer) packet).isMoving()) {
            synchronized (positions) {
                positions.add(new PositionData(((C03PacketPlayer) packet).getPos(), System.currentTimeMillis(), mc.player.renderYawOffset, RotationHandler.rotations));
            }
        }

        synchronized (packetQueue) {
            packetQueue.add(new TimedPacket(packet, System.currentTimeMillis()));
        }
    };
    
    @EventLink
    public final Listener<GameEvent> onGame = event -> {
        WorldClient world = mc.world;

        if (mc.player == null || world == null) {
            return;
        }

        if (allowedDistToEnemy.getSecondValue().floatValue() > 0) {
            Vec3 playerPos = mc.player.getPositionVector();
            Vec3 serverPos = positions.isEmpty() ? playerPos : positions.getFirst().getPos();
            AxisAlignedBB playerBox = mc.player.getEntityBoundingBox().offset(serverPos.subtract(playerPos).xCoord, serverPos.subtract(playerPos).yCoord, serverPos.subtract(playerPos).zCoord);

            wasNearEnemy = false;

            for (EntityPlayer otherPlayer : world.playerEntities) {
                if (otherPlayer == mc.player) {
                    continue;
                }

                Entity entityMixin = (Entity) otherPlayer;

                if (entityMixin != null) {
                    Vec3 eyes = getTruePositionEyes(otherPlayer);

                    if (eyes.distanceTo(getNearestPointBB(eyes, playerBox)) >= allowedDistToEnemy.getValue().longValue()
                        && eyes.distanceTo(getNearestPointBB(eyes, playerBox)) <= allowedDistToEnemy.getSecondValue().floatValue()) {
                        blink(true);
                        wasNearEnemy = true;
                        return;
                    }
                }
            }
        }

        if (getModule(Blink.class).isEnabled() || mc.player.isDead || mc.player.isUsingItem()) {
            blink(false);
            return;
        }

        if (!resetTimer.finished(recoilTime.getValue().intValue())) {
            return;
        }

        handlePackets(true);
        ignoreWholeTick = false;
    };
	
    public void blink(boolean handlePackets) {
        mc.addScheduledTask(() -> {
            if (handlePackets) {
                resetTimer.reset();
            }

            handlePackets(true);
            ignoreWholeTick = true;
        });
    }

    public void handlePackets(boolean clear) {
        synchronized (packetQueue) {
            packetQueue.removeIf(queueData -> {
                TimedPacket data = queueData;
                long timestamp = data.getStopWatch().getMillis();
                if (timestamp <= System.currentTimeMillis() - delay.getValue().longValue() || clear) {
                    PacketUtil.sendNoEvent(data.getPacket());
                    return true;
                }
                return false;
            });
        }

        synchronized (positions) {
            positions.removeIf(positionData -> positionData.getTime() <= System.currentTimeMillis() - delay.getValue().longValue() || clear);
        }
    }
    
    private Vec3 getTruePositionEyes(EntityPlayer player) {
        Entity entity = (Entity) player;
        return new Vec3(entity.getTrueX(), entity.getTrueY() + player.getEyeHeight(), entity.getTrueZ());
    }
    
    public Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
        double[] origin = new double[]{eye.xCoord, eye.yCoord, eye.zCoord};
        double[] destMins = new double[]{box.minX, box.minY, box.minZ};
        double[] destMaxs = new double[]{box.maxX, box.maxY, box.maxZ};

        for (int i = 0; i < 3; i++) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
            } else if (origin[i] < destMins[i]) {
                origin[i] = destMins[i];
            }
        }

        return new Vec3(origin[0], origin[1], origin[2]);
    }

	public class PositionData {
	    private final Vec3 pos;
	    private final long time;
	    private final float body;
	    private final Vector2f rotation;
	
	    public PositionData(Vec3 pos, long time, float body, Vector2f rotation) {
	        this.pos = pos;
	        this.time = time;
	        this.body = body;
	        this.rotation = rotation;
	    }
	
	    public Vec3 getPos() {
	        return pos;
	    }
	
	    public long getTime() {
	        return time;
	    }
	
	    public float getBody() {
	        return body;
	    }
	
	    public Vector2f getRotation() {
	        return rotation;
	    }
	
	    @Override
	    public String toString() {
	        return "PositionData{" +
	                "pos=" + pos +
	                ", time=" + time +
	                ", body=" + body +
	                ", rotation=" + rotation +
	                '}';
	    }
	}
}