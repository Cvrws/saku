package cc.unknown.module.impl.world;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

@ModuleInfo(aliases = "Legit Scaffold", description = "Shiftea al borde de cada bloque", category = Category.WORLD)
public class LegitScaffold extends Module {
	
	private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 100, 200, 0, 500, 1);
	private final BooleanValue pitchCheck = new BooleanValue("Pitch Check", this, true);
	private final BoundsNumberValue pitchRange = new BoundsNumberValue("Pitch Range", this, 70, 85, 0, 90, 1, () -> !pitchCheck.getValue());
	private final BooleanValue legit = new BooleanValue("Legitimize", this, true);
	private final BooleanValue holdShift = new BooleanValue("Hold Shift", this, false);
	private final BooleanValue slotSwap = new BooleanValue("Block Switching", this, true);
	private final BooleanValue blocksOnly = new BooleanValue("Blocks Only", this, true);
	private final BooleanValue backwards = new BooleanValue("Backwards Movement Only", this, true);
	private final BooleanValue spoof = new BooleanValue("Spoof Slot", this, true);
	private final BooleanValue hideSneak = new BooleanValue("Hide Sneak Animation", this, false);

	private boolean shouldBridge, isShifting = false;
	private int lastSlot;
	private StopWatch stopWatch = new StopWatch();

	@Override
	public void onEnable() {
		lastSlot = -1;		
	}
	
	@Override
	public void onDisable() {
		setSneak(false);
		if (overAir()) {
			setSneak(false);
		}

		mc.player.inventory.currentItem = lastSlot;
		SpoofHandler.stopSpoofing();
		shouldBridge = false;
	}

	@EventLink(value = Priority.LOW)
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (!(mc.currentScreen == null) || !isInGame()) return;

		if (hideSneak.getValue()) {
			mc.player.hideSneakHeight.reset();
		}
		
		boolean shift = delay.getSecondValue().intValue() > 0;

		if (mc.player.rotationPitch < pitchRange.getValue().floatValue() || mc.player.rotationPitch > pitchRange.getSecondValue().floatValue()) {
			shouldBridge = false;
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				setSneak(true);
			}
			return;
		}

		if (holdShift.getValue()) {
			if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				shouldBridge = false;
				return;
			}
		}

		if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
			return;
		}

		if (blocksOnly.getValue()) {
			ItemStack i = mc.player.getHeldItem();
			if (i == null || !(i.getItem() instanceof ItemBlock)) {
				if (isShifting) {
					isShifting = false;
					setSneak(false);
				}
				return;
			}
		}

		if (backwards.getValue()) {
			if ((mc.player.movementInput.moveForward > 0) && (mc.player.movementInput.moveStrafe == 0)
					|| mc.player.movementInput.moveForward >= 0) {
				shouldBridge = false;
				return;
			}
		}

		if (mc.player.onGround) {
			if (overAir()) {
				if (shift) {
					stopWatch.setMillis(MathHelper.randomInt(delay.getValue().intValue(),
							(int) (delay.getSecondValue().intValue() + 0.1)));
					stopWatch.reset();
				}

				isShifting = true;
				setSneak(true);
				shouldBridge = true;
			} else if (mc.player.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
					&& holdShift.getValue()) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (holdShift.getValue() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (mc.player.isSneaking()
					&& (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && holdShift.getValue())
					&& (!shift || stopWatch.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			} else if (mc.player.isSneaking() && !holdShift.getValue() && (!shift || stopWatch.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			}
		} else if (shouldBridge && mc.player.capabilities.isFlying) {
			setSneak(false);
			shouldBridge = false;
		} else if (shouldBridge && overAir() && legit.getValue()) {
			isShifting = true;
			setSneak(true);
		} else {
			isShifting = false;
			setSneak(false);
		}
	};

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
        
        if (slotSwap.getValue() && shouldSkipBlockCheck()) {
        	mc.player.inventory.currentItem = slot;
        }
        
        if (spoof.getValue()) SpoofHandler.startSpoofing(lastSlot);

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
	
	private void setSneak(boolean sneak) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sneak);		
	}
	
	private boolean overAir() {
		return blockRelativeToPlayer(0, -1, 0) instanceof BlockAir;
	}
	

	private Block getBlock(final double x, final double y, final double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

	private Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return getBlock(mc.player.posX + offsetX, mc.player.posY + offsetY, mc.player.posZ + offsetZ);
    }
}