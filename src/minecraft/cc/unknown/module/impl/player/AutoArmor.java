package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Auto Armor", description = "Te coloca automaticamente la armadura", category = Category.PLAYER)
public class AutoArmor extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Spoof"))
			.add(new SubMode("Open Inv"))
			.setDefault("Open Inv");

	private final NumberValue startDelay = new NumberValue("Start Delay", this, 150, 0, 1000, 1);
	private final NumberValue speed = new NumberValue("Speed", this, 150, 0, 1000, 1);
	private final KeyBinding[] moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak };
	private final StopWatch startTimer = new StopWatch();
	private final StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
	    if (!mode.is("Spoof") || mc.currentScreen == null) {
	        if (mode.is("Open Inv")) {
	    	    if (mc.currentScreen == null) {
	    	        startTimer.reset();
	    	    }

	    	    if (!startTimer.elapse(startDelay.getValue().doubleValue(), false)) {
	    	        return;
	    	    }
	        }

	        if (stopWatch.elapse(speed.getValue().doubleValue(), false)) {
	            if (!(mc.currentScreen instanceof GuiInventory)) {
	                return;
	            }

	    	    for (int type = 1; type < 5; ++type) {
	    	    	if (stopWatch.elapse(speed.getValue().doubleValue(), false)) {
		    	        if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
		    	            ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
		    	            if (!InventoryUtil.isBestArmor(is, type)) {
		    	                InventoryUtil.drop(4 + type);
	
		    	                stopWatch.reset();
		    	                if (speed.getValue().doubleValue() != 0) {
		    	                    break;
		    	                }
		    	            }
		    	        }
	    	    	}
	    	    }
	    	    for (int type = 1; type < 5; ++type) {
	    	        if (stopWatch.elapse(speed.getValue().doubleValue(), false)) {
	    	            for (int i = 9; i < 45; ++i) {
	    	                if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
	    	                    ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
	    	                    if (isValidArmor(is, type)) {
	    	                        InventoryUtil.shiftClick(i);

	    	                        stopWatch.reset();
	    	                        if (speed.getValue().doubleValue() != 0) {
	    	                            break;
	    	                        }
	    	                    }
	    	                }
	    	            }
	    	        }
	    	    }
	        }
	    }
	};
	
	private boolean isValidArmor(ItemStack itemStack, int type) {
	    return InventoryUtil.getProtection(itemStack) > 0.0F
	            && InventoryUtil.isBestArmor(itemStack, type)
	            && !InventoryUtil.isBadStack(itemStack, true, true);
	}
}