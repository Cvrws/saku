package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Tool", description = "Cambia a la mejor herramienta cada que rompes un bloque", category = Category.PLAYER)
public class AutoTool extends Module {

    public final BooleanValue spoof = new BooleanValue("Spoof Slot", this, true);
    public final BooleanValue switchBack = new BooleanValue("Switch Back", this, true, () -> !spoof.getValue());
    private int oldSlot;
    public boolean wasDigging;
    
    @Override
    public void onDisable() {
        if (wasDigging) {
            mc.player.inventory.currentItem = oldSlot;
            wasDigging = false;
        }
        
        SpoofHandler.stopSpoofing();
    }
    
	@EventLink
	public final Listener<TickEvent> onTick = event -> {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && PlayerUtil.findTool(mc.objectMouseOver.getBlockPos()) != -1) {
            if (!wasDigging) {
                oldSlot = mc.player.inventory.currentItem;
                if (spoof.getValue()) {
                	SpoofHandler.startSpoofing(oldSlot);
                }
            }
            mc.player.inventory.currentItem = PlayerUtil.findTool(mc.objectMouseOver.getBlockPos());
            wasDigging = true;
        } else if (wasDigging && (switchBack.getValue() || spoof.getValue())) {
            mc.player.inventory.currentItem = oldSlot;
            SpoofHandler.stopSpoofing();
            wasDigging = false;
        } else {
            oldSlot = mc.player.inventory.currentItem;
        }
	};
}