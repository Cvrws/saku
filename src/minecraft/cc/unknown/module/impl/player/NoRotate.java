package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "No Rotate", description = "Evita que los servidores modifiquen tu rotaci�n.", category = Category.PLAYER)
public class NoRotate extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Cancel"))
            .add(new SubMode("Edit"))
            .setDefault("Edit");
    
    private float yaw, pitch;
    private boolean teleport;
    
    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
    	if (mode.is("Cancel")) {
	        event.setYaw(mc.player.rotationYaw);
	        event.setPitch(mc.player.rotationPitch);
    	}
    	
    	if (mode.is("Edit")) {
            this.yaw = event.getYaw();
            this.pitch = event.getPitch();

            event.setYaw(mc.player.rotationYaw);
            event.setPitch(mc.player.rotationPitch);

            this.teleport = true;
    	}
    };
    
    @EventLink
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        final Packet packet = event.getPacket();

        if (mode.is("Edit")) {
        	if (this.teleport && packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
        		final C03PacketPlayer.C06PacketPlayerPosLook wrapped = ((C03PacketPlayer.C06PacketPlayerPosLook) packet);
        		wrapped.yaw = this.yaw;
        		wrapped.pitch = this.pitch;
        		event.setPacket(wrapped);
        		this.teleport = false;
	        }
        }
    };
}