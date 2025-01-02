package cc.unknown.module.impl.other;

import org.lwjgl.input.Mouse;

import com.ibm.icu.impl.duration.impl.Utils;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.ItemUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Auto Sword", description = "Cambia rapidamente a la espada", category = Category.OTHER)
public final class AutoSword extends Module {

	private final BooleanValue onlyHold = new BooleanValue("Only when hold left click", this, true);
	private final BooleanValue backRev = new BooleanValue("Revert to old slot", this, true);
	
    private boolean onWeapon;
    private int prevSlot;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!isInGame() || mc.currentScreen != null) {
	        return;
	    }

	    if (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null || (onlyHold.getValue() && !Mouse.isButtonDown(0))) {
	        if (onWeapon) {
	            onWeapon = false;
	            if (backRev.getValue()) {
	                mc.player.inventory.currentItem = prevSlot;
	            }
	        }
	        return;
	    }

	    Entity target = mc.objectMouseOver.entityHit;

	    if (onlyHold.getValue() && !Mouse.isButtonDown(0)) {
	        return;
	    }

	    if (!onWeapon) {
	        prevSlot = mc.player.inventory.currentItem;
	        onWeapon = true;

	        int dmgSlot = ItemUtil.getMaxDamageSlot();
	        if (dmgSlot >= 0 && 
	            ItemUtil.getSlotDamage(dmgSlot) > ItemUtil.getSlotDamage(mc.player.inventory.currentItem)) {
	            mc.player.inventory.currentItem = dmgSlot;
	        }
	    }
	};
}
