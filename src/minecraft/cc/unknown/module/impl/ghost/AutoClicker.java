package cc.unknown.module.impl.ghost;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clickea automáticamente", category = Category.GHOST)
public class AutoClicker extends Module {

	private final ModeValue randomization = new ModeValue("Randomization", this) {
		{
			add(new SubMode("Normal"));
			add(new SubMode("ButterFly"));
			add(new SubMode("Drag"));
			add(new SubMode("Smart"));
			setDefault("Normal");
		}
	};

	private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 8, 14, 1, 20, 1, () -> randomization.is("Smart"));

	private final BoundsNumberValue cpsAir = new BoundsNumberValue("CPS [Air]", this, 5, 10, 1, 20, 1, () -> !randomization.is("Smart"));
	private final BoundsNumberValue recalculateTickDelayAir = new BoundsNumberValue("Recalculate tick delay [Air]", this, 10, 20, 1, 50, 1, () -> !randomization.is("Smart"));
	private final BooleanValue notInArrow = new BooleanValue("Not in a row [Air]", this, true, () -> !randomization.is("Smart"));
	private final BoundsNumberValue dcpsAir = new BoundsNumberValue("DCPS [Air]", this, 0, 0, 0, 10, 1, () -> !randomization.is("Smart"));
	private final BoundsNumberValue recalculateDCPSDelayAir = new BoundsNumberValue("DCPS Recalculate tick delay [Air]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	
	private final DescValue separator = new DescValue(" ", this, () -> !randomization.is("Smart"));
	
    private final BoundsNumberValue cpsWall = new BoundsNumberValue("CPS [Wall]", this, 9, 11, 1, 20, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateTickDelayWall = new BoundsNumberValue("Recalculate tick delay [Wall]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	private final BooleanValue notInArrow2 = new BooleanValue("Not in a row [Wall]", this, true, () -> !randomization.is("Smart"));
    private final BoundsNumberValue dcpsWall = new BoundsNumberValue("DCPS [Wall]", this, 4, 5, 0, 10, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateDCPSDelayWall = new BoundsNumberValue("DCPS Recalculate tick delay [Wall]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	
    private final DescValue separator3 = new DescValue(" ", this, () -> !randomization.is("Smart"));
    
    private final BoundsNumberValue cpsTarget = new BoundsNumberValue("CPS [Target]", this, 16, 18, 1, 20, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateTickDelayTarget = new BoundsNumberValue("Recalculate tick delay [Target]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	private final BooleanValue notInArrow3 = new BooleanValue("Not in a row [Target]", this, true, () -> !randomization.is("Smart"));
    private final BoundsNumberValue dcpsTarget = new BoundsNumberValue("DCPS [Target]", this, 4, 5, 0, 10, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateDCPSDelayTarget = new BoundsNumberValue("DCPS Recalculate tick delay [Target]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    
    private final DescValue separator4 = new DescValue(" ", this, () -> !randomization.is("Smart"));
    
    private final BoundsNumberValue cpsFirst = new BoundsNumberValue("CPS [First Hit]", this, 18, 18, 1, 20, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateTickDelayFirst = new BoundsNumberValue("Recalculate tick delay [First Hit]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    private final BooleanValue notInArrow4 = new BooleanValue("Not in a row [First Hit]", this, true, () -> !randomization.is("Smart"));
    private final BoundsNumberValue dcpsFirst = new BoundsNumberValue("DCPS [First Hit]", this, 4, 5, 0, 10, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateDCPSDelayFirst = new BoundsNumberValue("DCPS Recalculate tick delay [First Hit]", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    
    private final DescValue separator5 = new DescValue(" ", this, () -> !randomization.is("Smart"));
    
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true);
	private final BooleanValue rightClick = new BooleanValue("Right Click", this, false);
    private final BooleanValue attackFriends = new BooleanValue("Attack Friends", this, false);

	private final StopWatch stopWatch = new StopWatch();
	private final Set<Integer> hitEntities = new HashSet<>();

	private int ticksDown;
	private int attackTicks;
	private long nextSwing;
	private long clicks;
    private int recalculateDelay;
    private int recalculateDCPSDelay;
    
    private long lastCpsAir = -1;
    private long lastCpsWall = -1;
    private long lastCpsTarget = -1;
    private long lastCpsFirstHit = -1;
        
	private Entity target;

	@EventLink
	public final Listener<NaturalPressEvent> onTick = event -> {
	    attackTicks++;
	    
	    HitSelect hitSelect = Sakura.instance.getModuleManager().get(HitSelect.class);
	    
	    if (!stopWatch.finished(nextSwing) || (hitSelect != null && hitSelect.isEnabled() && attackTicks < 10 && mc.player != null && mc.player.hurtTime == 0)) {
	        return;
	    }

	    if (mc.currentScreen != null) {
	        return;
	    }

	    if (Mouse.isButtonDown(0)) {
	        ticksDown++;
	    } else {
	        ticksDown = 0;
	    }

	    if (!attackFriends.getValue() && target instanceof EntityPlayer && FriendUtil.isFriend((EntityPlayer) target)) {
	        return;
	    }

	    clicks = (long) (this.cps.getRandomBetween().longValue() * 1.5);
	    
	    switch (randomization.getValue().getName()) {
	        case "Normal":
	            nextSwing = 1000 / clicks;
	            break;

	        case "ButterFly":
	            nextSwing = Math.random() < 0.5 ? (long) (Math.random() * 100) : 1000 / clicks;
	            break;

	        case "Drag":
	            double fluctuation = Math.random() * 10 - 5;
	            boolean pause = Math.random() < 0.05;
	            nextSwing = (long) (pause ? (15 + 50 + fluctuation) : (15 + fluctuation));
	            break;

	        case "Smart":
	            boolean ground = mc.player.onGround;
	            boolean hittingTarget = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY;
	            boolean firstHit = isFirstHit(mc.objectMouseOver);
	            boolean hittingWall = isHittingWall();

	            if (firstHit) {
	                clicks = getNewCps(cpsFirst, lastCpsFirstHit, notInArrow4);
	                lastCpsFirstHit = clicks;
	            } else if (hittingTarget) {
	                clicks = getNewCps(cpsTarget, lastCpsTarget, notInArrow3);
	                lastCpsTarget = clicks;
	            } else if (hittingWall || ground) {
	                clicks = getNewCps(cpsWall, lastCpsWall, notInArrow2);
	                lastCpsWall = clicks;
	            } else {
	                clicks = getNewCps(cpsAir, lastCpsAir, notInArrow);
	                lastCpsAir = clicks;
	            }

	            nextSwing = 1000 / clicks;

	            if (firstHit) {
	                recalculateDelay = recalculateTickDelayFirst.getValue().intValue();
	                recalculateDCPSDelay = recalculateDCPSDelayFirst.getValue().intValue();
	            } else if (hittingTarget) {
	                recalculateDelay = recalculateTickDelayTarget.getValue().intValue();
	                recalculateDCPSDelay = recalculateDCPSDelayTarget.getValue().intValue();
	            } else if (hittingWall) {
	                recalculateDelay = recalculateTickDelayWall.getValue().intValue();
	                recalculateDCPSDelay = recalculateDCPSDelayWall.getValue().intValue();
	            } else {
	                recalculateDelay = recalculateTickDelayAir.getValue().intValue();
	                recalculateDCPSDelay = recalculateDCPSDelayAir.getValue().intValue();
	            }

	            for (int i = 0; i < recalculateDelay; i++) {
	                nextSwing = 1000 / (clicks + i);
	            }

	            int dcpsValue;
	            if (firstHit) {
	                dcpsValue = dcpsFirst.getValue().intValue();
	            } else if (hittingTarget) {
	                dcpsValue = dcpsTarget.getValue().intValue();
	            } else if (hittingWall) {
	                dcpsValue = dcpsWall.getValue().intValue();
	            } else {
	                dcpsValue = dcpsAir.getValue().intValue();
	            }

	            for (int i = 0; i < recalculateDCPSDelay; i++) {
	                nextSwing = 1000 / (clicks + dcpsValue + i);
	            }

	            break;
	    }
	    
		if (Mouse.isButtonDown(0) && ticksDown > 1) {
			mc.clickMouse();
			stopWatch.reset();
			event.setCancelled();
		}
		
		if (Mouse.isButtonDown(0) && !breakBlocks.getValue()) {
			mc.playerController.curBlockDamageMP = 0;
		}
		
		if (rightClick.getValue() && Mouse.isButtonDown(1)) {
			mc.rightClickMouse();
			stopWatch.reset();
			event.setCancelled();

			if (Math.random() > 0.9) {
				mc.rightClickMouse();
				stopWatch.reset();
				event.setCancelled();
			}
		}
	    
		if (isBlocking() && Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
			mc.playerController.onStoppedUsingItem(mc.player);
		} else if (!isBlocking() && Mouse.isButtonDown(0) && Mouse.isButtonDown(1)) {
			mc.playerController.sendUseItem(mc.player, mc.world, mc.player.getHeldItem());
		}
	};
	
	private boolean isBlocking() {
		return mc.player.isBlocking() && PlayerUtil.isHoldingWeapon();
	}
	
	private boolean isFirstHit(MovingObjectPosition objectMouseOver) {
	    return objectMouseOver != null && objectMouseOver.entityHit != null && !hasHitBefore(objectMouseOver.entityHit);
	}

	private boolean isHittingWall() {
	    return mc.playerController.getIsHittingBlock();
	}
	
	private boolean hasHitBefore(Entity target) {
	    if (target == null) return false;
	    
	    int entityId = target.getEntityId();
	    
	    if (hitEntities.contains(entityId)) {
	        return true;
	    }

	    hitEntities.add(entityId);
	    return false;
	}

    private long getNewCps(BoundsNumberValue cpsValue, long lastCps, BooleanValue notInRow) {
        long newCps = cpsValue.getRandomBetween().longValue();
        if (notInRow.getValue()) {
            while (newCps == lastCps) {
                newCps = cpsValue.getRandomBetween().longValue();
            }
        }
        return newCps;
    }

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		attackTicks = 0;
		target = (Entity) event.getTarget();
	};
}