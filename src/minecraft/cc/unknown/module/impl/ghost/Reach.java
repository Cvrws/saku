package cc.unknown.module.impl.ghost;

import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MouseEvent;
import cc.unknown.event.impl.input.RightClickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.MouseOverEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Reach", description = "Permite golpear más lejos", category = Category.GHOST)
public class Reach extends Module {

	public final NumberValue range = new NumberValue("Range", this, 3.0, 3.0, 6.0, 0.1);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 1, 100, 1);
	private final BooleanValue throughWalls = new BooleanValue("Through Walls", this, false);
	
	@EventLink
	public final Listener<MouseEvent> onMouseInput = event -> {
		AutoClicker clicker = getModule(AutoClicker.class);
		if (isInGame() && event.getCode() == 0 && (!clicker.isEnabled() || !Mouse.isButtonDown(0)) || PlayerUtil.isClicking()) {
			callReach();
		}
	};

    @EventLink
    public final Listener<RightClickEvent> onRightClick = event -> mc.objectMouseOver = RayCastUtil.rayCast(RotationHandler.rotations, 4.5);

	private boolean callReach() {
		if (!isInGame()) {
			return false;
		} else if (!(chance.getValue().intValue() == 100 || Math.random() <= chance.getValue().intValue() / 100)) {
			return false;
		} else {
			if (!throughWalls.getValue() && mc.objectMouseOver != null) {
				BlockPos p = mc.objectMouseOver.getBlockPos();
				if (p != null && mc.world.getBlockState(p).getBlock() != Blocks.air) {
					return false;
				}
			}

			double reach = range.getValue().doubleValue();
			Object[] object = findEntitiesWithinReach(reach);
			if (object == null) {
				return false;
			} else {
				Entity en = (Entity) object[0];
				mc.objectMouseOver = new MovingObjectPosition(en, (Vec3) object[1]);
				mc.pointedEntity = en;
				return true;
			}
		}
	}

	private Object[] findEntitiesWithinReach(double reach) {
		if (!this.isEnabled()) {
			reach = mc.playerController.extendedReach() ? 6 : 3;
		}

		Entity renderView = mc.getRenderViewEntity();
		Entity target = null;
		if (renderView == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 eyePosition = renderView.getPositionEyes(1F);
			Vec3 playerLook = renderView.getLook(1F);
			Vec3 reachTarget = eyePosition.addVector(playerLook.xCoord * reach, playerLook.yCoord * reach,
					playerLook.zCoord * reach);
			Vec3 targetHitVec = null;
			List<Entity> targetsWithinReach = mc.world.getEntitiesWithinAABBExcludingEntity(renderView,
					renderView.getEntityBoundingBox()
							.addCoord(playerLook.xCoord * reach, playerLook.yCoord * reach, playerLook.zCoord * reach)
							.expand(1, 1, 1));
			double adjustedReach = reach;

			for (Entity entity : targetsWithinReach) {
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize());
					AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition targetPosition = entityBoundingBox.calculateIntercept(eyePosition,
							reachTarget);
					if (entityBoundingBox.isVecInside(eyePosition)) {
						if (0 < adjustedReach || adjustedReach == 0) {
							target = entity;
							targetHitVec = targetPosition == null ? eyePosition : targetPosition.hitVec;
							adjustedReach = 0;
						}
					} else if (targetPosition != null) {
						double distanceToVec = eyePosition.distanceTo(targetPosition.hitVec);
						if (distanceToVec < adjustedReach || adjustedReach == 0) {
							if (entity == renderView.ridingEntity) {
								if (adjustedReach == 0) {
									target = entity;
									targetHitVec = targetPosition.hitVec;
								}
							} else {
								target = entity;
								targetHitVec = targetPosition.hitVec;
								adjustedReach = distanceToVec;
							}
						}
					}
				}
			}

			if (adjustedReach < reach && !(target instanceof EntityLivingBase) && !(target instanceof EntityItemFrame)) {
				target = null;
			}

			mc.mcProfiler.endSection();
			if (target != null && targetHitVec != null) {
				return new Object[] { target, targetHitVec };
			} else {
				return null;
			}
		}
	}
}