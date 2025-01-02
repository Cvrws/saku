package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Anti Blind", description = "Remueve efectos que afectan en la visión del jugador", category = Category.VISUALS)
public final class AntiBlind extends Module {
    
    public final BooleanValue removeConfusion = new BooleanValue("Remove Confusion/Blindness", this, true);
    public final NumberValue fire = new NumberValue("Fire Alpha", this, 0.0, 0.0, 1, 0.1);

}