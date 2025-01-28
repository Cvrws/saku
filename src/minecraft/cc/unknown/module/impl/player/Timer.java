package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Timer", description = "Cambia la velocidad del juego.", category = Category.PLAYER)
public final class Timer extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Constant"))
			.add(new SubMode("Random"))
			.add(new SubMode("Ground"))
			.setDefault("Constant");

	private final NumberValue timerSpeed = new NumberValue("Timer Speed", this, 1.5, 0.1, 25, 0.1, () -> !mode.is("Constant") && !mode.is("Random"));
	private final NumberValue variation = new NumberValue("Variation", this,  15, 5, 50, 5, () -> !mode.is("Random"));
	
	private final NumberValue onGroundSpeed = new NumberValue("On Ground Speed", this, 1.5, 0.1, 20, 0.1, () -> !mode.is("Ground"));
	private final NumberValue offGroundSpeed = new NumberValue("Off Ground Speed", this, 1.5, 0.1, 20, 0.1, () -> !mode.is("Ground"));
	
	@Override
	public void onEnable() {
		if (mc.player == null) return;
	}
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1;
	}
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    switch (mode.getValue().getName()) {
	        case "Constant":
	        	mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
	            break;
	        case "Random":
	        	mc.timer.timerSpeed = calculateRandomTimer();
	            break;
	        case "Ground":
	        	mc.timer.timerSpeed = mc.player.onGround ? onGroundSpeed.getValue().floatValue() : offGroundSpeed.getValue().floatValue();
	            break;
	    }
	};

	private float calculateRandomTimer() {
	    float speed = timerSpeed.getValue().floatValue();
	    int variationHalf = variation.getValue().intValue() / 2;
	    float randomFactor = MathUtil.nextSecure(-variationHalf, variationHalf + 1).intValue() / 2.0f;
	    float adjustedTicksPerSec = Math.max(speed + randomFactor, 1.0f);
	    return adjustedTicksPerSec;
	}
}
