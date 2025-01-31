package cc.unknown.module.impl.ghost;

import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clickea automáticamente", category = Category.GHOST)
public class AutoClicker extends Module {

	private final ModeValue clickMode = new ModeValue("Randomization", this) {
		{
			add(new SubMode("Normal"));
			add(new SubMode("ButterFly"));
			add(new SubMode("Drag"));
			setDefault("Normal");

		}
	};

	private final ModeValue button = new ModeValue("Click Button", this)
			.add(new SubMode("Left"))
			.add(new SubMode("Right"))
			.add(new SubMode("Both"))
			.setDefault("Left");

	private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 8, 14, 1, 20, 0.1);

	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true, () -> !isButtonClick());
	private final BooleanValue guiClicker = new BooleanValue("Gui Clicker", this, false, () -> !isButtonClick());

	private final StopWatch stopWatch = new StopWatch();
	private int ticksDown;
	private int attackTicks;
	private int invClick;
	private int mouseDownTicks = 0;
	private long nextSwing;

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		attackTicks++;
		HitSelect hitSelect = Sakura.instance.getModuleManager().get(HitSelect.class);

		if (hitSelect != null && stopWatch.finished(nextSwing) && (!hitSelect.isEnabled() || ((hitSelect.isEnabled() && attackTicks >= 10) || (mc.player != null && mc.player.hurtTime > 0 && stopWatch.finished(nextSwing)))) && mc.currentScreen == null) {
			final long clicks = (long) (this.cps.getRandomBetween().longValue() * 1.5);

			if (mc.gameSettings.keyBindAttack.isKeyDown()) {
				ticksDown++;
			} else {
				ticksDown = 0;
			}

			switch (clickMode.getValue().getName()) {
			case "Normal":
				this.nextSwing = 1000 / clicks;
				break;
			case "ButterFly":
				if (this.nextSwing >= 100) {
					this.nextSwing = (long) (Math.random() * 100);
				}
				break;
			case "Drag":
				double base = 15;
				double fluctuation = Math.random() * 10 - 5;
				boolean pause = Math.random() < 0.05;

				if (pause) {
					this.nextSwing = (long) (base + 50 + fluctuation);
				} else {
					this.nextSwing = (long) (base + fluctuation);
				}
				break;
			}

			switch (button.getValue().getName()) {
			case "Left":
				handleLeftClick();
				break;
			case "Right":
				handleRightClick();
				break;
			case "Both":
				handleLeftClick();
				handleRightClick();
				break;
			}

			this.stopWatch.reset();
		}
	};

	@EventLink
	public final Listener<PreMotionEvent> onMotion = event -> {
		if (guiClicker.getValue()) {
			shouldInvClick(mc.currentScreen);
		}
	};

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		attackTicks = 0;
	};

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		mc.leftClickCounter = 0;
	};

	private void handleLeftClick() {
		if (ticksDown > 1 && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
			mc.clickMouse();
		}
		
		if (!breakBlocks.getValue()) {
			mc.playerController.curBlockDamageMP = 0;
		}
	}

	private void handleRightClick() {
		if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
			mc.rightClickMouse();

			if (Math.random() > 0.9) {
				mc.rightClickMouse();
			}
		}
	}

	@SneakyThrows
	private void shouldInvClick(GuiScreen gui) {
		if (gui instanceof GuiContainer) {
			if (Mouse.isButtonDown(0) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42))) {
				invClick++;
				int x = Mouse.getX() * gui.width / mc.displayWidth;
				int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

				if (invClick >= 1) {
					Method mouseClicked = GuiScreen.class.getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
					mouseClicked.setAccessible(true);

					mouseClicked.invoke(gui, x, y, 0);
					invClick = 0;
				}
				return;
			}
		}
	}

    private boolean isButtonClick() {
    	return button.is("Left") || button.is("Both");
    }
}