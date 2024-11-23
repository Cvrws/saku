package cc.unknown.module.impl.world;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.BotComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Anti Bot", description = "Remueve los bots usados en algunos servidores", category = Category.WORLD)
public final class AntiBot extends Module {

	private final BooleanValue funcraftAntiBot = new BooleanValue("Funcraft Check", this, false);
	private final BooleanValue ncps = new BooleanValue("NPC Detection Check", this, false);
	private final BooleanValue duplicate = new BooleanValue("Duplicate Name Check", this, false);
	private final BooleanValue ping = new BooleanValue("No Ping Check", this, false);
	private final BooleanValue negativeIDCheck = new BooleanValue("Negative Unique ID Check", this, false);
	private final BooleanValue duplicateIDCheck = new BooleanValue("Duplicate Unique ID Check", this, false);
	private final BooleanValue ticksVisible = new BooleanValue("Time Visible Check", this, false);
	private final BooleanValue middleClick = new BooleanValue("Middle Click Bot", this, false);

	private boolean down;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (duplicateIDCheck.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				if (mc.world.playerEntities.stream()
						.anyMatch(player2 -> player2.getEntityId() == player.getEntityId() && player2 != player)) {
					getComponent(BotComponent.class).add(this, player);
				}
			});
		}

		if (duplicate.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				String name = player.getDisplayName().getUnformattedText();

				if (mc.world.playerEntities.stream()
						.anyMatch(player2 -> name.equals(player2.getDisplayName().getUnformattedText()))) {
					getComponent(BotComponent.class).add(this, player);
				}
			});
		}

		if (ping.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				final NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());

				if (info != null && info.getResponseTime() < 0) {
					getComponent(BotComponent.class).add(this, player);
				}
			});
		}

		if (ticksVisible.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				if (player.ticksVisible < 160) {
					getComponent(BotComponent.class).add(this, player);
				} else if (player.ticksExisted == 160) {
					getComponent(BotComponent.class).remove(this, player);
				}
			});
		}

		if (middleClick.getValue()) {
			if (Mouse.isButtonDown(2)
					|| (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && mc.gameSettings.keyBindAttack.isKeyDown())) {
				if (down)
					return;
				down = true;

				if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
					Entity entity = mc.objectMouseOver.entityHit;

					if (getComponent(BotComponent.class).contains(this, entity)) {
						getComponent(BotComponent.class).remove(this, entity);
					} else {
						getComponent(BotComponent.class).add(this, entity);
						PlayerUtil.display(entity.getName());
						;
					}
				}
			} else
				down = false;
		}

		if (negativeIDCheck.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				if (player.getEntityId() < 0) {
					getComponent(BotComponent.class).add(this, player);
				}
			});
		}

		if (ncps.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				if (player.moved) {
					getComponent(BotComponent.class).remove(this, player);
				} else {
					getComponent(BotComponent.class).add(this, player);
				}
			});
		}

		if (funcraftAntiBot.getValue()) {
			mc.world.playerEntities.forEach(player -> {
				if (player.getDisplayName().getUnformattedText().contains("§")) {
					getComponent(BotComponent.class).remove(this, player);
					return;
				}

				getComponent(BotComponent.class).add(this, player);
			});
		}

	};

	@Override
	public void onDisable() {
		getComponent(BotComponent.class).clear();
	}
}
