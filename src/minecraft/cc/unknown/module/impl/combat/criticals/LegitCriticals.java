package cc.unknown.module.impl.combat.criticals;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.impl.combat.Criticals;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class LegitCriticals extends Mode<Criticals> {
	public LegitCriticals(String name, Criticals parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {        
		if (!MathUtil.isChance(getParent().chance)) return;
        
		if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock().isCollidable()) {
			if(mc.player.onGround && mc.player.jumpMovementFactor == 0.125)
				mc.player.jump();
		}
    };
}
