package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.Getter;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

@ModuleInfo(aliases = "Ambience", description = "Permite cambiar la hora y el tiempo del juego", category = Category.VISUALS)
public final class Ambience extends Module {

	private final NumberValue time = new NumberValue("Time", this, 0, 0, 22999, 1);
	private final NumberValue speed = new NumberValue("Time Speed", this, 0, 0, 20, 1);

	@Override
	public void onDisable() {
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		mc.theWorld.setWorldTime(
				(long) (time.getValue().intValue() + (System.currentTimeMillis() * speed.getValue().intValue())));
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.player.ticksExisted % 20 == 0) {
			mc.theWorld.setRainStrength(0);
			mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
			mc.theWorld.getWorldInfo().setRainTime(0);
			mc.theWorld.getWorldInfo().setThunderTime(0);
			mc.theWorld.getWorldInfo().setRaining(false);
			mc.theWorld.getWorldInfo().setThundering(false);
		}
	};

	@EventLink
	public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
		if (event.getPacket() instanceof S03PacketTimeUpdate) {
			event.setCancelled();
		} else if (event.getPacket() instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapped = (S2BPacketChangeGameState) event.getPacket();

			if (wrapped.getGameState() == 1 || wrapped.getGameState() == 2) {
				event.setCancelled();
			}
		}
	};
}