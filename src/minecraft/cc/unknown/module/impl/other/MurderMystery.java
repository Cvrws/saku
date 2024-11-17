package cc.unknown.module.impl.other;

import java.awt.Color;
import java.util.ArrayList;

import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatFormatting;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Murder Mystery", description = "Murder Mystery utilities", category = Category.OTHER)
public final class MurderMystery extends Module {

	private final BooleanValue checkBow = new BooleanValue("Check bow", this, false);
	private final BooleanValue bowAim = new BooleanValue("Bow aimbot", this, false);

	private final BooleanValue esp = new BooleanValue("ESP", this, false);
	private final BooleanValue drawGold = new BooleanValue("Draw gold ingots esp", this, false, () -> !esp.getValue());
	private final BooleanValue drawBow = new BooleanValue("Draw bow esp", this, false, () -> !esp.getValue());
	private final BooleanValue drawMurder = new BooleanValue("Draw murder esp", this, false, () -> !esp.getValue());
	private final BooleanValue drawDetective = new BooleanValue("Draw detective esp", this, false, () -> !esp.getValue());

	private final BooleanValue notification = new BooleanValue("Notification", this, false);

	private final ArrayList<EntityPlayer> murderers = new ArrayList<>();
	private final ArrayList<EntityPlayer> detectives = new ArrayList<>();

	public EntityLivingBase target;
	public float rangeAimVelocity = 0.0f;

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		if (!isInGame())
			return;

		for (EntityPlayer player : mc.world.playerEntities) {
			if (player.getHeldItem() == null || detectives.contains(player))
				continue;
			if (player == mc.player)
				continue;

			String itemName = player.getHeldItem().getDisplayName();

			if (!murderers.contains(player) && isMurder(itemName)) {
				murderers.add(player);
				sendNotification(PlayerUtil.name(player) + " es el asesino.", ChatFormatting.RED, "!");
			}

			if (checkBow.getValue() && isBow(itemName) && !murderers.contains(player)) {
				detectives.add(player);
				sendNotification(PlayerUtil.name(player) + " tiene un arco.", ChatFormatting.BLUE, "*");
			}
		}
	};

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity == mc.player)
				continue;

			if (entity instanceof EntityItem && ((EntityItem) entity).getEntityItem().getItem() == Items.gold_ingot
					&& drawGold.getValue()) {
				RenderUtil.drawSimpleItemBox(entity, Color.YELLOW);
			} else if (entity instanceof EntityArmorStand && drawBow.getValue()
					&& isArmorStandHoldingBow((EntityArmorStand) entity)) {
				RenderUtil.drawSimpleItemBox(entity, Color.CYAN);
			} else if (entity instanceof EntityPlayer) {
				if (murderers.contains(entity)) {
					RenderUtil.drawSimpleLine((EntityPlayer) entity, event.getPartialTicks(), Color.RED);
				}
			}
		}
	};

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		for (EntityPlayer player : mc.world.playerEntities) {
			if (player == mc.player)
				continue;

			if (murderers.contains(player)) {
				RenderUtil.drawSimpleBox(player, Color.RED);
			} else if (detectives.contains(player)) {
				RenderUtil.drawSimpleBox(player, Color.BLUE);
			}
		}
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!isInGame()) return;

	    if (bowAim.getValue()) {

	        this.target = this.getClosestEntity();
	        if (this.target == null) {
	            return;
	        }

	        if (!(target instanceof EntityPlayer) || !murderers.contains(target)) {
	            return;
	        }

	        ItemStack itemStack = mc.player.inventory.getCurrentItem();
	        if (itemStack == null) {
	            return;
	        }

	        if (!(itemStack.getItem() instanceof ItemBow)) {
	            return;
	        }

	        if (target instanceof EntityPlayer) {

	            if (((EntityPlayer) target).getHeldItem() == null) return;

	            String itemName = ((EntityPlayer) target).getHeldItem().getDisplayName();

	            if (!murderers.isEmpty()) {

	                if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
	                    return;
	                }

	                int rangeCharge = mc.player.getItemInUseCount();
	                this.rangeAimVelocity = rangeCharge / 20;
	                this.rangeAimVelocity = (this.rangeAimVelocity * this.rangeAimVelocity + this.rangeAimVelocity * 2.0f) / 3.0f;
	                this.rangeAimVelocity = 1.0f;
	                if (this.rangeAimVelocity > 1.0f) {
	                    this.rangeAimVelocity = 1.0f;
	                }
	                double posX = this.target.posX - mc.player.posX;
	                double posY = this.target.posY + (double) this.target.getEyeHeight() - 0.15 - mc.player.posY - (double) mc.player.getEyeHeight();
	                double posZ = this.target.posZ - mc.player.posZ;
	                double y2 = Math.sqrt(posX * posX + posZ * posZ);
	                float g = 0.006f;
	                float tmp = (float) ((double) (this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity) - (double) g * ((double) g * (y2 * y2) + 2.0 * posY * (double) (this.rangeAimVelocity * this.rangeAimVelocity)));
	                float pitch = (float) (-Math.toDegrees(Math.atan(((double) (this.rangeAimVelocity * this.rangeAimVelocity) - Math.sqrt(tmp)) / ((double) g * y2))));
	                assistFaceEntity((Entity) this.target, 22.0F, 0.0f);
	                mc.player.rotationPitch = pitch;
	            }
	        }
	    }
	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		murderers.clear();
		detectives.clear();
	};

	private boolean isArmorStandHoldingBow(EntityArmorStand armorStand) {
		return armorStand.getEquipmentInSlot(0) != null && armorStand.getEquipmentInSlot(0).getItem() == Items.bow;
	}

	private void sendNotification(String message, ChatFormatting color, String symbol) {
		if (notification.getValue()) {
			NotificationComponent.post("Murder Mystery", message, 1000);
		} else {
			ChatUtil.display(ChatFormatting.YELLOW + "[" + color + symbol + ChatFormatting.YELLOW + "] " + color
					+ message + ChatFormatting.RESET);
		}
	}

	private boolean isMurder(String itemName) {
		return itemName.contains("Knife") || itemName.contains("Cuchillo");
	}

	private boolean isBow(String itemName) {
		return itemName.contains("Bow") || itemName.contains("Arco");
	}

	private boolean check(EntityLivingBase entity) {
		if (entity instanceof EntityArmorStand) {
			return false;
		}
		if (entity == mc.player) {
			return false;
		}
		if (entity.isDead) {
			return false;
		}
		return mc.player.canEntityBeSeen(entity);
	}

	private void assistFaceEntity(Entity entity, float yaw, float pitch) {
		double yDifference;
		if (entity == null) {
			return;
		}
		double diffX = entity.posX - mc.player.posX;
		double diffZ = entity.posZ - mc.player.posZ;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			yDifference = entityLivingBase.posY + (double) entityLivingBase.getEyeHeight() - (mc.player.posY + (double) mc.player.getEyeHeight());
		} else {
			yDifference = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.player.posY + (double) mc.player.getEyeHeight());
		}
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float rotationYaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
		float rotationPitch = (float) (-(Math.atan2(yDifference, dist) * 180.0 / Math.PI));
		if (yaw > 0.0f) {
			mc.player.rotationYaw = updateRotation(mc.player.rotationYaw, rotationYaw, yaw / 4.0f);
		}
		if (pitch > 0.0f) {
			mc.player.rotationPitch = updateRotation(mc.player.rotationPitch, rotationPitch, pitch / 4.0f);
		}
	}

	private float updateRotation(float currentRotation, float targetRotation, float maxRotationDelta) {
		float deltaRotation = MathHelper.wrapAngleTo180_float(targetRotation - currentRotation);
		deltaRotation = MathHelper.clamp_float(deltaRotation, -maxRotationDelta, maxRotationDelta);
		return currentRotation + deltaRotation;
	}

	private EntityLivingBase getClosestEntity() {
		EntityLivingBase closestEntity = null;
		for (Entity o : mc.world.loadedEntityList) {
			EntityLivingBase entity;
			if (!(o instanceof EntityLivingBase) || o instanceof EntityArmorStand
					|| !this.check(entity = (EntityLivingBase) o) || closestEntity != null
							&& !(mc.player.getDistanceToEntity(entity) < mc.player.getDistanceToEntity(closestEntity)))
				continue;
			closestEntity = entity;
		}
		return closestEntity;
	}
}
