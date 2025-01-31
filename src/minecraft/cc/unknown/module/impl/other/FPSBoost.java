package cc.unknown.module.impl.other;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.Minecraft;

@ModuleInfo(aliases = "FPS Boost", description = "Incrementa tus FPS", category = Category.OTHER)
public final class FPSBoost extends Module {
	
	public BooleanValue noCrosshair = new BooleanValue("No Crosshair", this, false);
	public BooleanValue noSkins = new BooleanValue("No Skins", this, false);
	public BooleanValue noCapes = new BooleanValue("No Capes", this, false);
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", this, false);
	private BooleanValue idle = new BooleanValue("Idle FPS", this, true);
	public BooleanValue noItemFrame = new BooleanValue("No Item Frames", this, true);
	public BooleanValue noRenderPortal = new BooleanValue("No Render Portal", this, false);
	public BooleanValue noPumpkinOverlay = new BooleanValue("No Pumpkin Overlay", this, true);
	public BooleanValue noBossHealth = new BooleanValue("No Boss Health", this, true);
	
	private ArrayList<GLTask> glTasks  = new ArrayList<>();
	private int before = 0;

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		if (idle.getValue()) {
			if (Display.isActive() && before != -1) {
				mc.gameSettings.limitFramerate = before;
				before = -1;
			} else if (!Display.isActive()) {
				if (before == -1) before = mc.gameSettings.limitFramerate;
				mc.gameSettings.limitFramerate = 15;
			}
		}
		
		glTasks.forEach(GLTask::run);
		glTasks.clear();
	};
	
	public interface GLTask {
	    void run();
	}
}
