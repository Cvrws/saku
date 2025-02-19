package de.florianmichael.viamcp.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.util.netty.PacketUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;

public class AttackOrder {
	private final static Minecraft mc = Minecraft.getInstance();

	public static void sendConditionalSwing(MovingObjectPosition mop) {
		if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
			mc.player.swingItem();
		}
	}

	public static void sendFixedAttack(EntityPlayer entityIn, Entity target, boolean packetSwing) {
		if (ViaLoadingBase.getInstance().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
			if (packetSwing) {
				PacketUtil.send(new C0APacketAnimation());
			} else {
				mc.player.swingItem();
			}
			mc.playerController.attackEntity(entityIn, target);
		} else {
			mc.playerController.attackEntity(entityIn, target);
			if (packetSwing) {
				PacketUtil.send(new C0APacketAnimation());
			} else {
				mc.player.swingItem();
			}
		}
	}
}
