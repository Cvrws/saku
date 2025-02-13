package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Motion Blur", description = "blur", category = Category.VISUALS)
public final class MotionBlur extends Module {
	
	private final NumberValue amount = new NumberValue("Amount", this, 1, 1, 10, 1);
	
	@Override
	public void onDisable() {
        if (mc.entityRenderer.isShaderActive())
            mc.entityRenderer.stopUseShader();
	}
	
    @EventLink
    public final Listener<TickEvent> onTick = event -> {
        if (mc.world != null) {
        	if ((mc.entityRenderer.getShaderGroup() == null))
        		mc.entityRenderer.loadShader(new ResourceLocation("minecraft", "shaders/post/motion_blur.json"));
        	float uniform = 1F - Math.min(amount.getValue().intValue() / 10F, 0.9f);
        	if (mc.entityRenderer.getShaderGroup() != null) {
        		mc.entityRenderer.getShaderGroup().listShaders.get(0).getShaderManager().getShaderUniform("Phosphor").set(uniform, 0F, 0F);
        	}
            
        }
    };
}