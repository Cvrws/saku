package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Auto Block", description = "Bloquea automáticamente", category = Category.GHOST)
public class AutoBlock extends Module {

    private final NumberValue duration = new NumberValue("Block Duration", this, 100, 1, 500, 1);
    private final NumberValue distance = new NumberValue("Distance", this, 3, 0, 6, 0.1);
    private final BooleanValue unBlockwithHurt = new BooleanValue("Unblock with hurtTime", this, false);
    private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

    public boolean block;

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (!isInGame() || mc.currentScreen != null || !InventoryUtil.isSword()) return;
        if (!MathUtil.isChance(chance)) return;

        if (block) {
            if (!Mouse.isButtonDown(0) || getStopWatch().hasFinished() || getStopWatch().getElapsedTime() >= duration.getValueToInt()) {
                block = false;
                releaseKey();
            }
            return;
        }

        if (Mouse.isButtonDown(0) && hasTargetInRange()) {
            block = true;
            getStopWatch().setMillis(duration.getValueToInt());
            getStopWatch().reset();
            
            if (mc.player.hurtTime > 0) {
            	releaseKey();
            }

            ItemStack itemStack = PlayerUtil.getItemStack();
            if (itemStack != null) {
            	mc.gameSettings.keyBindUseItem.setPressed(true);
            }
        }
    };
    
    private void releaseKey() {
    	mc.gameSettings.keyBindUseItem.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem);
    }
    
    private boolean hasTargetInRange() {
        return mc.world.loadedEntityList.stream()
            .filter(entity -> entity instanceof EntityLivingBase)
            .map(entity -> (EntityLivingBase) entity)
            .anyMatch(entity -> mc.player.getDistanceToEntity(entity) <= distance.getValueToFloat());
    }
}
