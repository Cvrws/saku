package cc.unknown.module.impl.combat;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.velocity.HypixelVelocity;
import cc.unknown.module.impl.combat.velocity.JumpVelocity;
import cc.unknown.module.impl.combat.velocity.LegitVelocity;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.val;

@ModuleInfo(aliases = "Velocity", description = "Modifica tu kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	private ModeValue mode = new ModeValue("Mode", this)
			.add(new HypixelVelocity("Hypixel", this))
			.add(new LegitVelocity("Legit Motion", this))
			.add(new JumpVelocity("Jump", this))
			.setDefault("Hypixel");
		
	public final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel"));
	public final NumberValue vertical = new NumberValue("Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel"));

	public final BooleanValue delay = new BooleanValue("Delay", this, false, () -> !mode.is("Hypixel"));
	public final NumberValue delayHorizontal = new NumberValue("Delayed Horizontal", this, 100, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	public final NumberValue delayVertical = new NumberValue("Delayed Vertical", this, 90, 0, 100, 1, () -> !mode.is("Hypixel") || !delay.getValue());
	
	public final BooleanValue onlyAir = new BooleanValue("Only in Air", this, false, () -> !mode.is("Hypixel"));
	public final BooleanValue onExplode = new BooleanValue("Explosion Ignore", this, false, () -> !mode.is("Hypixel"));
	
	public ModeValue jumpType = new ModeValue("Jump Type", this)
			.add(new SubMode("Tick"))
			.add(new SubMode("Hit"))
			.setDefault("Tick");
	
	public final NumberValue ticksUntilJump = new NumberValue("Ticks", this, 4, 0, 20, 1, () -> !mode.is("Jump") || !jumpType.is("Tick"));
	public final NumberValue hitsUntilJump = new NumberValue("Hits", this, 2, 1, 20, 1, () -> !mode.is("Jump") || !jumpType.is("Hit"));
	
	public final BooleanValue notWhileSpeed = new BooleanValue("Not while with potion speed", this, true);
	public final BooleanValue notWhileJumpBoost = new BooleanValue("Not while with potion jump ", this, true);
	public final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);


}