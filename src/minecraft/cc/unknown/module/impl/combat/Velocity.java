package cc.unknown.module.impl.combat;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.velocity.*;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@ModuleInfo(aliases = "Velocity", description = "Modifica tu kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	public ModeValue mode = new ModeValue("Mode", this)
			.add(new HypixelVelocity("Hypixel", this))
			.add(new LegitVelocity("Legit Prediction", this))
			.setDefault("Hypixel");
		
	public final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel"));
	public final NumberValue vertical = new NumberValue("Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel"));

	public final BooleanValue delay = new BooleanValue("Delay", this, false, () -> !mode.is("Hypixel"));
	public final NumberValue delayHorizontal = new NumberValue("Delayed Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	public final NumberValue delayVertical = new NumberValue("Delayed Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	
	public final BooleanValue onlyAir = new BooleanValue("Only in Air", this, false, () -> !mode.is("Hypixel"));
	public final BooleanValue onExplode = new BooleanValue("Explosion Ignore", this, false, () -> !mode.is("Hypixel"));
	
	public final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
	public final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump ", this, true);
	public final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);

	private int counter;
	private boolean s12 = false;
	
	private final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.fromBounds(0, 0, 0, 1, 0, 1);
    private final Set<BlockPos> needToBoundingPos = new HashSet<>(2);
	
    @Override
    public void onEnable() {
        needToBoundingPos.clear();
    }
    
	@Override
	public void onDisable() {
		counter = 0;
	}
}