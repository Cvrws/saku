package cc.unknown.module.impl.move;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING;
import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(aliases = "Sneak", description = "Te hace shiftear siempre o a veces sin frenar [BETA]", category = Category.MOVEMENT)
public class Sneak extends Module {
	
    @Override
    public void onDisable() {
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
    }
    
    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
        PacketUtil.send(new C0BPacketEntityAction(mc.player, START_SNEAKING));
    };
    
    @EventLink
	public final Listener<PreMotionEvent> oPrenMotion = event -> {
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, START_SNEAKING));
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
	};
}