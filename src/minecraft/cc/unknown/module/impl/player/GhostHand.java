package cc.unknown.module.impl.player;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

@ModuleInfo(aliases = "Ghost Hand", description = "Te permite abrir cofres detras de las paredes.", category = Category.PLAYER)
public class GhostHand extends Module {
	public Block block = Blocks.chest;
}
