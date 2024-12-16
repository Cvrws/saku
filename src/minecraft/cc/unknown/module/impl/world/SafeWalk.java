package cc.unknown.module.impl.world;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;

@ModuleInfo(aliases = "Safe Walk", description = "Evita que te caigas del borde", category = Category.WORLD)
public class SafeWalk extends Module {

	private final BooleanValue legit = new BooleanValue("Legit [BETA]", this, false);
    private final BooleanValue blocksOnly = new BooleanValue("Blocks Only", this, false, () -> legit.getValue());
    private final BooleanValue backwardsOnly = new BooleanValue("Backwards Only", this, false, () -> legit.getValue());

    private boolean stop = false;
    
	@Override
	public void onDisable() {
		if (PlayerUtil.isOverAir()) {
			stop = false;
		}
	}

    
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
    	if (!legit.getValue()) {
	        mc.player.safeWalk = mc.player.onGround && (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.getValue()) &&
	                ((PlayerUtil.getItemStack() != null && PlayerUtil.getItemStack().getItem() instanceof ItemBlock) ||
	                        !this.blocksOnly.getValue());
    	} else {
    		if (mc.player.onGround && PlayerUtil.isOverAir()) {
    			stop |= isKeyPressed(mc.gameSettings.keyBindForward);
    			stop |= isKeyPressed(mc.gameSettings.keyBindBack);
    			stop |= isKeyPressed(mc.gameSettings.keyBindLeft);
    			stop |= isKeyPressed(mc.gameSettings.keyBindRight);
    			
    			if (stop) MoveUtil.stop();
    		}
    	}
    };
    
    private boolean isKeyPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            keyBinding.pressed = false;
            return true;
        }
        return false;
    }
}