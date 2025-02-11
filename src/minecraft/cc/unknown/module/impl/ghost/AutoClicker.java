package cc.unknown.module.impl.ghost;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clickea automáticamente", category = Category.GHOST)
public class AutoClicker extends Module {

	private final ModeValue mode = new ModeValue("Randomization", this) {
		{
			add(new SubMode("Normal"));
			add(new SubMode("ButterFly"));
			add(new SubMode("Drag"));
			add(new SubMode("Smart"));
			setDefault("Normal");
		}
	};

	private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 8, 14, 1, 20, 1, () -> mode.is("Smart"));
	
	private final BooleanValue onAir = new BooleanValue("On Air", this, true, () -> !mode.is("Smart"));
	private final BoundsNumberValue cpsAir = new BoundsNumberValue("CPS [Air]", this, 5, 10, 1, 20, 1, () -> !mode.is("Smart") || !onAir.getValue());	
	private final BoundsNumberValue rcAir = new BoundsNumberValue("Recalculate Tick [Air]", this, 10, 20, 1, 20, 1, () -> !mode.is("Smart") || !onAir.getValue());
	private final BoundsNumberValue deltaAir = new BoundsNumberValue("Delta [Air]", this, 3, 8, 1, 20, 1, () -> !mode.is("Smart") || !onAir.getValue());

	private final DescValue separator = new DescValue(" ", this, () -> !mode.is("Smart"));
	
	private final BooleanValue onWall = new BooleanValue("On Wall", this, true, () -> !mode.is("Smart"));
	private final BoundsNumberValue cpsWall = new BoundsNumberValue("CPS [Wall]", this, 9, 11, 1, 20, 1, () -> !mode.is("Smart") || !onWall.getValue());    
	private final BoundsNumberValue rcWall = new BoundsNumberValue("Recalculate Tick [Wall]", this, 3, 8, 1, 20, 1, () -> !mode.is("Smart") || !onWall.getValue());
	private final BoundsNumberValue deltaWall = new BoundsNumberValue("Delta [Wall]", this, 3, 8, 1, 20, 1, () -> !mode.is("Smart") || !onWall.getValue());
	
	private final DescValue separator1 = new DescValue(" ", this, () -> !mode.is("Smart"));
	
	private final BooleanValue firstHit = new BooleanValue("On First Hit", this, true, () -> !mode.is("Smart"));
    private final BoundsNumberValue cpsFirst = new BoundsNumberValue("CPS [First Hit]", this, 16, 18, 1, 20, 1, () -> !mode.is("Smart") || !firstHit.getValue());
	private final BoundsNumberValue rcFirst = new BoundsNumberValue("Recalculate Tick [First Hit]", this, 6, 9, 1, 20, 1, () -> !mode.is("Smart") || !firstHit.getValue());
	private final BoundsNumberValue deltaFirst = new BoundsNumberValue("Delta [First Hit]", this, 4, 5, 1, 20, 1, () -> !mode.is("Smart") || !firstHit.getValue());
	
	private final DescValue separator2 = new DescValue(" ", this, () -> !mode.is("Smart"));

	private final BooleanValue onTarget = new BooleanValue("On Target", this, true, () -> !mode.is("Smart"));
	private final BoundsNumberValue cpsTarget = new BoundsNumberValue("CPS [Target]", this, 18, 18, 1, 20, 1, () -> !mode.is("Smart") || !onTarget.getValue());        
	private final BoundsNumberValue rcTarget = new BoundsNumberValue("Recalculate Tick [Target]", this, 3, 8, 1, 20, 1, () -> !mode.is("Smart") || !onTarget.getValue());
	private final BoundsNumberValue deltaTarget = new BoundsNumberValue("Delta [Target]", this, 3, 8, 1, 20, 1, () -> !mode.is("Smart") || !onTarget.getValue());
	private final BoundsNumberValue dCpsTarget = new BoundsNumberValue("DCPS [Target]", this, 4, 5, 1, 20, 1, () -> !mode.is("Smart") || !onTarget.getValue());        

	private final DescValue separator3 = new DescValue(" ", this, () -> !mode.is("Smart"));
	
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true);
	private final BooleanValue rightClick = new BooleanValue("Right Click", this, false);

	private final StopWatch stopWatch = new StopWatch();
	private final Set<Integer> hitEntities = new HashSet<>();

	private int ticksDown;
	private long nextSwing;
	private long clicks;
	private long ticksSinceLastRecalculation;
	private long baseCPS;

	@EventLink
	public final Listener<TickEvent> onTick = event -> {	    
	    double randomization = 1.5;
	    AutoBlock autoBlock = getModule(AutoBlock.class);
	    String mode = this.mode.getValue().getName();
        
	    if (stopWatch.finished(nextSwing) && mc.currentScreen == null) {
		    if (Mouse.isButtonDown(0)) {
		        ticksDown++;
		    } else {
		        ticksDown = 0;
		    }
		    
		    clicks = (long) (this.cps.getRandomBetween().longValue() * randomization);
		    
		    switch (mode) {
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
		            boolean hittingWall = mc.playerController.isHittingBlock;

		            if (firstHit) {
		                if (ticksSinceLastRecalculation >= rcFirst.getRandomBetween().longValue()) {
		                    baseCPS = cpsFirst.getRandomBetween().longValue();
		                    ticksSinceLastRecalculation = 0;
		                }

		                long delta = deltaFirst.getRandomBetween().longValue();
		                clicks = baseCPS + delta;
		                clicks = Math.max(1, clicks);

		                ticksSinceLastRecalculation++;
		            } else if (hittingTarget) {
		            	
			            for (int i = 0; i < dCpsTarget.getRandomBetween().longValue(); i++) {
			                nextSwing = 1000 / (clicks + i);
			            }
		                
		                if (ticksSinceLastRecalculation >= rcTarget.getRandomBetween().longValue()) {
		                    baseCPS = cpsTarget.getRandomBetween().longValue();
		                    ticksSinceLastRecalculation = 0;
		                }

		                long delta = deltaTarget.getRandomBetween().longValue();
		                clicks = baseCPS + delta;
		                clicks = Math.max(1, clicks);

		                ticksSinceLastRecalculation++;
		            } else if (hittingWall || ground) {
		                if (ticksSinceLastRecalculation >= rcWall.getRandomBetween().longValue()) {
		                    baseCPS = cpsWall.getRandomBetween().longValue();
		                    ticksSinceLastRecalculation = 0;
		                }

		                long delta = deltaWall.getRandomBetween().longValue();
		                clicks = baseCPS + delta;
		                clicks = Math.max(1, clicks);

		                ticksSinceLastRecalculation++;
		            } else if (!ground) {
		                if (ticksSinceLastRecalculation >= rcAir.getRandomBetween().longValue()) {
		                    baseCPS = cpsAir.getRandomBetween().longValue();
		                    ticksSinceLastRecalculation = 0;
		                }

		                long delta = deltaAir.getRandomBetween().longValue();
		                clicks = baseCPS + delta;
		                clicks = Math.max(1, clicks);

		                ticksSinceLastRecalculation++;
		            }

		            nextSwing = 1000 / clicks;
		            break;

		    }
		    
			if (Mouse.isButtonDown(0) && ticksDown > 1) {
				mc.clickMouse();
				stopWatch.reset();
			}
			
			if (Mouse.isButtonDown(1) && rightClick.getValue()) {
				mc.rightClickMouse();
				stopWatch.reset();
			}
			
			if (Mouse.isButtonDown(0) && !breakBlocks.getValue()) {
				mc.playerController.curBlockDamageMP = 0;
			}
	    }
	};
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> mc.leftClickCounter = -1;

	private boolean isFirstHit(MovingObjectPosition objectMouseOver) {
	    return objectMouseOver != null && objectMouseOver.entityHit != null && !hasHitBefore(objectMouseOver.entityHit);
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
	
	private void recalibrateCPS(BoundsNumberValue rc, BoundsNumberValue cps, BoundsNumberValue delta) {
        if (ticksSinceLastRecalculation >= 20 + Math.random() * 10) { 
            baseCPS = (long) (cps.getRandomBetween().longValue() + (Math.random() * delta.getRandomBetween().longValue() - delta.getRandomBetween().longValue() / 2));
            ticksSinceLastRecalculation = 0;
        }
        ticksSinceLastRecalculation++;
	}
}