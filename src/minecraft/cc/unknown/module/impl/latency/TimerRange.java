package cc.unknown.module.impl.latency;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Timer Range", description = ">:3c", category = Category.LATENCY)
public class TimerRange extends Module {
	
	private NumberValue range = new NumberValue("Range", this, 3.5f, 1f, 5f, 0.05);
	private NumberValue positiveTimer = new NumberValue("Positive Timer", this, 1.5, 0.1, 35, 0.1);
	private NumberValue positiveTicks = new NumberValue("Positive Ticks", this, 10, 1, 20, 1);

	private NumberValue negativeTimer = new NumberValue("Negative Timer",this,  0.45, 0.5, 5, 0.5);
	private NumberValue negativeTick = new NumberValue("Negative Ticks", this, 10, 1, 50, 1);

	private BoundsNumberValue timerBoost = new BoundsNumberValue("Timer Boost", this, 0.5, 0.56, 0.1, 1, 0.1);
	private BoundsNumberValue timerCharged = new BoundsNumberValue("Timer Charged", this, 0.75, 0.91, 0.1, 1, 0.1);

	private BooleanValue ignoreTeams = new BooleanValue("Ignore Teams", this, false);
	private BooleanValue onlyGround = new BooleanValue("Only on Ground", this, false);
	private BooleanValue onlyForward = new BooleanValue("Only Forward", this, true);
	
	private int playerTicks = 0;
	private int cooldownTick = 0;
	private boolean confirmAttack = false;
	private boolean confirmKnockback = false;
	private EntityPlayer target;

	@Override
	public void onEnable() {
		mc.timer.timerSpeed = 1f;
	}

	@Override
	public void onDisable() {
		reset();
	}
    
	@EventLink
	public final Listener<WorldChangeEvent> onRender3D = event -> reset();
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (target == null) {
			return;
		}

		float timerboost = MathUtil.nextSecure(timerBoost.getValue().floatValue(), timerBoost.getSecondValue().floatValue()).floatValue();
		float charged = MathUtil.nextSecure(timerCharged.getValue().floatValue(), timerCharged.getSecondValue().floatValue()).floatValue();
		double predictX = mc.player.posX + ((mc.player.posX - mc.player.lastTickPosX) * 2);
		double predictZ = mc.player.posZ + ((mc.player.posZ - mc.player.lastTickPosZ) * 2);
		float f = (float) (predictX - target.posX);
		float f1 = (float) (mc.player.posY - target.posY);
		float f2 = (float) (predictZ - target.posZ);
		double predictedDistance = MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);

		if (playerTicks <= 0) {
			mc.timer.timerSpeed = 1f;
			return;
		}

		if (PlayerUtil.isTeam(target) && ignoreTeams.getValue()) {
			mc.timer.timerSpeed = 1f;
			return;
		}

		if ((mc.player.moveStrafing > 0.08 || !mc.gameSettings.keyBindForward.pressed || predictedDistance > mc.player.getDistanceToEntity(target) + 0.08) && onlyForward.getValue()) {
			mc.timer.timerSpeed = 1f;
			return;
		}

		int tickProgress = playerTicks / positiveTicks.getValue().intValue();
		float playerSpeed;

		if (tickProgress < timerboost)
			playerSpeed = positiveTimer.getValue().floatValue();
		else if (tickProgress < charged)
			playerSpeed = negativeTimer.getValue().floatValue();
		else
			playerSpeed = 1f;

		float speedAdjustment = (float) (playerSpeed >= 0 ? playerSpeed : 1f + positiveTicks.getValue().intValue() - playerTicks);
		float adjustedTimerSpeed = Math.max(speedAdjustment, 0f);

		mc.timer.timerSpeed = adjustedTimerSpeed;

		playerTicks--;
	};
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		if (event.getTarget() != null && event.getTarget() instanceof EntityPlayer) {
			target = (EntityPlayer) event.getTarget();
		}
	};
	
	@EventLink
	public final Listener<PacketSendEvent> onSend = event -> {
		Packet packet = event.getPacket();

		if (packet instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
			if (wrapper.getAction() != C02PacketUseEntity.Action.ATTACK && playerTicks >= 1) {
				mc.timer.timerSpeed = 1f;
				return;
			} else {
				confirmAttack = true;
			}

			double entityDistance = RotationUtil.getDistanceToEntityBox(target);

			cooldownTick++;

			boolean shouldSlowed = cooldownTick >= negativeTick.getValue().intValue() && entityDistance <= range.getValue().floatValue();

			if (shouldSlowed && confirmAttack) {
				confirmAttack = false;
				playerTicks = positiveTicks.getValue().intValue();

				confirmKnockback = true;
				cooldownTick = 0;
			} else {
				mc.timer.timerSpeed = 1f;
			}
		}
	};
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (MoveUtil.isMoving() && !shouldResetTimer() && (mc.timer.timerSpeed > 1.0 || mc.timer.timerSpeed < 1.0)) {
			if (confirmKnockback) {
				if (mc.player.hurtTime > 0) {
					if (mc.player.motionY > 0 && (mc.player.motionX != 0) || mc.player.motionZ != 0) {
						confirmKnockback = false;
						mc.timer.timerSpeed = 1f;
					}
				}
			}
		}
	};
	
	private boolean shouldResetTimer() {
		return (playerTicks >= 1 || mc.player.isSpectator() || mc.player.isDead || mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb || mc.player.isOnLadder() || mc.player.isRiding());
	}
	
    private void reset() {
		mc.timer.timerSpeed = 1f;
		cooldownTick = 0;
		playerTicks = 0;
		confirmAttack = false;
		confirmKnockback = false;
    }
 }
