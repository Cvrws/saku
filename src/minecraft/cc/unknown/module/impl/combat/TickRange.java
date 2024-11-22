package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Tick Range", description = "Congela el juego para acercarte a tu objetivo", category = Category.COMBAT)
public class TickRange extends Module {

	private final NumberValue coolDown = new NumberValue("Delay after dash to be able again to dash", this, 1, 1, 8, 0.5);
	private final NumberValue range2 = new NumberValue("Distance from target to start dashing", this, 3, 3, 6, 0.1);
	private final NumberValue freeze = new NumberValue("Freeze ticks duration on dash", this, 2, 1, 70, 1);
	private final NumberValue packets = new NumberValue("Packets value to send on freeze", this, 2, 1, 70, 1);

	private int durationTicks = 0, waitTicks = 0, delayTicks = 0;
	public static boolean publicFreeze = false;

	@Override
	public void onEnable() {
		clear();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		publicFreeze = false;
		clear();
		super.onDisable();
	}

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame() || getModule(Scaffold.class).isEnabled()) return;
		publicFreeze = false;

		if (waitTicks == 0) {
			waitTicks--;
			for (int i = 0; i < packets.getValue().intValue() * 2.5; i++) {
				mc.world.tick();
			}
		}
		if (waitTicks > 0) {
			waitTicks--;
			publicFreeze = true;
		} else {
		}
		if (delayTicks > 0) {

			delayTicks--;
		}

		if (getModule(KillAura.class).target != null || getModule(AimAssist.class).target != null) {
			double afterRange = RotationUtil.nearestRotation(getModule(KillAura.class).isEnabled() ? getModule(KillAura.class).target.getEntityBoundingBox() : getModule(AimAssist.class).target.getEntityBoundingBox());
			if (afterRange < range2.getValue().floatValue() && afterRange > 3 && mc.gameSettings.keyBindForward.pressed) {
				if (delayTicks > 0) {
				} else {
					waitTicks = (int) (freeze.getValue().intValue() * 2.5);
					delayTicks = (int) (coolDown.getValue().floatValue() * 160);
				}
			}
		} else {
			clear();
			return;
		}
	};

	public void clear() {
		publicFreeze = false;
		durationTicks = 0;
	}

}
