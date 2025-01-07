package cc.unknown.module.impl.move;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(aliases = "No Slow", description = "Remueve la lentitud al utilizar algunos objetos.", category = Category.MOVEMENT)
public class NoSlow extends Module {
	
	private final ModeValue mode = new ModeValue("Type", this)
			.add(new SubMode("Pre Switch"))
			.add(new SubMode("Post Switch"))
			.add(new SubMode("Switch"))
			.add(new SubMode("Vanilla"))
			.setDefault("Vanilla");
	
	private final NumberValue forward = new NumberValue("Forward", this, 1, 0.2, 1, 0.1);
	private final NumberValue strafe = new NumberValue("Strafe", this, 1, 0.2, 1, 0.1);
	
	private final BooleanValue sword = new BooleanValue("Sword", this, true);
	private final BooleanValue bow = new BooleanValue("Bow", this, true);
	private final BooleanValue comestibles = new BooleanValue("Comestibles", this, true);
	private final BooleanValue sprint = new BooleanValue("Sprint", this, true);
	private final BooleanValue timer = new BooleanValue("Boost Timer", this, true);
	private final NumberValue boostTimer = new NumberValue("Timer", this, 1, 0.1, 10, 0.1, () -> !timer.getValue());

	
	private final StopWatch timeHelper = new StopWatch();

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0F;
	}

	@Override
	public void onEnable() {
		mc.timer.timerSpeed = 1.0F;
	}
	
	@EventLink
	public final Listener<SlowDownEvent> onSlowDown = event -> {
		if (!isInGame()) return;
	    ItemStack item = PlayerUtil.getItemStack();
		
	    if (item == null) return;
		
		if (item != null && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
			if ((sword.getValue() && item.getItem() instanceof ItemSword) || (bow.getValue() && item.getItem() instanceof ItemBow) || (comestibles.getValue() && item.getItem() instanceof ItemFood || item.getItem() instanceof ItemBucketMilk || item.getItem() instanceof ItemPotion)) {
				event.setSprint(sprint.getValue());
				event.setForwardMultiplier(forward.getValue().floatValue());
				event.setStrafeMultiplier(strafe.getValue().floatValue());
			}
			mc.timer.timerSpeed = timer.getValue() ? boostTimer.getValue().floatValue() : 1.0F;
		} else {
			timeHelper.reset();
		}
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
	    if (!isInGame()) return;
	    ItemStack item = PlayerUtil.getItemStack();
	    if (item == null) return;
	    
        if (mc.player.getItemInUseDuration() == 1) {
			if ((sword.getValue() && item.getItem() instanceof ItemSword) || (bow.getValue() && item.getItem() instanceof ItemBow) || (comestibles.getValue() && item.getItem() instanceof ItemFood || item.getItem() instanceof ItemBucketMilk || item.getItem() instanceof ItemPotion)) {
				if (mode.is("Post Switch")) {
					PacketUtil.send(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 3));
					PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
				}
				mc.timer.timerSpeed = timer.getValue() ? boostTimer.getValue().floatValue() : 1.0F;
			}
        }
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!isInGame()) return;
	    ItemStack item = PlayerUtil.getItemStack();
	    if (item == null) return;
	    
        if (mc.player.getItemInUseDuration() == 1) {
			if ((sword.getValue() && item.getItem() instanceof ItemSword) || (bow.getValue() && item.getItem() instanceof ItemBow) || (comestibles.getValue() && item.getItem() instanceof ItemFood || item.getItem() instanceof ItemBucketMilk || item.getItem() instanceof ItemPotion)) {
				switch (mode.getValue().getName()) {
				case "Pre Switch":
					PacketUtil.send(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 3));
					PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
					break;
				case "Switch":
			        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
			        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 7 + 2));
			        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
					break;
				}
			}
        }
	};
}