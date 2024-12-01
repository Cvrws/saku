package cc.unknown.module.impl.latency;

import java.util.ArrayList;

import com.mojang.authlib.GameProfile;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(aliases = "Back Track", description = "Utiliza la latencia para atacar desde más lejos", category = Category.LATENCY)
public final class BackTrack extends Module {

	private final ArrayList<Packet> incomingPackets = new ArrayList();
	private final ArrayList<Packet> outgoingPackets = new ArrayList();
	private double lastRealX;
	private double lastRealY;
	private double lastRealZ;
	private WorldClient lastWorld;
	private EntityLivingBase entity;
	private final BooleanValue legit = new BooleanValue("Legit", this, false);
	private final BooleanValue releaseOnHit = new BooleanValue("Release on hit", this, true, () -> !legit.getValue());
	private final NumberValue delay = new NumberValue("Delay", this, 400.0D, 0.0D, 1000.0D, 10.0D);
	private final NumberValue hitRange = new NumberValue("Hit Range", this, 3.0D, 0.0D, 10.0D, 0.1D);
	private final BooleanValue onlyIfNeed = new BooleanValue("Only If Need", this, true);
	private final BooleanValue esp = new BooleanValue("ESP", this, true);
	private final StopWatch timer = new StopWatch();
	
	@Override
	public void onEnable() {
		incomingPackets.clear();
		outgoingPackets.clear();
	}

	@EventLink
	public final Listener<PacketSendEvent> onSend = event -> {
		if (mc.player != null && mc.world != null && mc.getNetHandler().getNetworkManager().getNetHandler() != null) {
			if (getModule(Scaffold.class).isEnabled()) {
				outgoingPackets.clear();
			} else {
				entity = null;

				if (mc.world != null && lastWorld != mc.world) {
					resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
					lastWorld = mc.world;
				} else {
					if (entity != null && (!onlyIfNeed.getValue() || !(mc.player.getDistanceToEntity(entity) < 3.0F))) {
						addOutgoingPackets(event.getPacket(), event);
					} else {
						resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
					}

				}
			}
		} else {
			outgoingPackets.clear();
		}
	};

	@SuppressWarnings("unused")
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		if (mc.player != null && mc.world != null && mc.getNetHandler().getNetworkManager().getNetHandler() != null) {
			if (getModule(Scaffold.class).isEnabled()) {
				incomingPackets.clear();
			} else {
				Entity entity;
				EntityLivingBase entityLivingBase;
				if (event.getPacket() instanceof S14PacketEntity) {
					S14PacketEntity packet = (S14PacketEntity) event.getPacket();
					entity = mc.world.getEntityByID(packet.entityId);
					if (entity instanceof EntityLivingBase) {
						entityLivingBase = (EntityLivingBase) entity;
						entityLivingBase.realPosX += (double) packet.getPosX();
						entityLivingBase.realPosY += (double) packet.getPosY();
						entityLivingBase.realPosZ += (double) packet.getPosZ();
					}
				}

				if (event.getPacket() instanceof S18PacketEntityTeleport) {
					S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
					entity = mc.world.getEntityByID(packet.getEntityId());
					if (entity instanceof EntityLivingBase) {
						entityLivingBase = (EntityLivingBase) entity;
						entityLivingBase.realPosX = (int) packet.getX();
						entityLivingBase.realPosY = (int) packet.getY();
						entityLivingBase.realPosZ = (int) packet.getZ();
					}
				}

				entity = null;

				if (mc.world != null && lastWorld != mc.world) {
					resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
					lastWorld = mc.world;
				} else {
					if (entity != null && (!onlyIfNeed.getValue() || !(mc.player.getDistanceToEntity(entity) < 3.0F))) {
						addIncomingPackets(event.getPacket(), event);
					} else {
						resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
					}

				}
			}
		} else {
			incomingPackets.clear();
		}
	};

	@EventLink
	public final Listener<GameEvent> onGame = event -> {
		if (entity != null && entity.getEntityBoundingBox() != null && mc.player != null && mc.world != null && entity.realPosX != 0.0D && entity.realPosY != 0.0D && entity.realPosZ != 0.0D && entity.width != 0.0F && entity.height != 0.0F && entity.posX != 0.0D && entity.posY != 0.0D && entity.posZ != 0.0D) {
			double realX = entity.realPosX / 32.0D;
			double realY = entity.realPosY / 32.0D;
			double realZ = entity.realPosZ / 32.0D;
			if (!onlyIfNeed.getValue()) {
				if (mc.player.getDistanceToEntity(entity) > 3.0F && mc.player.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.player.getDistance(realX, realY, realZ)) {
					resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
					resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
				}
			} else if (mc.player.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.player.getDistance(realX, realY, realZ) || mc.player.getDistance(realX, realY, realZ) < mc.player.getDistance(lastRealX, lastRealY, lastRealZ)) {
				resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
				resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
			}

			if (legit.getValue() && releaseOnHit.getValue() && entity.hurtTime <= 1) {
				resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
				resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
			}

			if (mc.player.getDistance(realX, realY, realZ) > hitRange.getValue().doubleValue() || timer.elapse(delay.getValue().doubleValue(), true)) {
				resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
				resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
			}

			lastRealX = realX;
			lastRealY = realY;
			lastRealZ = realZ;
		}
	};

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (entity != null && entity.getEntityBoundingBox() != null && mc.player != null && mc.world != null && entity.realPosX != 0.0D && entity.realPosY != 0.0D && entity.realPosZ != 0.0D && entity.width != 0.0F && entity.height != 0.0F && entity.posX != 0.0D && entity.posY != 0.0D && entity.posZ != 0.0D && esp.getValue()) {
			boolean render = true;
			double realX = entity.realPosX / 32.0D;
			double realY = entity.realPosY / 32.0D;
			double realZ = entity.realPosZ / 32.0D;
			if (!onlyIfNeed.getValue()) {
				if (mc.player.getDistanceToEntity(entity) > 3.0F && mc.player.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.player.getDistance(realX, realY, realZ)) {
					render = false;
				}
			} else if (mc.player.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.player.getDistance(realX, realY, realZ) || mc.player.getDistance(realX, realY, realZ) < mc.player.getDistance(lastRealX, lastRealY, lastRealZ)) {
				render = false;
			}

			if (legit.getValue() && releaseOnHit.getValue() && entity.hurtTime <= 1) {
				render = false;
			}

			if (mc.player.getDistance(realX, realY, realZ) > hitRange.getValue().doubleValue() || timer.elapse(delay.getValue().doubleValue(), false)) {
				render = false;
			}

			if (entity != null && entity != mc.player && !entity.isInvisible() && render) {
				if (entity == null || entity.width == 0.0F || entity.height == 0.0F) {
					return;
				}
				
				EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.world, new GameProfile(EntityPlayer.getUUID(mc.player.getGameProfile()), ""));
				entityOtherPlayerMP.setPosition(entity.realPosX / 32.0D, entity.realPosY / 32.0D, entity.realPosZ / 32.0D);
				entityOtherPlayerMP.inventory = ((EntityOtherPlayerMP)entity).inventory;
				entityOtherPlayerMP.inventoryContainer = ((EntityOtherPlayerMP)entity).inventoryContainer;
				entityOtherPlayerMP.rotationYawHead = entity.rotationYawHead;
				entityOtherPlayerMP.rotationYaw = entity.rotationYaw;
				entityOtherPlayerMP.rotationPitch = entity.rotationPitch;
				mc.world.addEntityToWorld(-42069, entityOtherPlayerMP);
			}
		}
	};
	
	private void resetIncomingPackets(INetHandler netHandler) {
		if (incomingPackets.size() > 0) {
			while (true) {
				if (incomingPackets.size() == 0) {
					timer.reset();
					break;
				}

				try {
					((Packet) incomingPackets.get(0)).processPacket(netHandler);
				} catch (Exception var3) {
				}

				incomingPackets.remove(incomingPackets.get(0));
			}
		}

	}

	private void addIncomingPackets(Packet packet, CancellableEvent event) {
		if (event != null && packet != null) {
			synchronized (incomingPackets) {
				if (blockPacketIncoming(packet)) {
					incomingPackets.add(packet);
					event.setCancelled(true);
				}

			}
		}
	}

	private void resetOutgoingPackets(INetHandler netHandler) {
		if (outgoingPackets.size() > 0) {
			while (true) {
				if (outgoingPackets.size() == 0) {
					timer.reset();
					break;
				}

				try {
					PacketUtil.sendNoEvent((Packet) outgoingPackets.get(0));
				} catch (Exception var3) {
				}

				outgoingPackets.remove(outgoingPackets.get(0));
			}
		}

	}

	private void addOutgoingPackets(Packet packet, CancellableEvent event) {
		if (event != null && packet != null) {
			synchronized (outgoingPackets) {
				if (blockPacketsOutgoing(packet)) {
					outgoingPackets.add(packet);
					event.setCancelled(true);
				}

			}
		}
	}

	private boolean isEntityPacket(Packet packet) {
		return packet instanceof S14PacketEntity || packet instanceof S19PacketEntityHeadLook
				|| packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
	}

	private boolean blockPacketIncoming(Packet packet) {
		return packet instanceof S03PacketTimeUpdate || packet instanceof S00PacketKeepAlive
				|| packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion
				|| packet instanceof S32PacketConfirmTransaction || packet instanceof S08PacketPlayerPosLook
				|| packet instanceof S01PacketPong || isEntityPacket(packet);
	}

	private boolean blockPacketsOutgoing(Packet packet) {
		if (!legit.getValue()) {
			return false;
		} else {
			return packet instanceof C03PacketPlayer || packet instanceof C02PacketUseEntity
					|| packet instanceof C0FPacketConfirmTransaction || packet instanceof C08PacketPlayerBlockPlacement
					|| packet instanceof C09PacketHeldItemChange || packet instanceof C07PacketPlayerDigging
					|| packet instanceof C0APacketAnimation || packet instanceof C01PacketPing
					|| packet instanceof C00PacketKeepAlive || packet instanceof C0BPacketEntityAction;
		}
	}
}