package cc.unknown.module.impl.move.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.move.Flight;
import cc.unknown.util.structure.geometry.Vector3d;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class NewVulcanFlight extends Mode<Flight> {

    public NewVulcanFlight(String name, Flight parent) {
        super(name, parent);
    }

    private Vector3d teleport;
    private boolean attempt;
    private int teleports;

    @Override
    public void onEnable() {
        teleport = null;
        attempt = false;
        teleports = 0;
    }

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (teleports > 0 && !attempt) {
            teleports--;
            mc.timer.timerSpeed = getParent().speed.getValue().floatValue();
        }
    };

    @EventLink
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (event.getBlock() instanceof BlockAir && !mc.player.isSneaking()) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y < mc.player.posY) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
        }
    };

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        Vector3d position = new Vector3d(event.getPosX(), event.getPosY(), event.getPosZ());

        if (teleport == null) {
            teleport = position;
            event.setCancelled();
            teleports += 2;
        } else if (!teleport.equals(position)) {
            getParent().toggle();
        } else {
            event.setCancelled();
            teleports += 2;
        }
    };

    @EventLink
    public final Listener<KeyboardInputEvent> onKey = event -> {
        if (event.getKeyCode() == getParent().getKey() && !attempt) {
            event.setCancelled();
            mc.player.jump();
            attempt = true;
        }
    };
}
