package cc.unknown.module.impl.latency;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.MoveEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.TimerManipulationEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.util.player.prediction.PredictProcess;
import cc.unknown.util.player.prediction.PredictEngine;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(aliases = "Timer Manipulation", description = "Utilia timer para hacer un tp", category = Category.LATENCY)
public class TimerManipulation extends Module {
	
    public final NumberValue delay = new NumberValue("Delay", this, 50, 0, 1000, 50);
    public final NumberValue ticks = new NumberValue("Ticks", this, 4, 1, 40, 1);
    public final BooleanValue displayPredictPos = new BooleanValue("Dislay Predict Pos", this, false);

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
            double x = predictProcesses.get(predictProcesses.size() - 1).getPosition().xCoord - mc.getRenderManager().viewerPosX;
            double y = predictProcesses.get(predictProcesses.size() - 1).getPosition().yCoord - mc.getRenderManager().viewerPosY;
            double z = predictProcesses.get(predictProcesses.size() - 1).getPosition().zCoord - mc.getRenderManager().viewerPosZ;
            AxisAlignedBB box = mc.player.getEntityBoundingBox().expand(0.1D, 0.1, 0.1);
            AxisAlignedBB axis = new AxisAlignedBB(box.minX - mc.player.posX + x, box.minY - mc.player.posY + y, box.minZ - mc.player.posZ + z, box.maxX - mc.player.posX + x, box.maxY - mc.player.posY + y, box.maxZ - mc.player.posZ + z);
            RenderUtil.drawSelectionBoundingBox(axis, new Color(50, 255, 255, 150).getRGB());
        }
	};

	@EventLink
	public final Listener<MoveEvent> onMove = event -> {
        predictProcesses.clear();

        PredictEngine simulatedPlayer = PredictEngine.fromClientPlayer(mc.player.movementInput);

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
		 target = (EntityPlayer) TargetUtil.getTarget(4 * 3);
	};
	
	@EventLink
	public final Listener<TimerManipulationEvent> onTimerManipulation = event -> {
		if (target == null || predictProcesses.isEmpty() || shouldStop()) {
			return;
		}
		
		if (mc.player.hurtTime > 0) {
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
	};
	
	public boolean shouldStart() {
	    int index = ticks.getValue().intValue() - 1;

	    if (index < 0 || index >= predictProcesses.size()) {
	        return false;
	    }

	    PredictProcess predictedProcess = predictProcesses.get(index);

	    boolean isWithinDistance = predictedProcess.getPosition().distanceTo(target.getPositionVector()) <
	                               mc.player.getPositionVector().distanceTo(target.getPositionVector());

	    boolean isWithinActiveRange = MathUtil.inBetween(
	        3,
	        4,
	        predictedProcess.getPosition().distanceTo(target.getPositionVector())
	    );

	    boolean canSeeEachOther = mc.player.canEntityBeSeen(target) && target.canEntityBeSeen(mc.player);

	    boolean isNotCollided = !predictedProcess.isCollidedHorizontally();

	    return isWithinDistance && isWithinActiveRange && canSeeEachOther && isNotCollided;
	}

    public boolean shouldStop(){
        return mc.player.hurtTime != 0;
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
 }
