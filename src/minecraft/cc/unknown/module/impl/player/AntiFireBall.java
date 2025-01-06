  package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.ItemFireball;
import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

@ModuleInfo(aliases = "Anti Fire Ball", description = "Golpea automaticámente las fireballs", category = Category.PLAYER)
public class AntiFireBall extends Module {

    private final NumberValue cps = new NumberValue("CPS", this, 9, 1, 20, 1);
    public final NumberValue range = new NumberValue("Range", this, 6.0F, 2.0F, 6F, .1f);
    private final BooleanValue customRotation = new BooleanValue("Custom Rotation", this, false);
    private final NumberValue yawRotationSpeed = new NumberValue("Yaw Rotation Speed", this, 10, 0, 10, 1, () -> !customRotation.getValue());
    private final NumberValue pitchRotationSpeed = new NumberValue("Pitch Rotation Speed", this, 10, 0, 10, 1, () -> !customRotation.getValue());
    private final BooleanValue moveFix = new BooleanValue("Move Fix", this, false);
    private final StopWatch attackTimer = new StopWatch();

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (getModule(Scaffold.class).isEnabled() || mc.player.getHeldItem().getItem() instanceof ItemFireball)
            return;
        
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFireball && entity.getDistanceToEntity(mc.player) < range.getValue().floatValue()) {
                if (attackTimer.finished((long) (1000L / (cps.getValue().intValue() + 4)))) {

                    Vector2f finalRotation = RotationUtil.calculate(entity);

                    if (customRotation.getValue()) {
                    	RotationHandler.setRotations(finalRotation, MathHelper.randomInt(yawRotationSpeed.getValue().intValue(), pitchRotationSpeed.getValue().intValue()), moveFix.getValue() ?  MoveFix.SILENT : MoveFix.OFF);
                    } else {
                    	RotationHandler.setRotations(finalRotation, 0, moveFix.getValue() ?  MoveFix.SILENT : MoveFix.OFF);
                    }
                    
                    AttackOrder.sendFixedAttack(mc.player, entity, false);
                    attackTimer.reset();
                }
            }
        }
    };
}