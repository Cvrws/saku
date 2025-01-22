package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleInfo(aliases = "Legit Expand", description = "Abusa del Look Vector", category = Category.PLAYER)
public class LegitExpand extends Module {

	public float yaw = 0.005491f;
	public float pitch = 49.5f;

	@EventLink
	public final Listener<PreMotionEvent> onPreUpdate = event -> {

	};
}
