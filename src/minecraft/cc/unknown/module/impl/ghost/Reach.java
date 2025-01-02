package cc.unknown.module.impl.ghost;

import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MouseEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
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

	public final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.0, 4.0, 3.0, 6.0, 0.01);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 1, 100, 1);
	private final BooleanValue hitThroughBlocks = new BooleanValue("Hit through blocks", this, false);

	@EventLink
	public final Listener<MouseEvent> onMouseInput = event -> {
		AutoClicker clicker = getModule(AutoClicker.class);
		if (isInGame() && event.getCode() == 0 && (!clicker.isEnabled() || !Mouse.isButtonDown(0)) || PlayerUtil.isClicking()) {
			callReach();
		}
	};

	private boolean callReach() {
		if (!isInGame()) {
			return false;
		} else if (!(chance.getValue().intValue() == 100 || Math.random() <= chance.getValue().intValue() / 100)) {
			return false;
		} else {
			if (!hitThroughBlocks.getValue() && mc.objectMouseOver != null) {
				BlockPos p = mc.objectMouseOver.getBlockPos();
				if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
					return false;
				}
			}

			double reach = range.getRandomBetween().doubleValue();
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
		Reach reich = getModule(Reach.class);

		if (!reich.isEnabled()) {
			reach = mc.playerController.extendedReach() ? 6.0D : 3.0D;
		}

		Entity renderView = mc.getRenderViewEntity();
		Entity target = null;
		if (renderView == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 eyePosition = renderView.getPositionEyes(1.0F);
			Vec3 playerLook = renderView.getLook(1.0F);
			Vec3 reachTarget = eyePosition.addVector(playerLook.xCoord * reach, playerLook.yCoord * reach,
					playerLook.zCoord * reach);
			Vec3 targetHitVec = null;
			List<Entity> targetsWithinReach = mc.theWorld.getEntitiesWithinAABBExcludingEntity(renderView,
					renderView.getEntityBoundingBox()
							.addCoord(playerLook.xCoord * reach, playerLook.yCoord * reach, playerLook.zCoord * reach)
							.expand(1.0D, 1.0D, 1.0D));
			double adjustedReach = reach;

			for (Entity entity : targetsWithinReach) {
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize());
					AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition targetPosition = entityBoundingBox.calculateIntercept(eyePosition,
							reachTarget);
					if (entityBoundingBox.isVecInside(eyePosition)) {
						if (0.0D < adjustedReach || adjustedReach == 0.0D) {
							target = entity;
							targetHitVec = targetPosition == null ? eyePosition : targetPosition.hitVec;
							adjustedReach = 0.0D;
						}
					} else if (targetPosition != null) {
						double distanceToVec = eyePosition.distanceTo(targetPosition.hitVec);
						if (distanceToVec < adjustedReach || adjustedReach == 0.0D) {
							if (entity == renderView.ridingEntity) {
								if (adjustedReach == 0.0D) {
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

			if (adjustedReach < reach && !(target instanceof EntityLivingBase)
					&& !(target instanceof EntityItemFrame)) {
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