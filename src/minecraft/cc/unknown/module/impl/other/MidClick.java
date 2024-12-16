package cc.unknown.module.impl.other;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.component.impl.player.FriendComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MouseEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = "Mid Click", description = ">:3c", category = Category.OTHER)
public final class MidClick extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Throw pearl"))
			.add(new SubMode("Add/Remove friend"))
			.setDefault("Throw pearl");

	private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean x = new AtomicBoolean(false);
    private AtomicInteger prevSlot = new AtomicInteger(0);
    private Robot bot;
    private AtomicInteger pearlEvent = new AtomicInteger(4);

	@Override
	public void onEnable() {
		try {
			this.bot = new Robot();
		} catch (AWTException x) {
			this.toggle();
		}
	}

	@EventLink
	public final Listener<MouseEvent> onMouse = event -> {
		if (mc.currentScreen != null)
			return;

		if (pearlEvent.get() < 4) {
			if (pearlEvent.get() == 3)
				mc.player.inventory.currentItem = prevSlot.get();
			pearlEvent.incrementAndGet();
		}

		if (!x.get() && event.getCode() == 2) {
			if (mode.is("Add/Remove friend") && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
				EntityPlayer playerHit = (EntityPlayer) mc.objectMouseOver.entityHit;
				if (!FriendComponent.isFriend(playerHit)) {
					FriendComponent.addFriend(playerHit);
					PlayerUtil.send(ChatFormatting.GRAY + playerHit.getName() + " was added to your friends.");
				} else {
					FriendComponent.removeFriend(playerHit);
					PlayerUtil.send(ChatFormatting.GRAY + playerHit.getName() + " was removed from your friends.");
				}
			}

			if (mode.is("Throw pearl")) {
				for (int s = 0; s <= 8; s++) {
					ItemStack item = mc.player.inventory.getStackInSlot(s);
					if (item != null && item.getItem() instanceof ItemEnderPearl) {
						prevSlot.set(mc.player.inventory.currentItem);
						mc.player.inventory.currentItem = s;
						executorService.execute(() -> {
							bot.mousePress(InputEvent.BUTTON3_MASK);
							bot.mouseRelease(InputEvent.BUTTON3_MASK);
						});
						pearlEvent.set(0);
						x.set(true);
						return;
					}
				}
			}
		}
		x.set(event.getCode() == 2);
	};
}
