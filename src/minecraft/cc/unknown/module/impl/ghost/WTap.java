package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.Clutch;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "WTap", description = "Suelta brevemente la W después de atacar para aumentar el knockback dado", category = Category.GHOST)
public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Silent"))
			.setDefault("Normal");

	private NumberValue delay = new NumberValue("Delay", this, 500, 50, 1000, 10, () -> !isTwo());
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1, () -> !isTwo());
	private NumberValue hurtTime = new NumberValue("HurtTime", this, 10, 1, 10, 10, () -> !isTwo());
	private BoundsNumberValue hits = new BoundsNumberValue("Hits", this, 1, 2, 0, 10, 1, () -> !isTwo());
	private BooleanValue onlyGround = new BooleanValue("Only Ground", this, false, () -> !isTwo());
	private BooleanValue onlyMove = new BooleanValue("Only Move", this, false, () -> !isTwo());
	private BooleanValue onlyMoveForward = new BooleanValue("Only Forward", this, false, () -> !onlyMove.getValue() || !isTwo());
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false, () -> !isTwo());

	private final StopWatch stopWatch = new StopWatch();
	private int ticks;

	private int hitsSend;
	private int combo;
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		double chanceValue = chance.getValue().doubleValue();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);
		if (!MathUtil.shouldPerformAction(chanceValue, randomFactor)) return;
		if (mc.player == null) return;

		if (event.getTarget().hurtTime > hurtTime.getValue().intValue() || !stopWatch.finished(delay.getValue().intValue()) || (onlyGround.getValue() && !mc.player.onGround)) {
			return;
		}

		if (ignoreTeammates.getValue() && PlayerUtil.isTeam((EntityPlayer) event.getTarget(), true, true)) { 
			return;
		}
		
	    if (event.getTarget().getName().contains("[NPC]") || event.getTarget().getName().contains("MEJORAS") || event.getTarget().getName().contains("CLICK DERECHO")) {
	        return;
	    }

		if (onlyMove.getValue() && (!MoveUtil.isMoving() || (onlyMoveForward.getValue() && mc.player.movementInput.moveStrafe != 0f))) {
			return;
		}
		
		switch (mode.getValue().getName()) {
		case "Normal":
		case "Silent":
			if (stopWatch.finished(delay.getValue().intValue())) {
				stopWatch.reset();
				ticks = hits.getSecondValue().intValue();
			}
			break;
		}
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreUpdate = event -> {
		if (getModule(Scaffold.class).isEnabled() || getModule(LegitScaffold.class).isEnabled() || getModule(Clutch.class).isEnabled()) return;

		if (ticks == hits.getSecondValue().intValue()) {
			switch (mode.getValue().getName()) {
			case "Normal":
				mc.gameSettings.keyBindForward.pressed = false;
				break;
			case "Silent":
				mc.player.sprintingTicksLeft = 1;
				break;
			}
		} else if (ticks == hits.getValue().intValue()) {
			switch (mode.getValue().getName()) {
			case "Normal":
				mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
				break;
			case "Silent":
				mc.player.sprintingTicksLeft = 0;
				break;
			}
		}

		if (ticks > 0) {
			ticks--;
		}
	};

	private boolean isTwo() {
		return mode.is("Normal") || mode.is("Silent");
	}
}