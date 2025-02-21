package cc.unknown.module.impl.world;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.NaturalPressEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

@ModuleInfo(aliases = "Fast Place", description = "Te permite colocar o usar items más rapido", category = Category.WORLD)
public class FastPlace extends Module {

	private final BooleanValue block = new BooleanValue("Block", this, true);
	private final NumberValue cpsBlock = new NumberValue("CPS [Block]", this, 19, 1, 40, 1, () -> !block.getValue());
	private final NumberValue dCpsBlock = new NumberValue("Delta CPS [Block]", this, 4, 1, 40, 1, () -> !block.getValue());
	private final NumberValue blockMultiplier = new NumberValue("Multiplier [Block]", this, 10, 1, 20, 1, () -> !block.getValue());
	
	private final BooleanValue snowBall = new BooleanValue("SnowBall", this, true);
	private final NumberValue cpsSnow = new NumberValue("CPS [SnowBall]", this, 19, 1, 40, 1, () -> !snowBall.getValue());
	private final NumberValue dCpsSnow = new NumberValue("Delta CPS [SnowBall]", this, 4, 1, 40, 1, () -> !snowBall.getValue());
	private final NumberValue snowMultiplier = new NumberValue("Multiplier [SnowBall]", this, 10, 1, 20, 1, () -> !snowBall.getValue());

	private final BooleanValue egg = new BooleanValue("Egg", this, true);
	private final NumberValue cpsEgg = new NumberValue("CPS [Egg]", this, 19, 1, 40, 1, () -> !egg.getValue());
	private final NumberValue dCpsEgg = new NumberValue("Delta CPS [Egg]", this, 4, 1, 40, 1, () -> !egg.getValue());
	private final NumberValue eggMultiplier = new NumberValue("Multiplier [Egg]", this, 10, 1, 20, 1, () -> !egg.getValue());

	private final BooleanValue xpBottle = new BooleanValue("Bottle XP", this, true);
	private final NumberValue cpsBottle = new NumberValue("CPS [BottleXP]", this, 19, 1, 40, 1, () -> !xpBottle.getValue());
	private final NumberValue dCpsBottle = new NumberValue("Delta CPS [BottleXP]", this, 4, 1, 40, 1, () -> !xpBottle.getValue());
	private final NumberValue xpMultiplier = new NumberValue("Multiplier [BottleXP]", this, 10, 1, 20, 1, () -> !xpBottle.getValue());

	private final BooleanValue autoSwap = new BooleanValue("Auto Swap", this, true);
	private final NumberValue pitchRange = new NumberValue("Pitch Range", this, 75, 0, 90, 1, () -> !autoSwap.getValue());

	private StopWatch stopWatch = new StopWatch();
    private boolean attack = false;
	private int lastSlot;
	private boolean place;
	
	@Override
	public void onEnable() {
		lastSlot = -1;		
	}
	
	@Override
	public void onDisable() {
		mc.player.inventory.currentItem = lastSlot;
	}

	@EventLink
	public final Listener<NaturalPressEvent> onNatural = event -> {
	    if (mc.currentScreen == null && Mouse.isButtonDown(1)) {
	        ItemStack stack = mc.player.getCurrentEquippedItem();
	        if (stack != null) {
	            Item item = stack.getItem();
	            if (item == null || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == null) return;
	            
	            long delay = 0;
	            boolean shouldCancel = false;
	            
	            if (block.getValue() && item instanceof ItemBlock) {
	                delay = getDeltaClick(cpsBlock, dCpsBlock, blockMultiplier.getValueToInt());
	                shouldCancel = true;
	                if (place) {
	                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	                }
	            } else if (snowBall.getValue() && item instanceof ItemSnowball) {
	                delay = getDeltaClick(cpsSnow, dCpsSnow, snowMultiplier.getValueToInt());
	                shouldCancel = true;
	            } else if (egg.getValue() && item instanceof ItemEgg) {
	                delay = getDeltaClick(cpsEgg, dCpsEgg, eggMultiplier.getValueToInt());
	                shouldCancel = true;
	            } else if (xpBottle.getValue() && item instanceof ItemExpBottle) {
	                delay = getDeltaClick(cpsBottle, dCpsBottle, xpMultiplier.getValueToInt());
	                shouldCancel = true;
	            }
	            
	            if (delay > 0 && stopWatch.finished(delay)) {
	                mc.rightClickMouse();
	                stopWatch.reset();
	            }
	            
	            if (shouldCancel) {
	                event.setCancelled();
	            }
	        }
	    }
	};
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (!isInGame() || attack) return;
        
        if (lastSlot == -1) {
        	lastSlot = mc.player.inventory.currentItem;
        }
	
        final int slot = InventoryUtil.findBlock();
        if (slot == -1) return;
	
        if (mc.player.rotationPitch < pitchRange.getValueToFloat()) {
        	return;
        }
	
        if (autoSwap.getValue() && shouldSkipBlockCheck()) {
        	mc.player.inventory.currentItem = slot;
        }
	
        if (mc.currentScreen == null || mc.player.getHeldItem() == null) return;
    };

    @EventLink
    public final Listener<PacketSendEvent> onSend = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
            if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
            	attack = true;
            } else {
            	attack = false;
            }
        }
    };
	
	private boolean shouldSkipBlockCheck() {
		ItemStack heldItem = mc.player.getHeldItem();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}
    
    private long getDeltaClick(NumberValue cps, NumberValue dCps, int multiplier) {
        double baseCPS = cps.getValueToLong();
        double delta = dCps.getValueToLong();
        double randomizedCPS = baseCPS + delta + Math.random() * multiplier;
        return (long) (1000 / randomizedCPS);
    }
}