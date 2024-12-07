package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.DisconnectionEvent;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(aliases = "Perfect Criticals", description = "Obtén criticos perfectos utilizando la latencia", category = Category.COMBAT)
public class PerfectCriticals extends Module {
	
	private NumberValue delay = new NumberValue("Delay", this, 120, 10, 500, 10);
	private NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1);
	
	private List<Packet<?>> packets = new ArrayList<>();
	private StopWatch stopWatch = new StopWatch();
	private boolean onAir = false;
	private boolean hitGround = false;
	
	@Override
	public void onDisable() {
		releasePackets();
	}
	
	@EventLink
	public Listener<PacketSendEvent> onSend = event -> {
		Packet packet = event.getPacket();
		
		if (mc.player.onGround)
			hitGround = true;

		if (!stopWatch.reached(delay.getValue().longValue()) && onAir) {
			event.setCancelled();
			packets.add(packet);
		}

		if (stopWatch.reached(delay.getValue().longValue()) && onAir) {
			onAir = false;
			releasePackets();
		}
	};
	
	@EventLink
	public Listener<AttackEvent> onAttack = event -> {
		boolean chance = (this.chance.getValue().intValue() / 100) > Math.random();
		
		if (!mc.player.onGround) {
			if (!onAir && hitGround && mc.player.fallDistance <= 1 && chance) {
				stopWatch.reset();
				onAir = true;
				hitGround = false;
			}
			return;
		}

		if (onAir) {
		    mc.player.onCriticalHit(event.getTarget());
		}
	};
	
	@EventLink
	public Listener<PacketReceiveEvent> onReceive = event -> {
		Packet packet = event.getPacket();
		
		if (mc.player == null) hitGround = true;
		if (packet instanceof S08PacketPlayerPosLook) hitGround = true;
	};
	
	@EventLink
	public Listener<DisconnectionEvent> onDisconnect = event -> {
		this.toggle();
	};
	
	private void releasePackets() {
		if (!packets.isEmpty())
			packets.forEach(PacketUtil::sendNoEvent);
		
		packets.clear();
		stopWatch.reset();
	}

}
