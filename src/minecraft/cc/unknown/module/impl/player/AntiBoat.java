  package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Anti Boat", description = "Previene que el gordo de Trident1964_ se suba al barco", category = Category.PLAYER)
public class AntiBoat extends Module {

    private final NumberValue cps = new NumberValue("CPS", this, 9, 1, 20, 1);
    public final NumberValue range = new NumberValue("Range", this, 3.0, 2.0, 6, .1);
    private final NumberValue yawSpeed = new NumberValue("Yaw Speed", this, 10, 0, 10, 1);
    private final NumberValue pitchSpeed = new NumberValue("Pitch Speed", this, 10, 0, 10, 1);
    private final BooleanValue moveFix = new BooleanValue("Move Fix", this, false);
    private final StopWatch stopWatch = new StopWatch();

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityBoat && entity.getDistanceToEntity(mc.player) < range.getValue().floatValue()) {
                if (stopWatch.finished((long) (1000L / (cps.getValue().intValue() + 4)))) {
                	int yaw = yawSpeed.getValue().intValue();
                	int pitch = pitchSpeed.getValue().intValue();
                    Vector2f finalRotation = RotationUtil.calculate(entity);
                	RotationHandler.setRotations(finalRotation, MathHelper.randomInt(yaw, pitch), moveFix.getValue() ?  MoveFix.SILENT : MoveFix.OFF);
                    AttackOrder.sendFixedAttack(mc.player, entity, false);
                    stopWatch.reset();
                }
            }
        }
    };
}