package cc.unknown.module.impl.latency;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.MoveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.TimerManipulationEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.SimulatedPlayer;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Tick Base", description = "Congela el juego", category = Category.LATENCY)
public class TickBase extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Future"))
			.add(new SubMode("Past"))
			.setDefault("Future");
	
    public final NumberValue delay = new NumberValue("Delay", this, 50, 0, 1000, 50);
    public final BoundsNumberValue activeRange = new BoundsNumberValue("Active Range", this, 3f, 7f, 0.1f, 7f, 0.1f);
    public final NumberValue ticks = new NumberValue("Ticks", this, 4, 1, 20, 1);
    public final BooleanValue displayPredictPos = new BooleanValue("Dislay Predict Pos", this, false);
    public final BooleanValue check = new BooleanValue("Check", this, false);

	private StopWatch timer = new StopWatch();
	
    public int skippedTick = 0;
    private long shifted, previousTime;
    public boolean working;
    private boolean firstAnimation = true;
    public final List<PredictProcess> predictProcesses = new ArrayList<>();
    public EntityPlayer target;

    @Override
    public void onEnable() {
        shifted = 0;
        previousTime = 0;
    }
    
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
        if(displayPredictPos.getValue()) {
            double x = predictProcesses.get(predictProcesses.size() - 1).position.xCoord - mc.getRenderManager().viewerPosX;
            double y = predictProcesses.get(predictProcesses.size() - 1).position.yCoord - mc.getRenderManager().viewerPosY;
            double z = predictProcesses.get(predictProcesses.size() - 1).position.zCoord - mc.getRenderManager().viewerPosZ;
            AxisAlignedBB box = mc.player.getEntityBoundingBox().expand(0.1D, 0.1, 0.1);
            AxisAlignedBB axis = new AxisAlignedBB(box.minX - mc.player.posX + x, box.minY - mc.player.posY + y, box.minZ - mc.player.posZ + z, box.maxX - mc.player.posX + x, box.maxY - mc.player.posY + y, box.maxZ - mc.player.posZ + z);
            RenderUtil.drawSelectionBoundingBox(axis, new Color(50, 255, 255, 150).getRGB());
        }
	};

	@EventLink
	public final Listener<MoveEvent> onMove = event -> {
        predictProcesses.clear();

        SimulatedPlayer simulatedPlayer = SimulatedPlayer.fromClientPlayer(mc.player.movementInput);

        simulatedPlayer.rotationYaw = RotationHandler.lastRotations != null ? RotationHandler.rotations.x : mc.player.rotationYaw;

        for (int i = 0; i < (skippedTick != 0 ? skippedTick : ticks.getValue().intValue()); i++) {
            simulatedPlayer.tick();
            predictProcesses.add(new PredictProcess(
                    simulatedPlayer.getPos(),
                    simulatedPlayer.fallDistance,
                    simulatedPlayer.onGround,
                    simulatedPlayer.isCollidedHorizontally
            ));
        }
	};
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		 target = (EntityPlayer) TargetUtil.getTarget(activeRange.getSecondValue().floatValue() * 3);
	};
	
	@EventLink
	public final Listener<TimerManipulationEvent> onTimerManipulation = event -> {
        if (mode.is("Past")) {

            if (target == null || predictProcesses.isEmpty() || shouldStop()) {
                return;
            }

            if (shouldStart() && timer.finished(delay.getValue().intValue())) {
                shifted += event.getTime() - previousTime;
            }

            if (shifted >= ticks.getValue().intValue() * (1000 / 20f)) {
                shifted = 0;
                timer.reset();
            }

            previousTime = event.getTime();
            event.setTime(event.getTime() - shifted);
        }
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mode.is("Future")) {

            if (target == null || predictProcesses.isEmpty() || shouldStop()) {
                return;
            }

            if(timer.finished(delay.getValue().intValue())) {
                if (shouldStart()) {
                    firstAnimation = false;
                    while (skippedTick <= ticks.getValue().intValue() && !shouldStop()) {
                        ++skippedTick;
                        try {
                            mc.runTick();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    timer.reset();
                }
            }
            working = false;
        }
	};
	
	public boolean shouldStart() {
	    int index = ticks.getValue().intValue() - 1;

	    if (index < 0 || index >= predictProcesses.size()) {
	        System.err.println("Invalid index: " + index + ", predictProcesses size: " + predictProcesses.size());
	        return false;
	    }

	    PredictProcess predictedProcess = predictProcesses.get(index);

	    boolean isWithinDistance = predictedProcess.position.distanceTo(target.getPositionVector()) <
	                               mc.player.getPositionVector().distanceTo(target.getPositionVector());

	    boolean isWithinActiveRange = MathUtil.inBetween(
	        activeRange.getValue().intValue(),
	        activeRange.getSecondValue().intValue(),
	        predictedProcess.position.distanceTo(target.getPositionVector())
	    );

	    boolean canSeeEachOther = mc.player.canEntityBeSeen(target) && target.canEntityBeSeen(mc.player);

	    boolean rotationCheck = (!check.getValue()) || (RotationUtil.getRotationDifference(mc.player, target) <= 90 && check.getValue());

	    boolean isNotCollided = !predictedProcess.isCollidedHorizontally;

	    return isWithinDistance && isWithinActiveRange && canSeeEachOther && rotationCheck && isNotCollided;
	}

    public boolean shouldStop(){
        return mc.player.hurtTime != 0;
    }

    public boolean handleTick() {
        if (mode.is("Future")) {
            if (working || skippedTick < 0) return true;
            if (isEnabled() && skippedTick > 0) {
                --skippedTick;
                return true;
            }
        }
        return false;
    }

    public boolean freezeAnim(){
        if (skippedTick != 0) {
            if (!firstAnimation) {
                firstAnimation = true;
                return false;
            }
            return true;
        }
        return false;
    }
	
    public static class PredictProcess {
        private final Vec3 position;
        private final float fallDistance;
        private final boolean onGround;
        private final boolean isCollidedHorizontally;

        public PredictProcess(Vec3 position, float fallDistance, boolean onGround, boolean isCollidedHorizontally) {
            this.position = position;
            this.fallDistance = fallDistance;
            this.onGround = onGround;
            this.isCollidedHorizontally = isCollidedHorizontally;
        }
    }
 }
