package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@ModuleInfo(aliases = "Ambience", description = "Permite cambiar la hora y el tiempo del juego", category = Category.VISUALS)
public final class Ambience extends Module {

    private final BooleanValue time = new BooleanValue("Time Editor", this, true);
    private final NumberValue timeValue = new NumberValue("Time", this, 18000, 0, 24000, 1000, () -> !time.getValue());
    private final BooleanValue weather = new BooleanValue("Weather Editor", this, true);
    
    private final ModeValue weatherValue = new ModeValue("Weather", this, () -> !weather.getValue())
    		.add(new SubMode("Clean"))
    		.add(new SubMode("Rain"))
    		.add(new SubMode("Thunder"))
    		.setDefault("Clean");
    
	@Override
	public void onDisable() {
		mc.world.setRainStrength(0);
		mc.world.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.world.getWorldInfo().setRainTime(0);
		mc.world.getWorldInfo().setThunderTime(0);
		mc.world.getWorldInfo().setRaining(false);
		mc.world.getWorldInfo().setThundering(false);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        if(time.getValue())
            mc.world.setWorldTime(timeValue.getValueToLong());
        if (weather.getValue()) {
            switch (weatherValue.getValue().getName()) {
                case "Rain":
                    mc.world.setRainStrength(1);
                    mc.world.setThunderStrength(0);
                    break;
                case "Thunder":
                    mc.world.setRainStrength(1);
                    mc.world.setThunderStrength(1);
                    break;
                default:
                    mc.world.setRainStrength(0);
                    mc.world.setThunderStrength(0);
            }
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