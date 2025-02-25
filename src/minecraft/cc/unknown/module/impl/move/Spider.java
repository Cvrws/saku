package cc.unknown.module.impl.move;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.spider.*;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = { "Spider", "wallclimb" }, description = "Permite trepar por las paredes como una araña.", category = Category.MOVEMENT)
public class Spider extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new VulcanSpider("Vulcan", this))
			.add(new PolarSpider("Polar", this))
			.setDefault("Vulcan");

	public final BooleanValue fast = new BooleanValue("Fast", this, true, () -> !mode.is("Polar"));
	public final DescValue help = new DescValue("0 -> Left | 1 -> Right | 2 -> Middle", this, () -> !mode.is("Polar"));
	public final NumberValue mouseButton = new NumberValue("Mouse button to go up faster", this, 1, 0, 5, 1, () -> !mode.is("Polar"));

}