package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Derp", description = "Makes you look like a derp", category = Category.PLAYER)
public class Derp extends Module {

	private float yaw, pitch;
	
	private NumberValue constantSpeed = new NumberValue("Constant Speed", this, 10, 10, 180, 0.1);
	
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {

    	pitch = mc.player.rotationPitch;
    	yaw += constantSpeed.getValue().floatValue();
        

        event.setYaw(yaw);
        event.setPitch(pitch);
        
        mc.player.renderYawOffset = yaw;
        mc.player.rotationYawHead = yaw;
        
        RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), constantSpeed.getValue().intValue(), MovementFix.SILENT);
    };
}