package cc.unknown.module.impl.combat.velocity;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.util.client.MathUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class JumpVelocity extends Mode<Velocity> {
    
    private boolean hasReceivedVelocity = false;
    private int limitUntilJump = 0;
    
    public JumpVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        Packet<?> packet = event.getPacket();
        
        if (event.isCancelled()) return;

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
            
            if (mc.player.getEntityId() == wrapper.getEntityID() &&
                wrapper.motionY > 0 && 
                (wrapper.motionX != 0 || wrapper.motionZ != 0) && 
                ((mc.player.motionX + wrapper.motionX) != 0.0 || (mc.player.motionZ + wrapper.motionZ) != 0.0)) {

                double motionX = wrapper.motionX;
                double motionZ = wrapper.motionZ;

                double packetDirection = Math.atan2(motionX, motionZ);
                double degreePlayer = getDirection();
                double degreePacket = Math.floorMod((int) Math.toDegrees(packetDirection), 360);

                double angle = Math.abs(degreePacket + degreePlayer);
                double threshold = 120.0;
                angle = Math.floorMod((int) angle, 360);
                
                boolean inRange = angle >= (180 - threshold / 2) && angle <= (180 + threshold / 2);
                if (inRange) {
                    hasReceivedVelocity = true;
                }
            }
        }
    };

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (mc.player == null) return;
        if (!MathUtil.isChance(getParent().chance, getParent().notWhileSpeed, getParent().notWhileJumpBoost)) return;

        if (hasReceivedVelocity) {
            if (!mc.player.isJumping && shouldJump() && mc.player.isSprinting() && mc.player.onGround && mc.player.hurtTime == 9) {

                mc.player.jump();
                limitUntilJump = 0;
            }
            hasReceivedVelocity = false;
            return;
        }

        switch (getParent().jumpType.getValue().getName().toLowerCase()) {
            case "ticks":
                limitUntilJump++;
                break;
            case "receivedhits":
                if (mc.player.hurtTime == 9) limitUntilJump++;
                break;
        }
    };

    private boolean shouldJump() {
        switch (getParent().jumpType.getValue().getName().toLowerCase()) {
            case "ticks":
                return limitUntilJump >= getParent().ticksUntilJump.getValueToInt();
            case "receivedhits":
                return limitUntilJump >= getParent().hitsUntilJump.getValueToInt();
            default:
                return false;
        }
    }
    
    private double getDirection() {
        float moveYaw = mc.player.rotationYaw;

        if (mc.player.moveForward != 0 && mc.player.moveStrafing == 0) {
            moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
        } else if (mc.player.moveForward != 0 && mc.player.moveStrafing != 0) {
            if (mc.player.moveForward > 0) {
                moveYaw += (mc.player.moveStrafing > 0) ? -45 : 45;
            } else {
                moveYaw -= (mc.player.moveStrafing > 0) ? -45 : 45;
            }
            moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
        } else if (mc.player.moveStrafing != 0 && mc.player.moveForward == 0) {
            moveYaw += (mc.player.moveStrafing > 0) ? -90 : 90;
        }

        return Math.floorMod((int) moveYaw, 360);
    }

}
