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
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
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

@ModuleInfo(aliases = "Murder Mystery", description = "Caja de herramientas para Murder Mystery", category = Category.OTHER)
public final class MurderMystery extends Module {

	private final BooleanValue checkBow = new BooleanValue("Check bow", this, false);
	
	private final BooleanValue drawGold = new BooleanValue("Draw gold ingots esp", this, false);
	private final BooleanValue drawBow = new BooleanValue("Draw bow esp", this, false);
	private final BooleanValue drawMurder = new BooleanValue("Draw murder esp", this, false);
	private final BooleanValue drawDetective = new BooleanValue("Draw detective esp", this, false);

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
			if (player.getHeldItem() == null || detectives.contains(player) || player == mc.player) continue;
			String itemName = player.getHeldItem().getDisplayName();

			if (!murderers.contains(player) && isMurder(itemName)) {
				murderers.add(player);
				mc.player.playSound("note.pling", 1.0F, 1.0F);
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
			if (entity == mc.player) continue;

			if (entity instanceof EntityItem && ((EntityItem) entity).getEntityItem().getItem() == Items.gold_ingot && drawGold.getValue()) {
				RenderUtil.drawSimpleItemBox(entity, Color.YELLOW);
			} else if (entity instanceof EntityArmorStand && drawBow.getValue() && isArmorStandHoldingBow((EntityArmorStand) entity)) {
				RenderUtil.drawSimpleItemBox(entity, Color.CYAN);
			} else if (entity instanceof EntityPlayer) {
				if (murderers.contains(entity)) {
					RenderUtil.drawSimpleLine((EntityPlayer) entity, event.getPartialTicks(), Color.RED);
				}
			}
			
			if (murderers.contains(entity) && drawMurder.getValue()) {
				RenderUtil.drawSimpleBox((EntityPlayer) entity, Color.RED.getRGB(), event.getPartialTicks());
			} else if (detectives.contains(entity) && drawDetective.getValue()) {
				RenderUtil.drawSimpleBox((EntityPlayer) entity, Color.BLUE.getRGB(), event.getPartialTicks());
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
			PlayerUtil.display(ChatFormatting.YELLOW + "[" + color + symbol + ChatFormatting.YELLOW + "] " + color
					+ message + ChatFormatting.RESET);
		}
	}

	private boolean isMurder(String itemName) {
		return itemName.contains("Knife") || itemName.contains("Cuchillo");
	}

	private boolean isBow(String itemName) {
		return itemName.contains("Bow") || itemName.contains("Arco");
	}
}
