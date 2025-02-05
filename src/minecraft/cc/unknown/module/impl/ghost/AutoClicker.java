package cc.unknown.module.impl.ghost;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ClickEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

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

	private final BoundsNumberValue cpsAir = new BoundsNumberValue("CPS In Air", this, 5, 10, 1, 20, 1, () -> !randomization.is("Smart"));
	private final BoundsNumberValue recalculateTickDelayAir = new BoundsNumberValue("RTD in Air", this, 10, 20, 1, 50, 1, () -> !randomization.is("Smart"));
	private final BoundsNumberValue dcpsAir = new BoundsNumberValue("DCPS in Air", this, 0, 0, 0, 10, 1, () -> !randomization.is("Smart"));
	private final BoundsNumberValue recalculateDCPSDelayAir = new BoundsNumberValue("DCPS RTD in Air", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	
	private final DescValue separator = new DescValue(" ", this, () -> !randomization.is("Smart"));
	
    private final BoundsNumberValue cpsWall = new BoundsNumberValue("CPS On Wall", this, 9, 11, 1, 20, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateTickDelayWall = new BoundsNumberValue("RTD On Wall", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue dcpsWall = new BoundsNumberValue("DCPS On Wall", this, 4, 5, 0, 10, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateDCPSDelayWall = new BoundsNumberValue("DCPS RTD On Wall", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
	
    private final DescValue separator3 = new DescValue(" ", this, () -> !randomization.is("Smart"));
    
    private final BoundsNumberValue cpsTarget = new BoundsNumberValue("CPS On Target", this, 18, 18, 1, 20, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateTickDelayTarget = new BoundsNumberValue("RTD Target", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue dcpsTarget = new BoundsNumberValue("DCPS On Target", this, 4, 5, 0, 10, 1, () -> !randomization.is("Smart"));
    private final BoundsNumberValue recalculateDCPSDelayTarget = new BoundsNumberValue("DCPS RTD On Target", this, 3, 8, 1, 50, 1, () -> !randomization.is("Smart"));
    
    private final DescValue separator4 = new DescValue(" ", this, () -> !randomization.is("Smart"));
    
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true);
	private final BooleanValue rightClick = new BooleanValue("Right Click", this, false);
    private final BooleanValue perfectHit = new BooleanValue("Perfect Hit", this, false);
    private final BooleanValue parkinson = new BooleanValue("Parkinson", this, false);
    private final BooleanValue attackFriends = new BooleanValue("Attack Friends", this, false);
    private final BooleanValue critFix = new BooleanValue("Crit Fix", this, false);

	private final StopWatch stopWatch = new StopWatch();
	
	private int ticksDown;
	private int attackTicks;
	private long nextSwing;
	private long clicks;
    private int recalculateDelay;
    private int recalculateDCPSDelay;
    
    private double directionX, directionY;
    
	private Entity target;
	
    @EventLink
    public final Listener<ClickEvent> onClick = event -> {
        stopWatch.reset();

        directionX = (Math.random() - 0.5) * 4;
        directionY = (Math.random() - 0.5) * 4;
    };

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
	    attackTicks++;

	    HitSelect hitSelect = Sakura.instance.getModuleManager().get(HitSelect.class);

	    if (!stopWatch.finished(nextSwing) || (hitSelect != null && hitSelect.isEnabled() && attackTicks < 10 && mc.player != null && mc.player.hurtTime == 0)) {
	        return;
	    }

	    if (mc.currentScreen != null) {
	        return;
	    }

	    if (mc.gameSettings.keyBindAttack.isKeyDown()) {
	        ticksDown++;
	    } else {
	        ticksDown = 0;
	    }

	    if (perfectHit.getValue() && attackTicks % 5 != 0) {
	        return;
	    }

	    if (!attackFriends.getValue() && target instanceof EntityPlayer && FriendUtil.isFriend((EntityPlayer) target)) {
	        return;
	    }

	    if (critFix.getValue() && !mc.player.onGround) {
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

	            if (!ground) {
	                clicks = (long) (cpsAir.getRandomBetween().longValue() * 1.5);

	                nextSwing = 1000 / clicks;

	                recalculateDelay = recalculateTickDelayAir.getValue().intValue();

	                for (int i = 0; i < (recalculateDelay + dcpsAir.getValue().intValue()); i++) {
	                    nextSwing = 1000 / (clicks + i + recalculateDelay);
	                }

	                recalculateDCPSDelay = recalculateDCPSDelayAir.getValue().intValue();
	                for (int i = 0; i < recalculateDCPSDelay; i++) {
	                    nextSwing = 1000 / (clicks + dcpsAir.getValue().intValue() + i);
	                }
	            } else if (ground && mc.player.movementInput.moveForward > 0.8) {
	                clicks = (long) (cpsWall.getRandomBetween().longValue() * 1.5);

	                nextSwing = 1000 / clicks;

	                recalculateDelay = recalculateTickDelayWall.getValue().intValue();

	                for (int i = 0; i < (recalculateDelay + dcpsWall.getValue().intValue()); i++) {
	                    nextSwing = 1000 / (clicks + i + recalculateDelay);
	                }

	                recalculateDCPSDelay = recalculateDCPSDelayWall.getValue().intValue();
	                for (int i = 0; i < recalculateDCPSDelay; i++) {
	                    nextSwing = 1000 / (clicks + dcpsWall.getValue().intValue() + i);
	                }
	            } else if (mc.objectMouseOver != null || mc.objectMouseOver.entityHit != null) {
	                clicks = (long) (cpsTarget.getRandomBetween().longValue() * 1.5);

	                nextSwing = 1000 / clicks;

	                recalculateDelay = recalculateTickDelayTarget.getValue().intValue();

	                for (int i = 0; i < (recalculateDelay + dcpsTarget.getValue().intValue()); i++) {
	                    nextSwing = 1000 / (clicks + i + recalculateDelay);
	                }

	                recalculateDCPSDelay = recalculateDCPSDelayTarget.getValue().intValue();
	                for (int i = 0; i < recalculateDCPSDelay; i++) {
	                    nextSwing = 1000 / (clicks + dcpsTarget.getValue().intValue() + i);
	                }
	            }
	            break;
	    }

	    handleClick();

	    stopWatch.reset();
	};

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		attackTicks = 0;
		target = (Entity) event.getTarget();
	};

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        mc.leftClickCounter = -1;

        if (!stopWatch.finished(100) && this.parkinson.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            EntityRenderer.mouseAddedX = (float) (((Math.random() - 0.5) * 400 / mc.getDebugFPS()) * directionX);
            EntityRenderer.mouseAddedY = (float) (((Math.random() - 0.5) * 400 / mc.getDebugFPS()) * directionY);
        }
    };

	private void handleClick() {
		if (ticksDown > 1 && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
			mc.clickMouse();
		}
		
		if (!breakBlocks.getValue()) {
			mc.playerController.curBlockDamageMP = 0;
		}
		
		if (rightClick.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
			mc.rightClickMouse();

			if (Math.random() > 0.9) {
				mc.rightClickMouse();
			}
		}
	}
}