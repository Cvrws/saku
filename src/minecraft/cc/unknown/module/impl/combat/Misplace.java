package cc.unknown.module.impl.combat;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.ghost.Reach;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.structure.EvictingList;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Misplace", description = "Permite darles hits a tus enemigos en su posición anterior.", category = Category.COMBAT)
public class Misplace extends Module {

    private final NumberValue amount = new NumberValue("Amount", this, 1, 1, 20, 1);
    private EvictingList<Vector2f> previousRotations = new EvictingList<>(1);
    private boolean attacked;
    private int lastSize;

    @EventLink
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (lastSize != amount.getValue().intValue()) {
            previousRotations = new EvictingList<>(amount.getValue().intValue());
            lastSize = amount.getValue().intValue();
        }

        previousRotations.add(new Vector2f(event.getYaw(), event.getPitch()));

        attacked = false;
    };

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (attacked) {
            event.setCancelled();
        }
        attacked = true;
    };
}