package cc.unknown.module.impl.world;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

@ModuleInfo(aliases = "Legit Scaffold", description = "Shiftea al borde de cada bloque", category = Category.WORLD)
public class LegitScaffold extends Module {
	
	private final NumberValue delay = new NumberValue("Delay", this, 1, 0, 5, 1);
	private final BooleanValue checkAngle = new BooleanValue("Check Angle", this, false);
	private final NumberValue angle = new NumberValue("Angle", this, 50, 45, 75, 1, () -> !checkAngle.getValue());
	private final NumberValue edgeOffset = new NumberValue("Edge Offset", this, 15, 0, 30, 1);
	private final NumberValue blockDist = new NumberValue("Block Distance", this, 0.5, 0.1, 2, 0.1);
	private final BooleanValue randomize = new BooleanValue("Randomize", this, false);
	private final BooleanValue legit = new BooleanValue("Legitimize", this, true);
	private final BooleanValue holdShift = new BooleanValue("Require Sneak", this, false);
	private final BooleanValue slotSwap = new BooleanValue("Block Switching", this, true);
	private final BooleanValue blocksOnly = new BooleanValue("Blocks Only", this, true);
	private final BooleanValue backwards = new BooleanValue("Backwards Movement Only", this, true);
	private final BooleanValue hideSneak = new BooleanValue("Hide Sneak Animation", this, false);

	private int slot;
	private StopWatch stopWatch = new StopWatch();
	
	@Override
	public void onEnable() {
		slot = -1;
	}
	
	@Override
	public void onDisable() {
		releaseKey();
		if (overAir()) {
			releaseKey();
		}

		mc.player.inventory.currentItem = slot;
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!(mc.currentScreen == null) || !isInGame()) return;
	    if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return;

	    if (hideSneak.getValue()) {
	        mc.player.hideSneakHeight.reset();
	    }

	    if (blocksOnly.getValue()) {
	        ItemStack i = mc.player.getHeldItem();
	        if (i == null || !(i.getItem() instanceof ItemBlock)) {
	        	releaseKey();
	            return;
	        }
	    }

	    if (backwards.getValue()) {
	        if ((mc.player.movementInput.moveForward > 0 && mc.player.movementInput.moveStrafe == 0) || mc.player.movementInput.moveForward >= 0) {
	        	releaseKey();
	            return;
	        }
	    }
	    
	    if (checkAngle.getValue()) {
	        if (MathHelper.wrapDegrees(event.getPitch()) < angle.getValueToFloat()) {
	        	this.releaseKey();
	        	return;
	        }
	    }

	    double edgeOffset = this.edgeOffset.getValueToDouble() / 100.0;
	    int delayTicks = delay.getValueToInt();
	    //ChatUtil.display("Edge Offset: " + edgeOffset);
	    
	    if (randomize.getValue()) {
	    	edgeOffset += (MathUtil.nextRandom(0.7, 1.3).doubleValue() * MathUtil.nextRandom(-0.01, 0.01).doubleValue());
	    	delayTicks = (int) MathUtil.getSafeRandom(delayTicks - 1, delayTicks + 1);
	    }
	    
	    boolean isOnEdge = mc.world.getBlockState(new BlockPos(mc.player).down()).getBlock() instanceof BlockAir && (legit.getValue() || event.isOnGround());
	    
	    if (!isOnEdge) {
	        double posX = mc.player.posX;
	        double posZ = mc.player.posZ;
	        double blockX = Math.floor(posX) + 0.5;
	        double blockZ = Math.floor(posZ) + 0.5;
	        
	        double distanceX = Math.abs(posX - blockX);
	        double distanceZ = Math.abs(posZ - blockZ);
	        
	        if (distanceX > blockDist.getValueToDouble() - edgeOffset || distanceZ > blockDist.getValueToDouble() - edgeOffset) {
	            isOnEdge = true;
	        }
	    }
	    
	    if (delayTicks > 0 && !stopWatch.reached(delayTicks * 50)) {
	        return;
	    }

	    if (holdShift.getValue()) {
	        mc.gameSettings.keyBindSneak.setPressed(isOnEdge && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
	    } else {
	        mc.gameSettings.keyBindSneak.setPressed(isOnEdge || GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
	    }

	    if (isOnEdge) {
	        stopWatch.reset();
	    }
	};
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame()) return;
        
		if (slot == -1) {
			slot = mc.player.inventory.currentItem;
		}
		
		int slot = InventoryUtil.findBlock();
		
		if (slot == -1) return;
        
        if (slotSwap.getValue() && shouldSkipBlockCheck()) {
        	mc.player.inventory.currentItem = slot;
        }
        
		if (mc.currentScreen == null || mc.player.getHeldItem() == null) return;

	};

	private void swapToBlock() {
		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
			if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && itemInSlot.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemInSlot.getItem();
				Block block = itemBlock.getBlock();
				if (mc.player.inventory.currentItem != slot && block.isFullCube()) {
					mc.player.inventory.currentItem = slot;
				} else {
					return;
				}
				return;
			}
		}
	}

	private boolean shouldSkipBlockCheck() {
		ItemStack heldItem = mc.player.getHeldItem();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}
	
	private boolean overAir() {
		return blockRelativeToPlayer(0, -1, 0) instanceof BlockAir;
	}

	private Block getBlock(final double x, final double y, final double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
	
	private void releaseKey() {
		mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
	}

	private Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return getBlock(mc.player.posX + offsetX, mc.player.posY + offsetY, mc.player.posZ + offsetZ);
    }
}