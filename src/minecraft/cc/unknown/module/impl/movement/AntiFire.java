package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.LookEvent;
import cc.unknown.event.impl.render.RenderRotationEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.structure.geometry.Vector2f;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Anti Fire", description = "Extinguishes the flame", category = Category.MOVEMENT)
public class AntiFire extends Module {

	private StopWatch timer = new StopWatch();
	private boolean canWork;
	private boolean done;
	
	@EventLink(value = Priority.HIGH)
	public final Listener<PreUpdateEvent> onHighPreUpdate = event -> {		
		if (canWork) {
			RotationHandler.setRotations(new Vector2f(mc.player.rotationYaw, 90.0f), 2, MoveFix.SILENT);
		}
	};

	@EventLink
	public final Listener<LookEvent> onLook = event -> {
		int slot;
		if (this.mc.player.isBurning()) {
			slot = findWaterBucket();
			if (slot == -1) {
				this.cancel();
				return;
			}

			this.canWork = true;
			this.mc.player.inventory.currentItem = slot;
			this.timer.reset();
		} else {
			if (!this.mc.player.isInWater()) {
				this.cancel();
				return;
			}

			slot = getEmptyBucketSlot();
			if (slot == -1) {
				this.cancel();
				return;
			}

			if (this.timer.elapse(100.0D, false)) {
				this.cancel();
				return;
			}

			this.canWork = true;
			this.mc.player.inventory.currentItem = slot;
		}

		if (this.canWork) {
			event.setRotation(new Vector2f(RotationHandler.lastServerRotations.x, 90.0f));
			RotationHandler.lastServerRotations.x = this.mc.player.rotationYaw;
			RotationHandler.lastServerRotations.y = 90.0F;
			if (!this.done) {
				this.mc.rightClickMouse();
			}

			this.done = true;
		}
	};

	private int getEmptyBucketSlot() {
	    int item = -1;

	    for (int i = 36; i < 45; ++i) {
	        if (mc.player.inventoryContainer.getSlot(i) != null) {
	            if (mc.player.inventoryContainer.getSlot(i).getStack() != null && mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.bucket) {
	                item = i - 36;
	                break;
	            }
	        }
	    }

	    return item;
	}
	
	private int findWaterBucket() {
		if (containsItem(mc.player.getHeldItem(), Items.water_bucket)) {
			return mc.player.inventory.currentItem;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (containsItem(mc.player.inventory.mainInventory[i], Items.water_bucket)) {
					return i;
				}
			}
		}
		return -1;
	}

	private boolean containsItem(ItemStack itemStack, Item item) {
		return itemStack != null && itemStack.getItem() == item;
	}
	
	private void cancel() {
		if (canWork) {
			done = false;
			canWork = false;
			RotationHandler.lastServerRotations.x = mc.player.rotationYaw;
			RotationHandler.lastServerRotations.y = mc.player.rotationPitch;;
		}
	}
}