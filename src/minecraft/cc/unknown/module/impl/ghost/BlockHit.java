package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.SlowDownEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(aliases = "Block Hit", description = "Block hitea automáticamente", category = Category.GHOST)
public class BlockHit extends Module {

	private final BoundsNumberValue duration = new BoundsNumberValue("Block Duration", this, 20, 100, 1, 500, 1);
	private final BoundsNumberValue distance = new BoundsNumberValue("Distance", this, 0, 3, 0, 6, 0.1);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	private boolean block;
	private StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame()) return;
		
		if (mc.currentScreen != null) return;

		if (block) {
			if (PlayerUtil.isHoldingWeapon() && (stopWatch.hasFinished() || !Mouse.isButtonDown(0)) && duration.getValue().intValue() <= stopWatch.getElapsedTime()) {
				block = false;
				mc.gameSettings.keyBindUseItem.pressed = false;
			}
			return;
		}

		if (PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null
				&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) >= distance.getValue().floatValue()
				&& mc.objectMouseOver.entityHit != null
				&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) <= distance.getSecondValue().floatValue()
				&& (chance.getValue().intValue() == 100 || Math.random() <= chance.getValue().intValue() / 100)) {
			block = true;
			stopWatch.setMillis(duration.getSecondValue().intValue());
			stopWatch.reset();
			
			 if (ViaLoadingBase.getInstance().getTargetVersion().isEqualTo(ProtocolVersion.v1_9)) {
				 mc.playerController.sendUseItem(mc.player, mc.theWorld, PlayerUtil.getItemStack());
			 } else {
				 mc.gameSettings.keyBindUseItem.pressed = true;
			 }
		}
	};
	
	@EventLink
	public final Listener<SlowDownEvent> onSlowDown = event -> {
		if (!isInGame()) return;
	    ItemStack item = PlayerUtil.getItemStack();
		
	    if (item == null) return;
		
		 if (ViaLoadingBase.getInstance().getTargetVersion().isEqualTo(ProtocolVersion.v1_9)) {
			if (item != null && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
				if ((item.getItem() instanceof ItemSword)) {
					event.setSprint(true);
					event.setForwardMultiplier(1f);
					event.setStrafeMultiplier(1f);
				}
			}
		 }
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!isInGame()) return;
	    ItemStack item = PlayerUtil.getItemStack();
	    if (item == null) return;
	    
        if (mc.player.getItemInUseDuration() == 1) {
			if (item.getItem() instanceof ItemSword) {
				if (ViaLoadingBase.getInstance().getTargetVersion().isEqualTo(ProtocolVersion.v1_9)) {
					PacketUtil.send(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 3));
					PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
				}
			}
        }
	};
}