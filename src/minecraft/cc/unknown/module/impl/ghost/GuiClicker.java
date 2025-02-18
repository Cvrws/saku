package cc.unknown.module.impl.ghost;

import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

@ModuleInfo(aliases = "Gui Clicker", description = "Como un autoclick pero en el inventario.", category = Category.GHOST)
public class GuiClicker extends Module {
	
	private int ticks;
	
	@EventLink
	public final Listener<PreMotionEvent> onMotion = event -> shouldInvClick(mc.currentScreen);

	@SneakyThrows
	private void shouldInvClick(GuiScreen gui) {
		if (gui instanceof GuiContainer) {
			if (Mouse.isButtonDown(0) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42))) {
				ticks++;
				int x = Mouse.getX() * gui.width / mc.displayWidth;
				int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

				if (ticks >= 1) {
					Method mouseClicked = GuiScreen.class.getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
					mouseClicked.setAccessible(true);
					mouseClicked.invoke(gui, x, y, 0);
					ticks = 0;
				}
				return;
			}
		}
	}
}