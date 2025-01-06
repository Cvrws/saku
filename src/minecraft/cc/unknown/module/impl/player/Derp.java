package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Derp", description = "ahegao", category = Category.PLAYER)
public class Derp extends Module {

	private float yaw, pitch;
	
	private NumberValue constantSpeed = new NumberValue("Constant Speed", this, 10, 10, 180, 0.1);
	private BooleanValue movefix = new BooleanValue("Movement Fix", this, true);
	
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
    	pitch = mc.player.rotationPitch;
    	yaw += constantSpeed.getValue().floatValue();
        
        event.setYaw(yaw);
        event.setPitch(pitch);
        
        mc.player.renderYawOffset = yaw;
        mc.player.rotationYawHead = yaw;
    };
    
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> RotationHandler.setCorrectMovement(movefix.getValue() ? MoveFix.SILENT : MoveFix.OFF);
}