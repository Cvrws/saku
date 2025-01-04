package cc.unknown.module.impl.movement;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.SlowDownEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(aliases = "No Slow", description = "Remueve la lentitud al utilizar algunos objetos.", category = Category.MOVEMENT)
public class NoSlow extends Module {
	private final BooleanValue sword = new BooleanValue("Sword", this, false);
	private final BooleanValue swordSlowdown = shortBoolean("Slowdown", sword);
	private final NumberValue swordForward = shortNumber("Sword Forward", sword, swordSlowdown);
	private final NumberValue swordStrafe = shortNumber("Sword Strafe", sword, swordSlowdown);
	private final BooleanValue swordSprint = shortBoolean("Sprint", sword);
	private final BooleanValue swordSwitchNormal = shortBoolean("Normal Switch", sword);
	private final BooleanValue swordPreSwitch = shortBoolean("Pre Switch", sword);
	private final BooleanValue swordC08Pre = shortBoolean("C08 Pre", sword);
	private final BooleanValue swordC0CPre = shortBoolean("C0C Pre", sword);
	private final BooleanValue swordC08Post = shortBoolean("C08 Post", sword);
	private final BooleanValue swordC07NRPre = shortBoolean("C07 Pre Normal Release", sword);
	private final BooleanValue swordC07NRPost = shortBoolean("C07 Post Normal Release", sword);
	private final BooleanValue swordC07BRPre = shortBoolean("C07 Pre Block Release", sword);
	private final BooleanValue swordC07BRPost = shortBoolean("C07 Post Block Release", sword);
	private final BooleanValue swordC07NDPre = shortBoolean("C07 Pre Normal Drop", sword);
	private final BooleanValue swordC07NDPost = shortBoolean("C07 Post Normal Drop", sword);
	private final BooleanValue swordC07BDPre = shortBoolean("C07 Pre Block Drop", sword);
	private final BooleanValue swordC07BDPost = shortBoolean("C07 Post Block Drop", sword);
	private final BooleanValue swordBug = shortBoolean("Bug", sword);
	
	private final BooleanValue swordTimer = shortBoolean("Timer", sword);
	private final NumberValue timerSword = shortNumber("Timer Sword", sword, swordTimer);

	private final BooleanValue bow = new BooleanValue("Bow", this, false);
	private final BooleanValue bowSlowdown = shortBoolean("Slowdown", bow);
	private final NumberValue bowForward = shortNumber("Bow Forward", bow, bowSlowdown);
	private final NumberValue bowStrafe = shortNumber("Bow Strafe", bow, bowSlowdown);
	private final BooleanValue bowSprint = shortBoolean("Sprint", bow);
	private final BooleanValue bowSwitchNormal = shortBoolean("Normal Switch", bow);
	private final BooleanValue bowPostSwitch = shortBoolean("Post Switch", bow);
	private final BooleanValue bowPreSwitch = shortBoolean("Pre Switch", bow);
	private final BooleanValue bowC08Pre = shortBoolean("C08 Pre", bow);
	private final BooleanValue bowC0CPre = shortBoolean("C0C Pre", bow);
	private final BooleanValue bowC08Post = shortBoolean("C08 Post", bow);
	private final BooleanValue bowC07NRPre = shortBoolean("C07 Pre Normal Release", bow);
	private final BooleanValue bowC07NRPost = shortBoolean("C07 Post Normal Release", bow);
	private final BooleanValue bowC07BRPre = shortBoolean("C07 Pre Block Release", bow);
	private final BooleanValue bowC07BRPost = shortBoolean("C07 Post Block Release", bow);
	private final BooleanValue bowC07NDPre = shortBoolean("C07 Pre Drop Release", bow);
	private final BooleanValue bowC07NDPost = shortBoolean("C07 Post Normal Drop Release", bow);
	private final BooleanValue bowC07BDPre = shortBoolean("C07 Pre Block Drop Release", bow);
	private final BooleanValue bowC07BDPost = shortBoolean("C07 Post Block Drop Release", bow);
	
	private final BooleanValue bowTimer = shortBoolean("Timer", bow);
	private final NumberValue timerBow = shortNumber("Timer Bow", bow, bowTimer);

	private final BooleanValue rest = new BooleanValue("Rest", this, false);
	private final BooleanValue restSlowdown = shortBoolean("Slowdown", rest);
	private final NumberValue restForward = shortNumber("Rest Forward", rest, restSlowdown);
	private final NumberValue restStrafe = shortNumber("Rest Strafe", rest, restSlowdown);
	private final BooleanValue restSprint = shortBoolean("Sprint", rest);
	private final BooleanValue restSwitchNormal = shortBoolean("Normal Switch", rest);
	private final BooleanValue restPostSwitch = shortBoolean("Post Switch", rest);
	private final BooleanValue restPreSwitch = shortBoolean("Pre Switch", rest);
	private final BooleanValue restC08Pre = shortBoolean("C08 Pre", rest);
	private final BooleanValue restC0CPre = shortBoolean("C0C Pre", rest);
	private final BooleanValue restC08Post = shortBoolean("C08 Post", rest);
	private final BooleanValue restC07NRPre = shortBoolean("C07 Pre Normal Release", rest);
	private final BooleanValue restC07NRPost = shortBoolean("C07 Post Normal Release", rest);
	private final BooleanValue restC07BRPre = shortBoolean("C07 Pre Block Release", rest);
	private final BooleanValue restC07BRPost = shortBoolean("C07 Post Block Release", rest);
	private final BooleanValue restC07NDPre = shortBoolean("C07 Pre Drop Release", rest);
	private final BooleanValue restC07NDPost = shortBoolean("C07 Post Normal Drop", rest);
	private final BooleanValue restC07BDPre = shortBoolean("C07 Pre Block Drop", rest);
	private final BooleanValue restC07BDPost = shortBoolean("C07 Post Block Drop", rest);
	
	private final BooleanValue restTimer = shortBoolean("Timer", rest);
	private final NumberValue timerRest = shortNumber("Timer Rest", rest, restTimer);
		
	private final StopWatch timeHelper = new StopWatch();

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0F;
	}

	@Override
	public void onEnable() {
		mc.timer.timerSpeed = 1.0F;
	}

	@EventLink
	public final Listener<SlowDownEvent> onSlowDown = event -> {
		if (!isInGame()) return;
	    ItemStack currentItem = PlayerUtil.getItemStack();
		
	    if (currentItem == null) return;
		
		if (currentItem != null && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
			if (currentItem.getItem() instanceof ItemSword) {
				event.setSprint(swordSprint.getValue());
				if (swordSlowdown.getValue()) {
					event.setForwardMultiplier(swordForward.getValue().floatValue());
					event.setStrafeMultiplier(swordStrafe.getValue().floatValue());
				}
			} else if (currentItem.getItem() instanceof ItemBow) {
				event.setSprint(bowSprint.getValue());
				if (bowSlowdown.getValue()) {
					event.setForwardMultiplier(bowForward.getValue().floatValue());
					event.setStrafeMultiplier(bowStrafe.getValue().floatValue());
				}
			} else {
				event.setSprint(restSprint.getValue());
				if (restSlowdown.getValue()) {
					event.setForwardMultiplier(restForward.getValue().floatValue());
					event.setStrafeMultiplier(restStrafe.getValue().floatValue());
				}
			}
		} else {
			timeHelper.reset();
		}
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
	    if (!isInGame()) return;
	    ItemStack currentItem = PlayerUtil.getItemStack();
	    if (currentItem == null) return;
	    
    	if (currentItem.getItem() instanceof ItemSword) {
    		if (swordC08Post.getValue()) sendC08();
    		if (swordC07NRPost.getValue()) sendC07NormalRelease();
    		if (swordC07BRPost.getValue()) sendC07BlockRelease();
    		if (swordC07NDPost.getValue()) sendC07DropNormal();
    		if (swordC07BDPost.getValue()) sendC07BlockDrop();
    	} else if (currentItem.getItem() instanceof ItemBow) {
    		if (bowC08Post.getValue()) sendC08();
    		if (bowC07NRPost.getValue()) sendC07NormalRelease();
    		if (bowC07BRPost.getValue()) sendC07BlockRelease();
    		if (bowC07NDPost.getValue()) sendC07DropNormal();
    		if (bowC07BDPost.getValue()) sendC07BlockDrop();
    		if (bowPostSwitch.getValue()) switchItem();
    	} else if (currentItem.getItem() instanceof ItemFood || currentItem.getItem() instanceof ItemPotion || currentItem.getItem() instanceof ItemBucketMilk) {
    		if (restC08Post.getValue()) sendC08();
    		if (restC07NRPost.getValue()) sendC07NormalRelease();
    		if (restC07BRPost.getValue()) sendC07BlockRelease();
    		if (restC07NDPost.getValue()) sendC07DropNormal();
    		if (restC07BDPost.getValue()) sendC07BlockDrop();
    		if (restPostSwitch.getValue()) switchItem();
    	}
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (!isInGame()) return;
	    ItemStack currentItem = PlayerUtil.getItemStack();
	    if (currentItem == null) return;
	    
        if (mc.player.getItemInUseDuration() == 1) {
        	if (currentItem.getItem() instanceof ItemSword) {
        		if (swordC08Pre.getValue()) sendC08();
        		if (swordC0CPre.getValue()) sendC0C();
        		if (swordC07NRPre.getValue()) sendC07NormalRelease();
        		if (swordC07BRPre.getValue()) sendC07BlockRelease();
        		if (swordC07NDPre.getValue()) sendC07DropNormal();
        		if (swordC07BDPre.getValue()) sendC07BlockDrop();
        		if (swordPreSwitch.getValue()) switchItem();
        		if (swordSwitchNormal.getValue()) switchItem2();
        		setTimerSpeed(swordTimer, timerSword);
        	} else if (currentItem.getItem() instanceof ItemBow) {
        		if (bowC08Pre.getValue()) sendC08();
        		if (bowC0CPre.getValue()) sendC0C();
        		if (bowC07NRPre.getValue()) sendC07NormalRelease();
        		if (bowC07BRPre.getValue()) sendC07BlockRelease();
        		if (bowC07NDPre.getValue()) sendC07DropNormal();
        		if (bowC07BDPre.getValue()) sendC07BlockDrop();
        		if (bowPreSwitch.getValue()) switchItem();
        		if (bowSwitchNormal.getValue()) switchItem2();
        		setTimerSpeed(bowTimer, timerBow);	    
        	} else if (currentItem.getItem() instanceof ItemFood || currentItem.getItem() instanceof ItemPotion || currentItem.getItem() instanceof ItemBucketMilk) {
        		if (restC08Pre.getValue()) sendC08();
        		if (restC0CPre.getValue()) sendC0C();
        		if (restC07NRPre.getValue()) sendC07NormalRelease();
        		if (restC07BRPre.getValue()) sendC07BlockRelease();
        		if (restC07NDPre.getValue()) sendC07DropNormal();
        		if (restC07BDPre.getValue()) sendC07BlockDrop();
        		if (restPreSwitch.getValue()) switchItem();
        		if (restSwitchNormal.getValue()) switchItem2();
        		setTimerSpeed(restTimer, timerRest);
		    }  	
        }
	};
	
	private void switchItem() {
		PacketUtil.send(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 3));
		PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
	}
	
	private void switchItem2() {
        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 7 + 2));
        PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
	}

	private void handleKeyPresses() {
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) mc.gameSettings.keyBindForward.pressed = true;
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) mc.gameSettings.keyBindBack.pressed = true;
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) mc.gameSettings.keyBindLeft.pressed = true;
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) mc.gameSettings.keyBindRight.pressed = true;
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) mc.gameSettings.keyBindSprint.pressed = true;
	}

	private boolean isRestItem(ItemStack item) {
	    return item.getItem() instanceof ItemFood || 
	           item.getItem() instanceof ItemPotion || 
	           item.getItem() instanceof ItemBucketMilk;
	}

	private void setTimerSpeed(BooleanValue timerEnabled, NumberValue timerValue) {
	    mc.timer.timerSpeed = timerEnabled.getValue() ? (float) timerValue.getValue() : 1.0F;
	}


	public void sendC08() {
		PacketUtil.send(new C08PacketPlayerBlockPlacement(PlayerUtil.getItemStack()));
	}

	public void sendC07NormalRelease() {
		PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
				new BlockPos(0, 0, 0), EnumFacing.DOWN));
	}

	public void sendC07BlockRelease() {
		PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
				new BlockPos(-1, -1, -1), EnumFacing.UP));
	}

	public void sendC07DropNormal() {
		PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM,
				new BlockPos(0, 0, 0), EnumFacing.DOWN));
	}

	public void sendC07BlockDrop() {
		PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM,
				new BlockPos(-1, -1, -1), EnumFacing.UP));
	}

	public void sendC0C() {
		PacketUtil.send(new C0CPacketInput(0.0F, 0.82F, false, false));
	}

	private BooleanValue shortBoolean(String name, BooleanValue parentValue) {
	    return new BooleanValue(name, this, false, () -> !parentValue.getValue());
	}

	private NumberValue shortNumber(String name, BooleanValue parentValue, BooleanValue parentSecondValue) {
	    return new NumberValue(name, this, 1, 0.2, 1, 0.1, () -> !parentValue.getValue() || !((BooleanValue) parentSecondValue).getValue());
	}
}