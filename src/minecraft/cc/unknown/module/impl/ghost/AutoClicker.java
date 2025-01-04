package cc.unknown.module.impl.ghost;

import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clickea automáticamente", category = Category.GHOST)
public class AutoClicker extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Left"))
			.add(new SubMode("Right"))
			.add(new SubMode("Both"))
			.setDefault("Left");

	private final BoundsNumberValue leftCPS = new BoundsNumberValue("Left CPS", this, 16, 19, 1, 40, 1, () -> !mode.is("Left") && !mode.is("Both"));
	private final BooleanValue weaponOnly = new BooleanValue("Only Use Weapons", this, false, () -> !mode.is("Left") && !mode.is("Both"));
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true, () -> !mode.is("Left") && !mode.is("Both"));
	private BooleanValue invClicker = new BooleanValue("Auto-Click in Inventory", this, false, () -> !mode.is("Left") && !mode.is("Both"));
	private final NumberValue invDelay = new NumberValue("Click Tick Delay", this, 5, 0, 10, 1, () -> !invClicker.getValue());

	private final BoundsNumberValue rightCPS = new BoundsNumberValue("Right CPS", this, 12, 16, 1, 40, 1, () -> !mode.is("Right") && !mode.is("Both"));
	private final BooleanValue onlyBlocks = new BooleanValue("Only Use Blocks", this, false, () -> !mode.is("Right") && !mode.is("Both"));
	private final BooleanValue allowEat = new BooleanValue("Allow Eating & Drinking", this, true, () -> !mode.is("Right") && !mode.is("Both"));
	private final BooleanValue allowBow = new BooleanValue("Allow Using Bow", this, true, () -> !mode.is("Right") && !mode.is("Both"));

	private boolean breakHeld;
	private int invClick;
	private long leftDelay = 50L;
	private long leftLastSwing = 0L;
	private long rightDelay = 0L;
	private long rightLastSwing = 0L;
	private int clickDelay = 0;
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		switch (mode.getValue().getName()) {
		case "Left":
			leftDelay();
			break;
		case "Right":
			rightDelay();
			break;
		case "Both":
			leftDelay();
			rightDelay();
			break;
		}
	};

	@EventLink
	public final Listener<PreMotionEvent> onMotion = event -> {
		if (invClicker.getValue()) {
			shouldInvClick(mc.currentScreen);
		}
	};
	
	private void leftDelay() {
		Mouse.poll();

		if (!mc.inGameHasFocus || checkScreen() || checkHit()) {
			return;
		}


		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (this.weaponOnly.getValue() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			if (System.currentTimeMillis() - leftLastSwing >= leftDelay) {
				leftLastSwing = System.currentTimeMillis();
				leftDelay = getClickDelay();
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
			} else if (leftLastSwing > leftDelay * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
			}
		}
	}

	private void rightDelay() {
		Mouse.poll();

		if (checkScreen() || !mc.inGameHasFocus)
			return;
		
		if (Mouse.isButtonDown(1)) {
			if (!rightClickAllowed())
				return;

			if (System.currentTimeMillis() - rightLastSwing >= rightDelay) {
				rightLastSwing = System.currentTimeMillis();
				rightDelay = getClickDelay();
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
			} else if (System.currentTimeMillis() - rightLastSwing > rightDelay * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			}
		}
	}

	private boolean breakBlockLogic() {
		if (this.breakBlocks.getValue() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();

			if (p != null) {
				Block bl = mc.theWorld.getBlockState(p).getBlock();
				if (bl != Blocks.air && !(bl instanceof BlockLiquid)) {
					if (!breakHeld) {
						int e = mc.gameSettings.keyBindAttack.getKeyCode();
						KeyBinding.setKeyBindState(e, true);
						KeyBinding.onTick(e);
						breakHeld = true;
					}
					return true;
				}
				if (breakHeld) {
					breakHeld = false;
				}
			}
		}
		return false;
	}

	private boolean rightClickAllowed() {
		ItemStack item = mc.player.getHeldItem();
		if (item != null) {

			if (item.getItem() instanceof ItemSword) {
				return false;
			} else if (item.getItem() instanceof ItemFishingRod) {
				return false;
			} else if (item.getItem() instanceof ItemBow) {
				return false;
			}

			if (allowEat.getValue()) {
				if ((item.getItem() instanceof ItemFood) || item.getItem() instanceof ItemPotion
						|| item.getItem() instanceof ItemBucketMilk) {
					return false;
				}
			}

			if (onlyBlocks.getValue()) {
				if (!(item.getItem() instanceof ItemBlock)) {
					return false;
				}
			}
		}

		return true;
	}

	private int getClickDelay() {
		switch (mode.getValue().getName()) {
		case "Left":
			setClickType(leftCPS.getValue().intValue(), leftCPS.getSecondValue().intValue());
			break;
		case "Right":
			setClickType(rightCPS.getValue().intValue(), rightCPS.getSecondValue().intValue());
			break;
		case "Both":
			setClickType(leftCPS.getValue().intValue(), leftCPS.getSecondValue().intValue());
			setClickType(rightCPS.getValue().intValue(), rightCPS.getSecondValue().intValue());
			break;
		}
		return clickDelay;
	}

	@SneakyThrows
	private void shouldInvClick(GuiScreen gui) {
		if (gui instanceof GuiContainer) {
			if (Mouse.isButtonDown(0) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42))) {
				invClick++;
				int x = Mouse.getX() * gui.width / mc.displayWidth;
				int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

				if (invClick >= invDelay.getValue().intValue()) {
					Method mouseClicked = GuiScreen.class.getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
					mouseClicked.setAccessible(true);
			            
					mouseClicked.invoke(gui, x, y, 0);
					invClick = 0;
				}
				return;
			}
		}
	}
	
	private void setClickType(int min, int max) {
		clickDelay = MathUtil.randomClickDelay(min, max);
	}
	
	public boolean checkScreen() {
		return mc.currentScreen != null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest;
	}

	public boolean checkHit() {
		HitSelect hitSelect = (HitSelect) Sakura.instance.getModuleManager().get(HitSelect.class);
		return hitSelect.isEnabled();
	}
}