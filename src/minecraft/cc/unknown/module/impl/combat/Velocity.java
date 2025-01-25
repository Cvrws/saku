package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Velocity", description = "Modifica tu kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Hypixel"))
			.add(new SubMode("Legit Prediction"))
			.add(new SubMode("Intave Reduce"))
			.add(new SubMode("Universocraft"))
			.setDefault("Hypixel");

	private final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel"));
	private final NumberValue vertical = new NumberValue("Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel"));

	private final BooleanValue delay = new BooleanValue("Delay", this, false, () -> !mode.is("Hypixel"));
	private final NumberValue delayHorizontal = new NumberValue("Delayed Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	private final NumberValue delayVertical = new NumberValue("Delayed Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	
	private final BooleanValue onlyAir = new BooleanValue("Only in Air", this, false, () -> !mode.is("Hypixel"));
	private final BooleanValue onExplode = new BooleanValue("Explosion Ignore", this, false, () -> !mode.is("Hypixel"));
	
	private final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
	private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump ", this, true);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	private int ticks;
	private int counter;
	private boolean s12 = false;
	private double motionY, motionX, motionZ;
	private EntityLivingBase target;
	
	@Override
	public void onDisable() {
		counter = 0;
	}
	
	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		if (mode.is("Legit Prediction")) {
	        if (target != null && mc.player.hurtTime > 0) {
	        	ArrayList<Vec3> vec3s = new ArrayList<>();
	            HashMap<Vec3, Integer> map = new HashMap<>();
	            Vec3 playerPos = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
	            Vec3 onlyForward = PlayerUtil.getPredictedPos(1.0F, 0.0F).add(playerPos);
	            Vec3 strafeLeft = PlayerUtil.getPredictedPos(1.0F, 1.0F).add(playerPos);
	            Vec3 strafeRight = PlayerUtil.getPredictedPos(1.0F, -1.0F).add(playerPos);
	            map.put(onlyForward, 0);
	            map.put(strafeLeft, 1);
	            map.put(strafeRight, -1);
	            vec3s.add(onlyForward);
	            vec3s.add(strafeLeft);
	            vec3s.add(strafeRight);
	            Vec3 targetVec = new Vec3(target.posX, target.posY, target.posZ);
	            vec3s.sort(Comparator.comparingDouble(targetVec::distanceXZTo));
	            if (!event.isSneak()) {
	                event.setStrafe(map.get(vec3s.get(0)));
	                mc.player.moveStrafing = map.get(vec3s.get(0));
	            }
	        }
	        target = null;
		}
		
		if (mode.is("Intave Reduce")) {
            if (mc.player.hurtTime == 9 && mc.player.onGround && counter++ % 2 == 0) {
                event.setJump(true);
            }
		}
	};
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		if (mode.is("Legit Prediction")) {
			if (event.getTarget() != null && event.getTarget() instanceof EntityLivingBase) {
				target = (EntityLivingBase) event.getTarget();
			}
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
		if (event.isCancelled()) return;
		final Packet<?> packet = event.getPacket();

	
		if (mode.is("Hypixel")) {
			if (mc.player.onGround && onlyAir.getValue()) return;
		
			final double horizontal = this.horizontal.getValue().doubleValue();
			final double vertical = this.vertical.getValue().doubleValue();
			final boolean onExplode = this.onExplode.getValue();
	
			if (packet instanceof S12PacketEntityVelocity) {
	
				final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
	
				if (wrapper.getEntityID() == mc.player.getEntityId()) {
	
					if (horizontal == 0) {
						if (vertical != 0 && !event.isCancelled()) {
	
							mc.player.motionY = wrapper.getMotionY() / 8000.0D;
						}
	
						event.setCancelled();
						return;
					}
	
					wrapper.motionX *= horizontal / 100;
					wrapper.motionY *= vertical / 100;
					wrapper.motionZ *= horizontal / 100;
	
					event.setPacket(wrapper);
	
				}
			} else if (packet instanceof S27PacketExplosion) {
				final S27PacketExplosion wrapper = (S27PacketExplosion) packet;
	
				if (onExplode) {
					event.setCancelled();
					return;
				}
	
				wrapper.posX *= horizontal / 100;
				wrapper.posY *= vertical / 100;
				wrapper.posZ *= horizontal / 100;
	
				event.setPacket(wrapper);
			}
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    double chanceValue = chance.getValue().doubleValue();
	    double randomFactor = MathUtil.getRandomFactor(chanceValue);

        if (noAction()) return;
	    if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
	    
		if (mode.is("Hypixel")) {
			if (delay.getValue()) {
				ticks++;
		
				if (mc.player.hurtTime == 9) {
					ticks = 0;
				}
		
				assert mc.player != null;
		
				if (mc.player.hurtTime == 9) {
					motionX = mc.player.motionX;
					motionY = mc.player.motionY;
					motionZ = mc.player.motionZ;
				}
		
				final double horizontal = this.delayHorizontal.getValue().doubleValue();
				final double vertical = this.delayVertical.getValue().doubleValue();
		
				if (mc.player.hurtTime == 8) {
					mc.player.motionX *= horizontal / 100;
					mc.player.motionY *= vertical / 100;
					mc.player.motionZ *= horizontal / 100;
				}
			}
		}
	};
	
    private boolean checks() {
        return Stream.<Supplier<Boolean>>of(mc.player::isInLava, mc.player::isBurning, mc.player::isInWater, () -> mc.player.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
    }
    
    private boolean noAction() {
        return mc.player.getActivePotionEffects().parallelStream().anyMatch(effect -> notWhileSpeed.getValue() && effect.getPotionID() == Potion.moveSpeed.getId() || notWhileJumpBoost.getValue() && effect.getPotionID() == Potion.jump.getId());
    }
}