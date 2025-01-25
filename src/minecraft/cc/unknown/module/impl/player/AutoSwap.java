  package cc.unknown.module.impl.player;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

@ModuleInfo(aliases = "Auto Swap", description = "Block Switching", category = Category.PLAYER)
public class AutoSwap extends Module {

	private final BoundsNumberValue pitchRange = new BoundsNumberValue("Pitch Range", this, 70, 85, 0, 90, 1);

	private int lastSlot;
	
	@Override
	public void onEnable() {
		lastSlot = -1;		
	}
	
	@Override
	public void onDisable() {
		mc.player.inventory.currentItem = lastSlot;
	}


	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame()) return;
		
        if (lastSlot == -1) {
        	lastSlot = mc.player.inventory.currentItem;
        }
        
		final int slot = SlotUtil.findBlock();
		
        if (slot == -1) {
            return;
        }
        
		if (mc.player.rotationPitch < pitchRange.getValue().floatValue() || mc.player.rotationPitch > pitchRange.getSecondValue().floatValue()) {
			return;
		}
        
        if (shouldSkipBlockCheck()) {
        	mc.player.inventory.currentItem = slot;
        }
        
		if (mc.currentScreen == null || mc.player.getHeldItem() == null) return;
	};
	
	private boolean shouldSkipBlockCheck() {
		ItemStack heldItem = mc.player.getHeldItem();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}
}