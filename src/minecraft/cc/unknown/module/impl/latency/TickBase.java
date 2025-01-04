package cc.unknown.module.impl.latency;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicates;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.HitEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.geometry.Doble;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.SneakyThrows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.optifine.reflect.Reflector;

@ModuleInfo(aliases = "Tick Base", description = "Congela el juego para acercarte a tu objetivo", category = Category.LATENCY)
public class TickBase extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Tick"))
			.add(new SubMode("Latency"))
			.setDefault("Tick");
	
	private final NumberValue range = new NumberValue("Range", this, 3, 3, 8, 0.1, () -> !mode.is("Packet") && !mode.is("Tick"));
	private final NumberValue lagTime = new NumberValue("Lag Time", this, 50, 0, 500, 10, () -> !mode.is("Tick"));
	private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 2000, 50, () -> !mode.is("Tick") && !mode.is("Latency"));
	private final BooleanValue checkTeams = new BooleanValue("Check Teams", this, false, () -> !mode.is("Tick"));

	private int durationTicks = 0, waitTicks = 0, delayTicks = 0;
	private long lastLagTime = 0;
	
	private LinkedList<Packet> outPackets = new LinkedList();
	private StopWatch timer = new StopWatch();

	@Override
	public void onDisable() {
		 fullRelease();
	}

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame() || !MoveUtil.isMoving() || getModule(Scaffold.class).isEnabled() || getModule(LegitScaffold.class).isEnabled()) return;
		
		if (mode.is("Tick")) {
			tickMode();
		}
	};
	
	@EventLink
	public final Listener<HitEvent> onHit = event -> {
		if (mode.is("Latency")) {
			if (isHurtTime()) {
				event.setForced(true);
				fullRelease();
			}
		}
	};
	
	@EventLink
	public final Listener<PacketSendEvent> onPacketSend = event -> {
		if (mode.is("Latency")) {
			if (shouldCancel() && !event.isCancelled()) {
				event.setCancelled();
				outPackets.add(event.getPacket());
			}
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mode.is("Latency")) {
			if (timer.elapse(delay.getValue().longValue(), false)) {
				fullRelease();
			}
	
			if (mc.player.hurtTime > 0) {
				fullRelease();
			}
		}
	};
	
	@SneakyThrows
	private void tickMode() {
        if (!shouldStart()) {
            return;
        }
        
		Thread.sleep(lagTime.getValue().intValue());
		lastLagTime = System.currentTimeMillis();
	}

    private boolean shouldStart() {
        if (System.currentTimeMillis() - lastLagTime < delay.getValue().longValue()) return false;
        EntityPlayer target = mc.theWorld.playerEntities.stream().filter(p -> p != mc.player).filter(p -> !checkTeams.getValue() || !PlayerUtil.sameTeam(p)).filter(p -> !FriendUtil.isFriend(p)).map(p -> new Doble<>(p, mc.player.getDistanceSqToEntity(p))).min(Comparator.comparing(Doble::getSecond)).map(Doble::getFirst).orElse(null);
        if (target == null) return false;
        double distance = new Vec3(target).distanceTo(mc.player);
        return distance >= 3.0 && distance <= range.getValue().doubleValue();
    }


    private void smartRelease() {
       if (!mc.isSingleplayer()) {
          try {
             while (outPackets.size() > 0) {
                Packet packet = outPackets.poll();
                PacketUtil.sendNoEvent(packet);
                if (packet instanceof C03PacketPlayer) {
                   C03PacketPlayer c03 = (C03PacketPlayer) packet;
                   if (RotationUtil.getDistanceToEntityBoxFromPosition(c03.x, c03.y, c03.z, mc.player) <= 3.0D) {
                      mc.player.setPosition(c03.x, c03.y, c03.z);
                      outPackets.clear();
                      timer.reset();
                   }
                }
             }
          } catch (Exception ignored) {
          }

          outPackets.clear();
          timer.reset();
       }
    }

    private void fullRelease() {
       if (!mc.isSingleplayer()) {
          try {
             while (outPackets.size() > 0) {
                PacketUtil.sendNoEvent(outPackets.poll());
             }
          } catch (Exception ignored) {
          }

          outPackets.clear();
          timer.reset();
       }
    }

    private boolean shouldCancel() {
       return true;
    }

    private boolean isTargetCloseOrVisible() {
       Entity rayTracedEntity = raycast(3, new Vector2f(RotationHandler.lastRotations.x, RotationHandler.lastRotations.y));
       return rayTracedEntity != null && RotationUtil.getDistanceToEntityBox(rayTracedEntity) <= 3.0D;
    }
    
    public boolean isHurtTime() {
        return (getModule(AimAssist.class).target.hurtTime <= 2 && getModule(AimAssist.class).isEnabled()) || (getModule(KillAura.class).target.hurtTime <= 2 && getModule(KillAura.class).isEnabled());
     }
    
    public Entity raycast(double range, final Vector2f rotation) {
        if (mc.objectMouseOver.entityHit != null) {
           return mc.objectMouseOver.entityHit;
        } else {
           Vec3 vec3 = mc.player.getPositionEyes(1.0F);
           Vec3 vec31 = mc.player.getVectorForRotation(rotation.y, rotation.x);
           Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
           Entity pointedEntity = null;
           float f = 1.0F;
           List<?> list = mc.theWorld.getEntitiesInAABBexcluding(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
           double d2 = range;
           Iterator var12 = list.iterator();

           while(true) {
              while(var12.hasNext()) {
                 Object o = var12.next();
                 Entity entity1 = (Entity)o;
                 float f1 = entity1.getCollisionBorderSize();
                 AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                 MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                 if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                       pointedEntity = entity1;
                       d2 = 0.0D;
                    }
                 } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                       boolean flag2 = false;
                       if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                          flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract);
                       }

                       if (entity1 == mc.getRenderViewEntity().ridingEntity && !flag2) {
                          if (d2 == 0.0D) {
                             pointedEntity = entity1;
                          }
                       } else {
                          pointedEntity = entity1;
                          d2 = d3;
                       }
                    }
                 }
              }

              return pointedEntity;
           }
        }
     }
 }
