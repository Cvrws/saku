package cc.unknown.module.impl.world;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

@ModuleInfo(aliases = "Fast Place", description = "Te permite colocar o usar items más rapido", category = Category.WORLD)
public class FastPlace extends Module {

	private final BooleanValue block = new BooleanValue("Block", this, true);
	private final BoundsNumberValue cpsBlock = new BoundsNumberValue("CPS [Block]", this, 18, 25, 1, 40, 1, () -> !block.getValue());
	private final BoundsNumberValue dCpsBlock = new BoundsNumberValue("DCPS [Block]", this, 2, 4, 1, 40, 1, () -> !block.getValue());
	
	private final BooleanValue snowBall = new BooleanValue("SnowBall", this, false);
	private final BoundsNumberValue cpsSnow = new BoundsNumberValue("CPS [SnowBall]", this, 18, 25, 1, 40, 1, () -> !snowBall.getValue());
	private final BoundsNumberValue dCpsSnow = new BoundsNumberValue("DCPS [SnowBall]", this, 2, 4, 1, 40, 1, () -> !snowBall.getValue());
	
	private final BooleanValue egg = new BooleanValue("Egg", this, false);
	private final BoundsNumberValue cpsEgg = new BoundsNumberValue("CPS [Egg]", this, 18, 25, 1, 40, 1, () -> !egg.getValue());
	private final BoundsNumberValue dCpsEgg = new BoundsNumberValue("DCPS [Egg]", this, 2, 4, 1, 40, 1, () -> !egg.getValue());
	
	private final BooleanValue xpBottle = new BooleanValue("Bottle XP", this, true);
	private final BoundsNumberValue cpsBottle = new BoundsNumberValue("CPS [BottleXP]", this, 18, 25, 1, 40, 1, () -> !xpBottle.getValue());
	private final BoundsNumberValue dCpsBottle = new BoundsNumberValue("DCPS [BottleXP]", this, 2, 4, 1, 40, 1, () -> !xpBottle.getValue());
		
	private StopWatch stopWatch = new StopWatch();
	
	private boolean place;

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
        if (mc.currentScreen == null && Mouse.isButtonDown(1)) {
            ItemStack stack = mc.player.getCurrentEquippedItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item == null || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == null) return;
                
                long delay = 0;
                
                if (block.getValue() && item instanceof ItemBlock) {
                	delay = getDeltaClick(cpsBlock, dCpsBlock);
                	if (place) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    }
                    
                } else if (snowBall.getValue() && item instanceof ItemSnowball) {
                    delay = getDeltaClick(cpsSnow, dCpsSnow);
                } else if (egg.getValue() && item instanceof ItemEgg) {
                    delay = getDeltaClick(cpsEgg, dCpsEgg);
                } else if (xpBottle.getValue() && item instanceof ItemExpBottle) {
                    delay = getDeltaClick(cpsBottle, dCpsBottle);
                }
                
                if (delay > 0 && stopWatch.finished(delay)) {
                    mc.rightClickMouse();
                    place = true;
                    stopWatch.reset();
                }
            }
        }       
    };
    
    private long getDeltaClick(BoundsNumberValue cps, BoundsNumberValue dCps) {
        double baseCPS = cps.getRandomBetween().longValue();
        double delta = dCps.getRandomBetween().longValue();
        double randomizedCPS = baseCPS + delta + Math.random() * 10;
        return (long) (1000 / randomizedCPS);
    }
}