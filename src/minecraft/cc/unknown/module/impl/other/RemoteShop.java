package cc.unknown.module.impl.other;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jetbrains.annotations.Nullable;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;

@ModuleInfo(aliases = "Remote Shop", description = "Only works in bedwars", category = Category.OTHER)
public final class RemoteShop extends Module {

    private final Queue<C0EPacketClickWindow> delayedPacket = new ConcurrentLinkedQueue<>();
    private boolean lastStop = false;
    protected @Nullable GuiChest cacheShop = null;
    
    @Override
    public void onDisable() {
        lastStop = false;
        delayedPacket.clear();
    }

    @EventLink
    public final Listener<PacketSendEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (mc.currentScreen == cacheShop && cacheShop != null) {
            if (event.getPacket() instanceof C0EPacketClickWindow) {
            	C0EPacketClickWindow wrapper = (C0EPacketClickWindow) packet;
                if (!lastStop) {
                    MoveUtil.stop();
                    lastStop = true;
                    delayedPacket.add(wrapper);
                    event.setCancelled();
                } else if (!delayedPacket.isEmpty()) {
                    delayedPacket.add(wrapper);
                    event.setCancelled();
                }
            } else if (event.getPacket() instanceof C0DPacketCloseWindow) {
                event.setCancelled();
            }
        }
    };
    
    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mc.currentScreen == cacheShop && cacheShop != null) {
            synchronized (delayedPacket) {
                for (C0EPacketClickWindow p : delayedPacket) {
                    PacketUtil.sendNoEvent(p);
                }
                delayedPacket.clear();
            }
        } else {
            delayedPacket.clear();
        }
    };
    
    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        if (lastStop) {
            MoveUtil.stop();
            lastStop = false;
        }
    };
    
    public void openContainer() {
        if (mc.currentScreen instanceof GuiChest) {
            cacheShop = (GuiChest) mc.currentScreen;
        } else {
            cacheShop = null;
        }
    }

    public void remoteShop() {
        if (cacheShop == null) {
            return;
        }

        mc.displayGuiScreen(cacheShop);
    }

    public void forceClose() {
        cacheShop = null;
    }
}
