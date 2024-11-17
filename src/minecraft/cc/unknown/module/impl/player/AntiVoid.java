package cc.unknown.module.impl.player;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.antivoid.*;
import cc.unknown.value.impl.ModeValue;

@ModuleInfo(aliases = "Anti Void", description = "Prevents you from falling into the void", category = Category.PLAYER)
public class AntiVoid extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new PositionAntiVoid("Position", this))
            .add(new VulcanAntiVoid("Vulcan", this))
            .add(new CollisionAntiVoid("Collision", this))
            .add(new PolarAntiVoid("Polar", this))
            .setDefault("Packet");
}