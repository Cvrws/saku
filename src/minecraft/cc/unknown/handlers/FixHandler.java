package cc.unknown.handlers;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.util.Accessor;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

public class FixHandler implements Accessor {
	
	private boolean inGUI;
	
	/*
	 * Gui Close Fix
	 */
	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.currentScreen == null && inGUI) {
			for (final KeyBinding bind : mc.gameSettings.keyBindings) {
				bind.setPressed(GameSettings.isKeyDown(bind));
			}
		}

		inGUI = mc.currentScreen != null;
	};
	
	/*
	 * Particles Fix
	 */
    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof S2APacketParticles) {
        	final S2APacketParticles wrapper = ((S2APacketParticles) packet);
        	
        	final double distance = mc.player.getDistanceSq(wrapper.getXCoordinate(), wrapper.getYCoordinate(), wrapper.getZCoordinate());
        	
        	if (distance >= 26) {
        		event.setCancelled();
	        }
        }
    };
}
