package cc.unknown.module.impl.visual;

import java.util.Collections;

import cc.unknown.cosmetics.api.CapeType;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.cosmetics.BlueArchive;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Cosmetics", description = "Like lunar cosmetics [BETA]", category = Category.VISUALS)
public final class Cosmetics extends Module {
	
	private final ListValue<BlueArchive> haloType = new ListValue<>("Halo Type", this);
	
	public final ListValue<CapeType> capeType = new ListValue<>("Cape Type", this);
	
	public final ModeValue hatType = new ModeValue("Hat Type", this)
			.add(new SubMode("Top"))
			.add(new SubMode("Dimmadome"))
			.add(new SubMode("Witch"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public final ModeValue petType = new ModeValue("Pet Type", this)
			.add(new SubMode("Whiter"))
			.add(new SubMode("Dog"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public final ModeValue auraType = new ModeValue("Aura Type", this)
			.add(new SubMode("Orbit"))
			.add(new SubMode("Blaze"))
			.add(new SubMode("Creeper"))
			.add(new SubMode("Enchanting"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public final ModeValue wingsType = new ModeValue("Wings Type", this)
			.add(new SubMode("Galaxy"))
			.add(new SubMode("Crystal"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public final ModeValue accesoriesType = new ModeValue("Accessories Type", this)
			.add(new SubMode("Bandana"))
			.add(new SubMode("None"))
			.setDefault("None");
	
	public Cosmetics() {
	    for (BlueArchive ba : BlueArchive.values()) {
	        haloType.add(ba);
	    }
	    
	    haloType.setDefault(BlueArchive.NONE);

	    for (CapeType cape : CapeType.values()) {
	        capeType.add(cape);
	    }
	    capeType.setDefault(CapeType.NONE);
	}
	
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        BlueArchive selectedHalo = haloType.getValue();
        
        if (selectedHalo != null) {
            selectedHalo.render(event);
        }
    };
}