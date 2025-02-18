package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.criticals.*;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "Criticals", description = "Consigue un golpe crítico cada vez que atacas", category = Category.COMBAT)
public final class Criticals extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Packet"))
            .add(new VerusCriticals("Verus", this))
            .add(new BalanceCriticals("Balance", this))
            .add(new LegitCriticals("Legit", this))
            .setDefault("Balance");
    
    public final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 1, () -> mode.is("balance"));
    public final NumberValue packets = new NumberValue("Packets", this, 45, 1, 100, 1, () -> !mode.is("Packet"));
    
    public final NumberValue timer = new NumberValue("Timer", this, 0.5, 0, 1, 0.1, () -> !mode.is("balance"));
    public final NumberValue timerTime = new NumberValue("Balance Delay", this, 2000, 100, 3000, 100, () -> !mode.is("balance"));
    public final NumberValue chance = new NumberValue("Chance", this, 90, 0, 100, 1, () -> !mode.is("balance"));
}
