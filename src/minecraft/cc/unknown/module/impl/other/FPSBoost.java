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

@ModuleInfo(aliases = "FPS Boost", description = "Incrementa tus FPS [BETA]", category = Category.OTHER)
public final class FPSBoost extends Module {
	
	public BooleanValue noSkins = new BooleanValue("Save memory by not loading minecraft skins", this, true);
	public BooleanValue noCapes = new BooleanValue("Save memory by not loading minecraft capes", this, true);
	private BooleanValue chunkDynamic = new BooleanValue("Chunk Updates Dynamic", this, true);
	private BooleanValue smartAnimations = new BooleanValue("Smart Animations", this, true);
	private BooleanValue renderRegions = new BooleanValue("Render Regions", this, true);
	private BooleanValue showErrors = new BooleanValue("Show GL Errors", this, false);
	private BooleanValue fastRender = new BooleanValue("Fast Render", this, true);
	private BooleanValue smoothFps = new BooleanValue("Smooth FPS", this, false);
	private NumberValue scale = new NumberValue("Gui Scale", this, 2, 1, 3, 1);
	private BooleanValue fastMath = new BooleanValue("Fast Math", this, true);
	private BooleanValue idle = new BooleanValue("Idle FPS", this, true);
	private BooleanValue vbo = new BooleanValue("Vbo", this, true);
	
	private ArrayList<GLTask> glTasks  = new ArrayList<>();
	private int before = 0;
	
	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		mc.gameSettings.ofFastRender = fastRender.getValue();
		mc.gameSettings.ofChunkUpdatesDynamic = chunkDynamic.getValue();
		mc.gameSettings.ofSmartAnimations = smartAnimations.getValue();
        mc.gameSettings.ofShowGlErrors = showErrors.getValue();
        mc.gameSettings.ofRenderRegions = renderRegions.getValue();
    	mc.gameSettings.ofSmoothFps = smoothFps.getValue();
        mc.gameSettings.ofFastMath = fastMath.getValue();
        mc.gameSettings.useVbo = vbo.getValue();
        mc.gameSettings.guiScale = scale.getValue().intValue();
	};
	
	@EventLink
	public final Listener<Render2DEvent> eventRender = event -> {
		if (idle.getValue() && this.isEnabled()) {
			if (Display.isActive() && before != -1) {
				mc.gameSettings.limitFramerate = before;
				before = -1;
			} else if (!Display.isActive()) {
				if (before == -1)
					before = Minecraft.getMinecraft().gameSettings.limitFramerate;
				mc.gameSettings.limitFramerate = 15;
			}
		}
		try {
			glTasks.forEach(GLTask::run);
			glTasks.clear();
		} catch (Exception ex) {
			glTasks.clear();
		}
	};
	
	public interface GLTask {
	    void run();
	}
}
