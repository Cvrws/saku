package cc.unknown.module.impl.movement;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.MoveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(aliases = "Clipper", description = "Traspasas los bloques hacia abajo al presionar shift.", category = Category.MOVEMENT)
public class Clipper extends Module {

    private boolean triggered, available, clipping;
    private int amount;
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
        available = false;

        boolean lastAir = false;
        for (int i = 0; i < 10; i++) {
            final Block block = PlayerUtil.blockRelativeToPlayer(0, -i, 0);

            if (block instanceof BlockAir) {
                if (lastAir && !(PlayerUtil.blockRelativeToPlayer(0, -i - 1, 0) instanceof BlockAir) && !(PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) && mc.player.onGround) {
                    available = true;
                    if (!triggered && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.setPosition(mc.player.posX, mc.player.posY - i, mc.player.posZ);
                        clipping = false;
                        triggered = true;
                    }
                }
                lastAir = true;
            } else
                lastAir = false;
        }

        if (mc.player.ticksExisted == 1)
            clipping = false;

        if (clipping)
            event.setPosY(event.getPosY() - amount);

        if (!mc.gameSettings.keyBindSneak.isKeyDown())
            triggered = false;
	};
	
	@EventLink
	public final Listener<MoveEvent> onMove = event -> {
        if (clipping)
            event.setCancelled(true);
	};
	
	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
        if (available)
            event.setSneak(false);
	};
	
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
        final Packet packet = event.getPacket();

        if (packet instanceof S08PacketPlayerPosLook && clipping) {
            final S08PacketPlayerPosLook wrapper = (S08PacketPlayerPosLook) packet;
            if (mc.player.posY - wrapper.getY() >= amount - 1 || wrapper.getY() > mc.player.posY)
                clipping = false;
            else
                event.setCancelled(true);
        }
	};
}