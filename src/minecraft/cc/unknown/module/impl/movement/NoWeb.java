package cc.unknown.module.impl.movement;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.noweb.IgnoreNoWeb;
import cc.unknown.module.impl.movement.noweb.VulcanNoWeb;
import cc.unknown.value.impl.ModeValue;

@ModuleInfo(aliases = "No Web", description = "Evita que te frenes en las telara�as.", category = Category.MOVEMENT)
public class NoWeb extends Module {
    private final ModeValue mode = new ModeValue("Mode", this)
    		.add(new VulcanNoWeb("Vulcan", this))
    		.add(new IgnoreNoWeb("Ignore", this))
    		.setDefault("Ignore");
}