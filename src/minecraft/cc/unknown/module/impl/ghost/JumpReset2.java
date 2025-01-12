package cc.unknown.module.impl.ghost;

import org.jetbrains.annotations.NotNull;

import com.ibm.icu.impl.duration.impl.Utils;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreLivingUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Jump Reset V2", description = "Salta automaticamente al recibir daño para resetear el kb.", category = Category.GHOST)
public class JumpReset2 extends Module {
	
	private final NumberValue xzOnHit = new NumberValue("XZ on Hit", this, 0.6, 0, 1, 0.1);
	private final NumberValue xzOnSprintHit = new NumberValue("XZ on sprint hit", this, 0.6, 0, 1, 0.1);
	private final BooleanValue reduceUnnecessarySlowdown = new BooleanValue("Reduce unnecessary slowdown", this, false);
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);
	private final BooleanValue jump = new BooleanValue("Jump", this, false);
	private final BooleanValue jumpInInv = new BooleanValue("Jump in inv", this, false, () -> !jump.getValue());
	private final NumberValue jumpChance = new NumberValue("Jump Chance", this, 100, 0, 100, 1, () -> !jump.getValue());
	private final BooleanValue notWhileSpeed = new BooleanValue("Not while speed", this, false);
	private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while jump boost", this, false);
	private final BooleanValue debug = new BooleanValue("Debug", this, false);

    private boolean reduced = false;

    @Override
    public void onEnable() {
        reduced = false;
    }
    
    @EventLink
    public final Listener<PreLivingUpdateEvent> onPreLivingUpdate = event -> {
        if (noAction()) return;

        if (jump.getValue()) {
            if (Math.random() > jumpChance.getValue().intValue() / 100) return;

            if (mc.player.onGround && (jumpInInv.getValue() || mc.currentScreen == null))
                mc.player.jump();
        }
        reduced = false;
    };

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (event.getTarget() instanceof EntityLivingBase && mc.player.hurtTime > 0) {
            if (noAction()) return;
            if (Math.random() > chance.getValue().intValue() / 100) return;
            if (reduceUnnecessarySlowdown.getValue() && reduced) return;

            if (mc.player.isSprinting()) {
                mc.player.motionX *= xzOnSprintHit.getValue().doubleValue();
                mc.player.motionZ *= xzOnSprintHit.getValue().doubleValue();
            } else {
                mc.player.motionX *= xzOnHit.getValue().doubleValue();
                mc.player.motionZ *= xzOnHit.getValue().doubleValue();
            }
            reduced = true;
            if (debug.getValue()) {
            	PlayerUtil.displayInClient(String.format("Reduced %.3f %.3f", mc.player.motionX, mc.player.motionZ));
            }            	
        }
    };
    
    private boolean noAction() {
        return mc.player.getActivePotionEffects().parallelStream().anyMatch(effect -> notWhileSpeed.getValue() && effect.getPotionID() == Potion.moveSpeed.getId() || notWhileJumpBoost.getValue() && effect.getPotionID() == Potion.jump.getId());
    }

}