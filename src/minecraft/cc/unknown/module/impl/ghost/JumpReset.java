package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Jump Reset", description = "Salta automáticamente al recibir daño para resetear el KB.", category = Category.GHOST)
public class JumpReset extends Module {

	private final NumberValue hurtTime = new NumberValue("Hurt Time", this, 5, 0, 10, 1);
	private final NumberValue offGroundTicks = new NumberValue("Off Ground Ticks", this, 0, 0, 10, 1);
	private final NumberValue onGroundTicks = new NumberValue("On Ground Ticks", this, 1, 0, 10, 1);
	private final NumberValue tickVelocity = new NumberValue("Velocity Ticks Received", this, 14, 0, 30, 1);
	private final NumberValue fallDist = new NumberValue("Fall Distance", this, 3.5, 0, 5, .1);
    private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

    private final BooleanValue onlyClick = new BooleanValue("Require Clicking", this, false);
    private final BooleanValue onlyTarget = new BooleanValue("Require Target", this, false);
    private final BooleanValue disabledWhileHold = new BooleanValue("Disable while holding S", this, false);
    private final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
    private final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump", this, true);

    private EntityPlayer target;
    
	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		assert target != null;
		
		if (!MathUtil.isChance(chance, notWhileSpeed, notWhileJumpBoost) || MathUtil.shouldPerformAction(onlyClick, onlyTarget, disabledWhileHold, target)) {
			return;
		}
		
		if (isEnabled(NoClip.class)) return;
		
        if (mc.player.hurtTime == hurtTime.getValueToInt() && jumpConditions()) {
            event.setJump(true);
        }
	};
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		target = (EntityPlayer) event.getTarget();
	};
	
    private boolean jumpConditions() {
        return mc.player.fallDistance >= fallDist.getValueToFloat() ||
               mc.player.ticksSinceVelocity <= tickVelocity.getValueToInt() ||
               mc.player.onGroundTicks <= onGroundTicks.getValueToInt() ||
               mc.player.offGroundTicks <= offGroundTicks.getValueToInt();
    }
}
