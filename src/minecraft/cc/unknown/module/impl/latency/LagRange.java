package cc.unknown.module.impl.latency;

import java.util.Comparator;

import cc.unknown.component.impl.player.BotComponent;
import cc.unknown.component.impl.player.FriendComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.geometry.Doble;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import lombok.SneakyThrows;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Lag Range", description = "Retiene los datos del servidor ocasionando lag", category = Category.LATENCY)
public class LagRange extends Module {
	
	private final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.6, 5, 0, 8, 0.1);
	private final NumberValue lagTime = new NumberValue("Lag Time", this, 50, 0, 500, 10);
	private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 2000, 50);
	private final BooleanValue checkTeams = new BooleanValue("Check Teams", this, false);
	private long lastLagTime = 0;
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
        if (!shouldStart()) {
            return;
        }

        try {
			Thread.sleep(lagTime.getValue().intValue());
		} catch (InterruptedException e) {
		}
        lastLagTime = System.currentTimeMillis();
	};
	
    private boolean shouldStart() {
        if (!isInGame()) return false;
        if (!MoveUtil.isMoving()) return false;
        if (System.currentTimeMillis() - lastLagTime < delay.getValue().longValue()) return false;

        EntityPlayer target = mc.world.playerEntities.stream()
                .filter(p -> p != mc.player)
                .filter(p -> !checkTeams.getValue() || !PlayerUtil.sameTeam(p))
                .filter(p -> !FriendComponent.isFriend(p))
                .filter(p -> !getComponent(BotComponent.class).contains(p))
                .map(p -> new Doble<>(p, mc.player.getDistanceSqToEntity(p)))
                .min(Comparator.comparing(Doble::getSecond))
                .map(Doble::getFirst)
                .orElse(null);

        if (target == null) return false;

        double distance = new Vec3(target).distanceTo(mc.player);
        return distance >= range.getValue().doubleValue() && distance <= range.getSecondValue().doubleValue();
    }

}
