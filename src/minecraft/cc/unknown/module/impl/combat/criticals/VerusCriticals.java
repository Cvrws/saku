package cc.unknown.module.impl.combat.criticals;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.impl.combat.Criticals;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VerusCriticals extends Mode<Criticals> {
	public VerusCriticals(String name, Criticals parent) {
		super(name, parent);
	}
	
    private final double[] offsets = new double[]{0.0625, 0};

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
		if (!MathUtil.isChance(getParent().chance)) return;
        
        if (getStopWatch().finished(getParent().delay.getValueToLong()) && mc.player.onGroundTicks > 2 && mc.player.hurtTime != 0) {
            for (double offset : offsets) {
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
            }
            mc.player.onCriticalHit(event.getTarget());
            getStopWatch().reset();
        }
    };
}
