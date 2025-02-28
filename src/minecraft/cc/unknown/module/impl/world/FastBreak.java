package cc.unknown.module.impl.world;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Fast Break", description = "Te permite romper los bloques más rápido", category = Category.WORLD)
public final class FastBreak extends Module {
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Percentage"))
            .add(new SubMode("Instant"))
            .add(new SubMode("Ticks"))
            .setDefault("Ticks");

    private final NumberValue speed = new NumberValue("Speed", this, 50, 0, 100, 1, () -> !mode.is("Percentage"));
    private final NumberValue ticks = new NumberValue("Ticks", this, 1, 1, 50, 1, () -> !mode.is("Ticks"));
    private final BooleanValue ignoringMiningFatigue = new BooleanValue("Ignore Mining Fatigue", this, false);

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (mc.player == null || mc.playerController == null) return;

        if (ignoringMiningFatigue.getValue()) {
            mc.player.removePotionEffect(Potion.digSlowdown.getId());
        }

        mc.playerController.blockHitDelay = 0;

        double faster = 0;

        switch (mode.getValue().getName()) {
            case "Percentage":
                faster = speed.getValue().doubleValue() / 100.0;
                break;

            case "Instant":
                mc.playerController.curBlockDamageMP = 1.0f;
                break;

            case "Ticks":
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                    Block block = PlayerUtil.block(blockPos);

                    if (block != null) {
                        float blockHardness = block.getPlayerRelativeBlockHardness(mc.player, mc.world, blockPos);
                        if (blockHardness > 0) {
                            faster = blockHardness * ticks.getValue().intValue();
                        }
                    }
                }
                break;
        }
        
        if (mc.playerController.curBlockDamageMP > 1.0 - faster && mc.playerController.curBlockDamageMP < 0.99f) {
        	mc.playerController.curBlockDamageMP = 0.99f;
        }
    };
}
