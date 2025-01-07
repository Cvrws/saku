package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Strafe", description = "Makes you always strafe, letting you move freely in air", category = Category.MOVEMENT)
public class Strafe extends Module {
	
    private NumberValue strength = new NumberValue("Strength", this, 100, 1, 100, 1);
    
    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        MoveUtil.partialStrafePercent(strength.getValue().floatValue());
    };
}