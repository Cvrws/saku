package cc.unknown.handlers;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.netty.TimedPacket;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.Tuple;

public class NetworkingHandler implements Accessor {
    public static ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
    static StopWatch enabledTimer = new StopWatch();
    public static boolean enabled;
    static long amount;
    static Tuple<Class[], Boolean> regular = new Tuple<>(new Class[]{C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, S1CPacketEntityMetadata.class}, false);
    static Tuple<Class[], Boolean> velocity = new Tuple<>(new Class[]{S12PacketEntityVelocity.class, S27PacketExplosion.class}, false);
    static Tuple<Class[], Boolean> teleports = new Tuple<>(new Class[]{S08PacketPlayerPosLook.class, S39PacketPlayerAbilities.class, S09PacketHeldItemChange.class}, false);
    static Tuple<Class[], Boolean> players = new Tuple<>(new Class[]{S13PacketDestroyEntities.class, S14PacketEntity.class, S14PacketEntity.S16PacketEntityLook.class, S14PacketEntity.S15PacketEntityRelMove.class, S14PacketEntity.S17PacketEntityLookMove.class, S18PacketEntityTeleport.class, S20PacketEntityProperties.class, S19PacketEntityHeadLook.class}, false);
    static Tuple<Class[], Boolean> blink = new Tuple<>(new Class[]{C02PacketUseEntity.class, C0DPacketCloseWindow.class, C0EPacketClickWindow.class, C0CPacketInput.class, C0BPacketEntityAction.class, C08PacketPlayerBlockPlacement.class, C07PacketPlayerDigging.class, C09PacketHeldItemChange.class, C13PacketPlayerAbilities.class, C15PacketClientSettings.class, C16PacketClientStatus.class, C17PacketCustomPayload.class, C18PacketSpectate.class, C19PacketResourcePackStatus.class, C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class, C0APacketAnimation.class}, false);
    static Tuple<Class[], Boolean> movement = new Tuple<>(new Class[]{C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class}, false);

    public static Tuple<Class[], Boolean>[] types = new Tuple[]{regular, velocity, teleports, players, blink, movement};
	
    @EventLink
	public final Listener<PacketSendEvent> onSend = event -> {
        event.setCancelled(onSend(event.getPacket(), event).isCancelled());

	};

	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		event.setCancelled(onReceive(event.getPacket(), event).isCancelled());
	};

	public PacketSendEvent onSend(Packet<?> packet, PacketSendEvent event) {
		if (!event.isCancelled() && enabled && Arrays.stream(types).anyMatch(tuple -> tuple.getSecond() && Arrays.stream(tuple.getFirst()).anyMatch(regularpacket -> regularpacket == packet.getClass()))) {
			event.setCancelled(true);
			packets.add(new TimedPacket(packet));
		}
		
		return event;
	}
	
    public PacketReceiveEvent onReceive(Packet<?> packet, PacketReceiveEvent event) {
        if (!event.isCancelled() && enabled && Arrays.stream(types).anyMatch(tuple -> tuple.getSecond() && Arrays.stream(tuple.getFirst()).anyMatch(regularpacket -> regularpacket == packet.getClass()))) {
            event.setCancelled(true);
            packets.add(new TimedPacket(packet));
        }

        return event;
    }

    public static void dispatch() {
        if (!packets.isEmpty()) {
            // Stops the packets from being called twice
            boolean enabled = NetworkingHandler.enabled;
            NetworkingHandler.enabled = false;
            packets.forEach(timedPacket -> PacketUtil.queue(timedPacket.getPacket()));
            NetworkingHandler.enabled = enabled;
            packets.clear();
        }
    }

    public static void disable() {
        enabled = false;
        enabledTimer.setMillis(enabledTimer.getElapsedTime() - 999999999);
    }

    @EventLink
    public final Listener<WorldChangeEvent> onWorld = event -> {
    	dispatch();
    };
    
    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        if (!(enabled = !enabledTimer.finished(100) && !(mc.currentScreen instanceof GuiDownloadTerrain))) {
            dispatch();
        } else {
            enabled = false;

            packets.forEach(packet -> {
                if (packet.getStopWatch().getMillis() + amount < System.currentTimeMillis()) {
                    PacketUtil.queue(packet.getPacket());
                    packets.remove(packet);
                }
            });

            enabled = true;
        }
    };

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players) {
        spoof(amount, regular, velocity, teleports, players, false);
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink, boolean movement) {
        enabledTimer.reset();

        NetworkingHandler.regular.setSecond(regular);
        NetworkingHandler.velocity.setSecond(velocity);
        NetworkingHandler.teleports.setSecond(teleports);
        NetworkingHandler.players.setSecond(players);
        NetworkingHandler.blink.setSecond(blink);
        NetworkingHandler.movement.setSecond(movement);
        NetworkingHandler.amount = amount;
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink) {
        spoof(amount, regular, velocity, teleports, players, blink, false);
    }

    public static void blink() {
        spoof(9999999, true, false, false, false, true);
    }
}