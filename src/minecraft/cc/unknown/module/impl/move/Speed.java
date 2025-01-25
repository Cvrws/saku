package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.speed.*;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Speed", description = "Aumenta tu velocidad de movimiento.", category = Category.MOVEMENT)
public class Speed extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaSpeed("Vanilla", this))
            .add(new BlocksMCSpeed("BlocksMC", this))
            .add(new LegitSpeed("Legit", this))
            .add(new LowSpeed("Low Hop", this))
            .add(new VulcanSpeed("Vulcan", this))
            .add(new NCPSpeed("NCP", this))
            .setDefault("Vanilla");

    public final BooleanValue speed = new BooleanValue("Increase Speed", this, false, () -> !mode.is("Legit"));
    public final NumberValue speedInc = new NumberValue("Speed", this, 1.12, 1, 1.4, 0.1, () -> !mode.is("Legit") || !speed.getValue());
    public final BooleanValue legitStrafe = new BooleanValue("Legit Strafe", this, false, () -> !mode.is("Legit"));
    public final BooleanValue fastFall = new BooleanValue("Fast Fall", this, false, () -> !mode.is("Legit"));
    
    public final NumberValue jumpMotion = new NumberValue("Jump Motion", this, 0.4, 0.4, 0.42, 0.01, () -> !mode.is("NCP"));
    public final NumberValue groundSpeed = new NumberValue("Ground Speed", this, 1.75, 0.1, 2.5, 0.05, () -> !mode.is("NCP"));
    public final NumberValue bunnySlope = new NumberValue("Bunny Slope", this, 0.66, 0, 1, 0.01, () -> !mode.is("NCP"));
    public final NumberValue timer = new NumberValue("Timer", this, 1, 0.1, 10, 0.05, () -> !mode.is("NCP"));
    public final BooleanValue boost = new BooleanValue("Damage Boost", this, true, () -> !mode.is("NCP"));
    public final NumberValue boostSpeed = new NumberValue("Boost Speed", this, .8, 0.1, 9.5, 0.1, () -> !mode.is("NCP") || !boost.getValue());
    public final BooleanValue hurtBoost = new BooleanValue("Hurt Boost", this, false, () -> !mode.is("NCP"));
    public final NumberValue hurTime = new NumberValue("Hurt Time", this, 6, 1, 10, 1, () -> !mode.is("NCP") || !hurtBoost.getValue());
    public final BooleanValue lowHop = new BooleanValue("Low Hop", this, false, () -> !mode.is("NCP"));
    public final BooleanValue yPort = new BooleanValue("Y-port Hop", this, false, () -> !mode.is("NCP"));
    
    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue stopOnDisable = new BooleanValue("Stop on Disable", this, false);

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        if (stopOnDisable.getValue()) {
            MoveUtil.stop();
        }
    }

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}