package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.SpoofComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Tool", description = "Cambia a la mejor herramienta cada que rompes un bloque", category = Category.PLAYER)
public class AutoTool extends Module {

	private int prevItem = 0;
	private boolean mining = false;
	private int bestSlot = 0;
		
	@Override
	public void onDisable() {
    	SpoofComponent.stopSpoofing();
	}
    
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		mining = false;
	};
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!mc.gameSettings.keyBindUseItem.isKeyDown() && mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			int bestSpeed = 0;
			bestSlot = -1;

			if (!mining) {
				prevItem = mc.player.inventory.currentItem;
			}

			Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

			for (int i = 0; i <= 8; i++) {
				ItemStack item = mc.player.inventory.getStackInSlot(i);
				if (item == null)
					continue;

				float speed = item.getStrVsBlock(block);

				if (speed > bestSpeed) {
					bestSpeed = (int) speed;
					bestSlot = i;
				}

				if (bestSlot != -1) {
					mc.player.inventory.currentItem = bestSlot;
				}
			}
			mining = true;
		} else {
			if (mining) {
				mining = false;
			} else {
				prevItem = mc.player.inventory.currentItem;
			}
		}
	};
}