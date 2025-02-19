package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Hit Select", description = "Escoge el mejor momento para atacar.", category = Category.GHOST)
public class HitSelect extends Module {
	
    private final NumberValue delay = new NumberValue("Delay", this, 420, 50, 500, 10);
    private final NumberValue chance = new NumberValue("Chance", this, 80, 0, 100, 1);
    
    private long attackTime = 0;
    private boolean currentShouldAttack = false;
    
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<AttackEvent> onAttackEvent = event -> {
        if (!currentShouldAttack) {
            event.setCancelled(true);
            return;
        }

        attackTime = System.currentTimeMillis();
    };
    
	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		currentShouldAttack = false;
        if (!MathUtil.isChance(chance)) {
            currentShouldAttack = true;
        } else if (!currentShouldAttack) {
                currentShouldAttack = System.currentTimeMillis() - attackTime >= delay.getValue().intValue();
        }
	};
    
    public boolean canAttack(Entity target) {
        return canSwing();
    }

    public boolean canSwing() {
        if (!isEnabled()) return true;
        return currentShouldAttack;
    }

}