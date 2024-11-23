package cc.unknown.module.impl.ghost;

import java.security.SecureRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker V2", description = "Clickea automáticamente [BETA]", category = Category.GHOST)
public class AutoClicker2 extends Module {

	private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 10, 14, 1, 20, 1);
	private final NumberValue cpsCap = new NumberValue("CPS Cap", this, 18.0, 0.0, 40.0, 1);
	private final BoundsNumberValue cpsCapR = new BoundsNumberValue("CPS Cap ReTick", this, 18, 18, 0, 40, 1);

	private final BooleanValue spikes = new BooleanValue("Spikes", this, false);
	private final BoundsNumberValue spikeCps = new BoundsNumberValue("Spike CPS", this, 4, 6, 1, 40, 1, () -> !spikes.getValue());
	private final BoundsNumberValue spikesDelay = new BoundsNumberValue("Spikes Delay", this, 700, 850, 1, 1000, 1, () -> !spikes.getValue());
	private final BoundsNumberValue spikesDuration = new BoundsNumberValue("Spikes Duration", this, 200, 300, 1, 1000, 1, () -> !spikes.getValue());

	private final BooleanValue drops = new BooleanValue("Drops", this, false);
	private final BoundsNumberValue dropsCps = new BoundsNumberValue("Drops CPS", this, 4, 6, 1, 40, 1, () -> !drops.getValue());
	private final BoundsNumberValue dropsDelay = new BoundsNumberValue("Drops Delay", this, 700, 850, 1, 1000, 1, () -> !drops.getValue());
	private final BoundsNumberValue dropsDuration = new BoundsNumberValue("Drops Duration", this, 200, 300, 1, 1000, 1, () -> !drops.getValue());

	private final StopWatch timeHelper = new StopWatch();
	private final StopWatch spikesDelayTimeHelper = new StopWatch();
	private final StopWatch spikesDurationTimeHelper = new StopWatch();
	private final StopWatch dropsDelayTimeHelper = new StopWatch();
	private final StopWatch dropsDurationTimeHelper = new StopWatch();

	private long randomDelay = 100L;
	private long nextSpikeDelay = 0, spikeDuration = 0;
	private long nextDropDelay = 0, dropDuration = 0;
	private boolean spike = false, drop = false;

	@Override
	public void onEnable() {
		super.onEnable();
		randomDelay = 0L;
	}

	@EventLink
	public final Listener<NaturalPressEvent> onNatural = event -> {
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air) return;

		if (Mouse.isButtonDown(0) && mc.currentScreen == null) {
			if (!mc.player.isUsingItem()) {
				mc.clickMouse();
				timeHelper.reset();
				setRandomDelay();
			} else {
				if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
					mc.playerController.onStoppedUsingItem(mc.player);
				}

				while (mc.gameSettings.keyBindAttack.isPressed()) {
				}

				while (mc.gameSettings.keyBindUseItem.isPressed()) {
				}

				while (mc.gameSettings.keyBindPickBlock.isPressed()) {
				}
			}

			if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.rightClickDelayTimer == 0 && !mc.player.isUsingItem()) {
				mc.rightClickMouse();
			}

			if (mc.currentScreen == null) {
			}

			mc.sendClickBlockToController(false);
			event.setCancelled();
		}
	};
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        mc.leftClickCounter = 0;
    };

	private void setRandomDelay() {
		randomDelay = (long) MathUtil.nextSecureInt((int) cps.getValue().intValue(), (int) cps.getSecondValue().intValue());

		if (spikes.getValue()) {
			if (spikesDelayTimeHelper.reached(nextSpikeDelay) || !spikesDurationTimeHelper.reached(spikeDuration) && !drop) {
				long spikeCPS = 0;
				if (spikeCps.getValue().intValue() < spikeCps.getSecondValue().intValue()) {
					spikeCPS = (long) MathUtil.nextSecureDouble((long) (spikeCps.getValue().intValue()),
							(long) (spikeCps.getSecondValue().intValue()));
				} else {
					spikeCPS = (long) (spikeCps.getValue().intValue());
				}
				double expectedCPS = (int) (1000.0 / randomDelay) + spikeCPS;
				long processedDelay = (long) (1000.0 / expectedCPS);
				randomDelay = (long) (1000.0 / expectedCPS);
				spike = true;

				if (spikesDurationTimeHelper.reached(spikeDuration)) {
					if (spikesDelay.getValue().intValue() < spikesDelay.getSecondValue().intValue()) {
						nextSpikeDelay = (long) MathUtil.nextSecureDouble((long) (spikesDelay.getValue().intValue()),
								(long) (spikesDelay.getSecondValue().intValue()));
					} else {
						nextSpikeDelay = (long) spikesDelay.getValue().intValue();
					}
					if (spikesDuration.getValue().intValue() < spikesDuration.getSecondValue().intValue()) {
						spikeDuration = (long) MathUtil.nextSecureDouble((long) (spikesDuration.getValue().intValue()),
								(long) (spikesDuration.getSecondValue().intValue()));
					} else {
						spikeDuration = (long) spikesDuration.getValue().intValue();
					}
					spike = false;
					spikesDurationTimeHelper.reset();
					spikesDelayTimeHelper.reset();
				}
			}
		}
		
		if (drops.getValue()) {
			if (dropsDelayTimeHelper.reached(nextDropDelay)
					|| !dropsDurationTimeHelper.reached(dropDuration) && !spike) {
				long dropCPS = 0;
				if (spikeCps.getValue().intValue() < spikeCps.getSecondValue().intValue()) {
					dropCPS = (long) MathUtil.nextSecureDouble((long) (spikeCps.getValue().intValue()),
							(long) (spikeCps.getSecondValue().intValue()));
				} else {
					dropCPS = (long) (spikeCps.getValue().intValue());
				}
				double expectedCPS = (int) (1000.0 / randomDelay) - dropCPS;
				long processedDelay = (long) (1000.0 / expectedCPS);
				randomDelay = (long) (1000.0 / expectedCPS);
				drop = true;

				if (dropsDurationTimeHelper.reached(dropDuration)) {
					if (dropsDelay.getValue().intValue() < dropsDelay.getSecondValue().intValue()) {
						nextDropDelay = (long) MathUtil.nextSecureDouble((long) (dropsDelay.getValue().intValue()),
								(long) (dropsDelay.getSecondValue().intValue()));
					} else {
						nextDropDelay = (long) dropsDelay.getValue().intValue();
					}
					if (dropsDuration.getValue().intValue() < dropsDuration.getSecondValue().intValue()) {
						dropDuration = (long) MathUtil.nextSecureDouble((long) (dropsDuration.getValue().intValue()),
								(long) (dropsDuration.getSecondValue().intValue()));
					} else {
						dropDuration = (long) dropsDuration.getValue().intValue();
					}
					drop = false;
					dropsDurationTimeHelper.reset();
					dropsDelayTimeHelper.reset();
				}
			}
		}

		if (cpsCap.getValue().intValue() > 0 && 1000.0 / randomDelay > cpsCap.getValue().intValue()) {
			if (!(cpsCapR.getValue().intValue() > cpsCap.getValue().intValue() && cpsCapR.getSecondValue().intValue() > cpsCap.getValue().intValue())) {
				randomDelay = (long) MathUtil.nextSecureDouble((long) (1000.0 / cpsCapR.getValue().intValue()), (long) (1000.0 / cpsCapR.getSecondValue().intValue()));
			} else {
				randomDelay = (long) (1000.0 / cpsCap.getValue().longValue());
			}
		}
	}
}