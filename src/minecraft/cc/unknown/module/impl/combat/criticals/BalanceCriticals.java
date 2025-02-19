package cc.unknown.module.impl.combat.criticals;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.combat.Criticals;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class BalanceCriticals extends Mode<Criticals> {
	public BalanceCriticals(String name, Criticals parent) {
		super(name, parent);
	}
	
    private long startTimer;
    private boolean delayed = false;
    private boolean attacked;
	
    @Override
    public void onDisable() {
        if (startTimer != -1) {
            mc.timer.timerSpeed = 1.0f;
        }
        startTimer = -1;
    }

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
		if (!MathUtil.isChance(getParent().chance)) return;
 
        attacked = true;
    };

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {        
    	if (mc.player == null) return;

    	if (startTimer != -1) {
    		if (mc.player.onGround || delayed || System.currentTimeMillis() - startTimer > getParent().timerTime.getValueToLong()) {
    			mc.timer.timerSpeed = 1.0f;
    			startTimer = -1;
    			attacked = false;
    		}
    	} else if (mc.player.motionY < 0 && !mc.player.onGround && !delayed && attacked) {
    		if (mc.timer.timerSpeed != getParent().timer.getValueToFloat() && getParent().chance.getValueToDouble() != 100 && Math.random() * 100 > getParent().chance.getValueToDouble()) {
    			delayed = true;
    			return;
    		}

    		if (isTargetNearby(getModule(KillAura.class).isEnabled() ? getModule(KillAura.class).range.getValueToInt() : 3)) {
    			startTimer = System.currentTimeMillis();
    			mc.timer.timerSpeed = getParent().timer.getValueToFloat();
    		}
    	} else if (mc.player.onGround) {
    		delayed = false;
        }
    };

    private boolean isTargetNearby(double dist) {
        return mc.world.playerEntities.stream().filter(target -> target != mc.player).anyMatch(target -> new Vec3(target).distanceTo(mc.player) < dist);
    }
}
