package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Cosmetics", description = "Like lunar cosmetics [BETA]", category = Category.VISUALS)
public final class Cosmetics extends Module {
	
	public final BooleanValue topHat = new BooleanValue("Top Hat", this, false);
	public final BooleanValue doug = new BooleanValue("Doug Dimmadome", this, false);
	
	public final BooleanValue whiterPet = new BooleanValue("Whiter Pet", this, false);
	public final BooleanValue dogPet = new BooleanValue("Dog Pet", this, false);
	public final BooleanValue bandana = new BooleanValue("Bandana", this, false);
	public final BooleanValue galaxyWings = new BooleanValue("Galaxy Wings", this, false);
	public final BooleanValue witchHat = new BooleanValue("Witch Hat", this, false);
	
}