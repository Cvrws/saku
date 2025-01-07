package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Flight", description = "Te concede la capacidad de volar.", category = Category.MOVEMENT)
public class Flight extends Module {

	private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue fakeDamage = new BooleanValue("Fake Damage", this, false);
    private final BooleanValue stopOnDisable = new BooleanValue("Stop on Disable", this, false);

    private boolean teleported;

    @Override
    public void onEnable() {
        if (fakeDamage.getValue() && mc.player.ticksExisted > 1) {
            PlayerUtil.fakeDamage();
        }

        teleported = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        if (stopOnDisable.getValue()) {
            MoveUtil.stop();
        }
    }
    
	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		final float speed = this.speed.getValue().floatValue();

		event.setSpeed(speed);
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		final float speed = this.speed.getValue().floatValue();

		mc.player.motionY = 0.0D + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
				- (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);

	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		event.setSneak(false);
	};

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}