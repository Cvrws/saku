package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.flight.*;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Flight", description = "Te concede la capacidad de volar.", category = Category.MOVEMENT)
public class Flight extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaFlight("Vanilla", this))
            .add(new AirWalkFlight("Air Walk", this))
            .add(new LatestNCPFlight("Latest NCP", this))
            .add(new VerusFlight("Verus", this))
            .add(new BlockFlight("Block", this))
            .add(new AirJumpFlight("Air Jump", this))
            .add(new NewVulcanFlight("Vulcan", this))
            .setDefault("Vanilla");
    
    public final ModeValue ncpMode = new ModeValue("NCP Mode", this, () -> !mode.is("Latest NCP"))
            .add(new SubMode("Normal"))
            .add(new SubMode("Clip"))
            .setDefault("Normal");

	public final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1, () -> !mode.is("Vanilla") || !mode.is("Vulcan"));

    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue viewBobbing = new BooleanValue("View Bobbing", this, false);
    private final BooleanValue fakeDamage = new BooleanValue("Fake Damage", this, false);

    @Override
    public void onEnable() {
        if (fakeDamage.getValue() && mc.player.ticksExisted > 1) {
            PlayerUtil.fakeDamage();
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (viewBobbing.getValue()) {
            mc.player.cameraYaw = 0.1F;
        }
    };

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}