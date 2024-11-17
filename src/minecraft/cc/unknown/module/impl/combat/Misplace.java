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
import cc.unknown.util.EvictingList;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Misplace", description = "Allows you to hit entities in their previous locations", category = Category.COMBAT)
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
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C0APacketAnimation && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            for (final Vector2f rotation : previousRotations) {
                final Reach reach = this.getModule(Reach.class);
                final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(rotation, reach.isEnabled() ? 3.0D + reach.range.getValue().doubleValue() : 3.0D);

                if (movingObjectPosition.entityHit != null && !attacked && movingObjectPosition.entityHit instanceof EntityLivingBase) {
                    final AttackEvent e = new AttackEvent((EntityLivingBase) movingObjectPosition.entityHit);
                    Sakura.instance.getEventBus().handle(e);

                    if (e.isCancelled()) return;
                    mc.playerController.attackEntity(mc.player, movingObjectPosition.entityHit);
                }
            }
        }
    };

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (attacked) {
            event.setCancelled();
        }
        attacked = true;
    };
}