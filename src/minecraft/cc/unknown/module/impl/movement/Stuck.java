package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(aliases = {"Stuck", "Freeze"}, description = "Steve I'm stuck", category = Category.MOVEMENT)
public class Stuck extends Module {
    private double x;
    private double y;
    private double z;
    private boolean onGround = false;
    private Vector2f rotation;

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        
        this.onGround = mc.player.onGround;
        this.x = mc.player.posX;
        this.y = mc.player.posY;
        this.z = mc.player.posZ;
        this.rotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        final Vector2f rotation = this.rotation;
        rotation.x -= this.rotation.x % gcd;
        final Vector2f rotation2 = this.rotation;
        rotation2.y -= this.rotation.y % gcd;
    }

    @EventLink
    public final Listener<TeleportEvent> onReceive = event -> {
    	toggle();
    };
    
    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
    	toggle();
    };
    
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        mc.player.motionX = 0.0;
        mc.player.motionY = 0.0;
        mc.player.motionZ = 0.0;
        mc.player.setPosition(this.x, this.y, this.z);
    };

    @EventLink
    public final Listener<PacketSendEvent> onSend = event -> {
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            final Vector2f current = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
            final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            current.x -= current.x % gcd;
            current.y -= current.y % gcd;
            if (this.rotation.equals(current)) {
                return;
            }
            this.rotation = current;
            event.setCancelled(true);
            PacketUtil.sendNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, this.onGround));
            PacketUtil.sendNoEvent(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
        }
        
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    };
}