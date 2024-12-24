package cc.unknown.module.impl.other;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

@ModuleInfo(aliases = "FPS Boost", description = "Incrementa tus FPS", category = Category.OTHER)
public final class FPSBoost extends Module {
	
	public BooleanValue noSkins = new BooleanValue("Save memory by not loading minecraft skins", this, false);
	public BooleanValue noCapes = new BooleanValue("Save memory by not loading minecraft capes", this, true);
	private BooleanValue cancelParticles = new BooleanValue("Cancel Particles", this, false);
	private BooleanValue idle = new BooleanValue("Idle FPS", this, false);
	
	private ArrayList<GLTask> glTasks  = new ArrayList<>();
	private int before = 0;

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if (cancelParticles.getValue() && event.getPacket() instanceof S2APacketParticles) {
        	event.setCancelled();
        }
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
