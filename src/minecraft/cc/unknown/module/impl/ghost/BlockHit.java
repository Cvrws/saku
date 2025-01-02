package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Block Hit", description = "Block hitea automáticamente", category = Category.GHOST)
public class BlockHit extends Module {
	
	private final NumberValue time = new NumberValue("Block Time", this, 150, 1, 500, 1);
	private final NumberValue delay = new NumberValue("Delay", this, 250, 1, 500, 1);
	private final BoundsNumberValue distance = new BoundsNumberValue("Distance", this, 1.3, 3.5, 0, 10, 1);
	
	private boolean mouseButtonDown = false;
	private long lastHitTime = 0;
	private long lastActivationTime = 0;
	private boolean isBlockActivated = false;
	private boolean isDelayOver = false;
	
	@Override
	public void onEnable() {
		PlayerUtil.displayInClient("Este modulo esta en fase beta");
	}
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
	    boolean isMouseButtonDown = Mouse.isButtonDown(0);
	    Entity target = event.getTarget();
	    long currentTime = System.currentTimeMillis();

	    if (PlayerUtil.isHoldingWeapon()) {
	        if (target != null && isMouseButtonDown && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
	            double distance = mc.player.getDistanceToEntity(target);

	            if (distance >= this.distance.getValue().doubleValue() && distance <= this.distance.getSecondValue().doubleValue()) {
	                if (!isBlockActivated) {
	                    lastHitTime = currentTime;
	                    isBlockActivated = true;

	                    PlayerUtil.displayInClient("block");
	                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
	                }

	                if (isBlockActivated) {
	                    if (currentTime - lastHitTime >= time.getValue().longValue()) {
	                        if (isBlockActivated) {
	                            isBlockActivated = false;
	                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	                            PlayerUtil.displayInClient("unblock");
	                        } else {
	                            isBlockActivated = true;
	                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
	                            PlayerUtil.displayInClient("block");
	                        }
	                        lastHitTime = currentTime;
	                    }
	                }
	            }
	        }

	        if (target == null || !isMouseButtonDown) {
	            if (isBlockActivated) {
	                isBlockActivated = false;
	                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	                PlayerUtil.displayInClient("unblock");
	            }
	        }
	    }
	};
}