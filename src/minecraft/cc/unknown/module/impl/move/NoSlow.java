package cc.unknown.module.impl.move;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PostUpdateEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

@ModuleInfo(aliases = "No Slow", description = "Remueve la lentitud al utilizar algunos objetos.", category = Category.MOVEMENT)
public class NoSlow extends Module {
	
	private final ModeValue eventType = new ModeValue("Event Type", this)
			.add(new SubMode("Pre Attack"))
			.add(new SubMode("Post Attack"))
			.add(new SubMode("Pre Position"))
			.add(new SubMode("Post Position"))
			.setDefault("Pre Attack");
	
	private final ModeValue mode = new ModeValue("Bypass Type", this)
			.add(new SubMode("Switch / 3"))
			.add(new SubMode("Alice"))
			.add(new SubMode("Test"))
			.add(new SubMode("Vanilla"))
			.setDefault("Vanilla");
	
	private final NumberValue itemDurationTime = new NumberValue("Item Use Duration", this, 1, 0, 1, 1);
	
	private final NumberValue forward = new NumberValue("Forward", this, 1, 0.2, 1, 0.1);
	private final NumberValue strafe = new NumberValue("Strafe", this, 1, 0.2, 1, 0.1);
	
	private final BooleanValue sword = new BooleanValue("Sword", this, true);
	private final BooleanValue bow = new BooleanValue("Bow", this, true);
	private final BooleanValue comestibles = new BooleanValue("Comestibles", this, true);
	private final BooleanValue sprint = new BooleanValue("Sprint", this, true);
	
	@EventLink
	public final Listener<SlowDownEvent> onSlowDown = event -> {		
		if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
			if (conditionables()) {
				event.setSprint(sprint.getValue());
				event.setForwardMultiplier(forward.getValue().floatValue());
				event.setStrafeMultiplier(strafe.getValue().floatValue());
			}
		}
	};
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (eventType.is("Pre Attack")) {
			 bypass(mode.getValue().getName());
		}
	};
	
	@EventLink
	public final Listener<PostUpdateEvent> onPostUpdate = event -> {
		if (eventType.is("Post Attack")) {
			 bypass(mode.getValue().getName());
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (eventType.is("Pre Position")) {
			 bypass(mode.getValue().getName());
		}
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
		if (eventType.is("Post Position")) {
			 bypass(mode.getValue().getName());
		}
	};

	private void bypass(String mode) {	    
        if (mc.player.getItemInUseDuration() == itemDurationTime.getValue().intValue()) {
			if (conditionables()) {
				switch (mode) {
				case "Switch / 3":
					PacketUtil.send(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 3));
					PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
					break;
				case "Test":
		            PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
		            PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 7 + 2));
		            PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
					break;
				case "Alice":
			        if (mc.player.ticksExisted % 3 == 0) {
			        	PacketUtil.send(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0, 0, 0));
			        }
					break;
				}
			}
        }
	}
	
	private boolean conditionables() {
	    ItemStack item = PlayerUtil.getItemStack();
	    if (item == null) return false;   
		return (sword.getValue() && item.getItem() instanceof ItemSword) || (bow.getValue() && item.getItem() instanceof ItemBow) || (comestibles.getValue() && item.getItem() instanceof ItemFood || item.getItem() instanceof ItemBucketMilk || item.getItem() instanceof ItemPotion);
	}
}