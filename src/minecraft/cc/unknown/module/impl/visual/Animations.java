package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.SwingAnimationEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Animations", description = "Ajusta las animaciones de los items", category = Category.VISUALS)
public final class Animations extends Module {

	public final BooleanValue blockHit = new BooleanValue("1.7 Block", this, true);
	public final BooleanValue bow = new BooleanValue("1.7 Bow", this, true);
	public final BooleanValue rod = new BooleanValue("1.7 Rod", this, true);
	public final BooleanValue eat = new BooleanValue("1.7 Eat", this, true);
    public final NumberValue swingSpeed = new NumberValue("Swing Speed", this, 1, -200, 50, 1);

    @EventLink
    public final Listener<SwingAnimationEvent> onSwingAnimation = event -> {
        int swingAnimationEnd = event.getAnimationEnd();
        swingAnimationEnd *= (-swingSpeed.getValue().floatValue() / 100f) + 1f;
        event.setAnimationEnd(swingAnimationEnd);
    };
}