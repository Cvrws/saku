package cc.unknown.module.impl.move.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Flight;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class LatestNCPFlight extends Mode<Flight> {

    private double moveSpeed;
    private boolean started, notUnder, clipped, teleport;

    public LatestNCPFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (teleport) {
            event.setCancelled();
            teleport = false;
            ChatUtil.display("Teleported");
        }
    };

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (!getParent().ncpMode.is("Clip")) return;

        final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, 1, 0);

        if (mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty() || started) {
            switch (mc.player.offGroundTicks) {
                case 0:
                    if (notUnder) {
                        if (clipped) {
                            started = true;
                            event.setSpeed(10);
                            mc.player.motionY = 0.42f;
                            notUnder = false;
                        }
                    }
                    break;

                case 1:
                    if (started) event.setSpeed(9.6);
                    break;

                default:
//                    if (mc.thePlayer.fallDistance > 0 && started) {
//                        mc.thePlayer.motionY += 2.5 / 100f;
//                    }
                    break;
            }
        } else {
            notUnder = true;

            if (clipped) return;

            clipped = true;

            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY - 0.1, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));

            teleport = true;
        }

        MoveUtil.strafe();

        mc.timer.timerSpeed = 0.4f;
    };

    @EventLink
    private final Listener<PreMotionEvent> preMotionEventListener = event -> {
        if (!getParent().ncpMode.is("Normal")) return;

        final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, 1, 0);

        if (started) {
            mc.player.motionY += 0.025;
            MoveUtil.strafe(moveSpeed *= 0.935F);

            if (mc.player.motionY < -0.5 && !PlayerUtil.isBlockUnder()) {
                toggle();
            }
        }

        if (mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty() && !started) {
            started = true;
            mc.player.jump();
            MoveUtil.strafe(moveSpeed = 9);
        }
    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }

    @Override
    public void onEnable() {
        ChatUtil.display("Start the fly under the block and walk forward");

        moveSpeed = 0;
        notUnder = false;
        started = false;
        clipped = false;
        teleport = false;
    }
}
