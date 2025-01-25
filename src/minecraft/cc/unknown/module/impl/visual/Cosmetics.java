package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.bluearchive.HaloRenderer;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Cosmetics", description = "Like lunar cosmetics [BETA]", category = Category.VISUALS)
public final class Cosmetics extends Module {
	
	private final ModeValue haloType = new ModeValue("Blue Archive Halo", this)
			.add(new SubMode("Shiroko"))
			.add(new SubMode("Hoshino"))
			.add(new SubMode("Aris"))
			.add(new SubMode("Natsu"))
			.add(new SubMode("Reisa"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public final BooleanValue topHat = new BooleanValue("Top Hat", this, false);
	public final BooleanValue doug = new BooleanValue("Doug Dimmadome", this, false);
	
	public final BooleanValue whiterPet = new BooleanValue("Whiter Pet", this, false);
	public final BooleanValue dogPet = new BooleanValue("Dog Pet", this, false);
	public final BooleanValue bandana = new BooleanValue("Bandana", this, false);
	public final BooleanValue galaxyWings = new BooleanValue("Galaxy Wings", this, false);
	public final BooleanValue witchHat = new BooleanValue("Witch Hat", this, false);
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {        
        switch (haloType.getValue().getName()) {
            case "Shiroko":
                HaloRenderer.drawShirokoHalo(event);
                break;
            case "Hoshino":
                HaloRenderer.drawHoshinoHalo(event);
                break;
            case "Aris":
                HaloRenderer.drawArisHalo(event);
                break;
            case "Natsu":
                HaloRenderer.drawNatsuHalo(event);
                break;
            case "Reisa":
                HaloRenderer.drawReisaHalo(event);
                break;
        }
    };
	
}