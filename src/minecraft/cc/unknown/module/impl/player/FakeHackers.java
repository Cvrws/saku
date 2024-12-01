package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;

@ModuleInfo(aliases = "Fake Hackers", description = "Incrimina a otros", category = Category.PLAYER)
public class FakeHackers extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
	        .add(new SubMode("Sneak"))
	        .add(new SubMode("KillAura"))
	        .add(new SubMode("Reach"))
	        .setDefault("Sneak");
	
	private final NumberValue range = new NumberValue("Range", this, 6, 3.2, 6, 0.1);
	private final StringValue name = new StringValue("IGN", this, "PepeGamer777");
	
	@Override
	public void onDisable() {
	    if (mc.world != null) {
	        String playerName = name.getValue();
	        if (playerName != null && !playerName.isEmpty()) {
	            EntityPlayer player = mc.world.getPlayerEntityByName(playerName);
	            if (player != null && mode.is("Sneak")) {
	                player.setSneaking(false);
	            }
	        }
	    }
	    super.onDisable();
	}
	
	@EventLink
	public final Listener<PacketSendEvent> onPacketSend = event -> {
		Packet packet = event.getPacket();
		
		if (mode.is("Reach")) {
			if (packet instanceof C04PacketPlayerPosition) {
				C04PacketPlayerPosition wrapped = (C04PacketPlayerPosition) packet;
				double angleA = Math.toRadians(normalizeAngle(wrapped.getYaw() - 90.0F));
				wrapped.x = wrapped.x + Math.cos(angleA) * 0.5;
				wrapped.z = wrapped.z + Math.cos(angleA) * 0.5;
                
			}
			
			if (packet instanceof C06PacketPlayerPosLook) {
				C06PacketPlayerPosLook wrapped = (C06PacketPlayerPosLook) packet;
				double angleA = Math.toRadians(normalizeAngle(wrapped.getYaw() - 90.0F));
				wrapped.x = wrapped.x + Math.cos(angleA) * 0.5;
				wrapped.z = wrapped.z + Math.cos(angleA) * 0.5;
			}
		}
	};
 	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (mc.world != null) {
	        String playerName = name.getValue();
	        if (playerName != null && !playerName.isEmpty()) {
	            EntityPlayer player = mc.world.getPlayerEntityByName(playerName);
	            if (player != null) {
	                if (mode.is("Sneak")) {
	                    player.setSneaking(true);
	                }

	                if (mode.is("KillAura")) {
	                	float range = this.range.getValue().floatValue();
	                    if (mc.player.getDistanceToEntity(player) < range) {
	                        float[] yawAndPitch = getAnglesForThisEntityToHitYou(player);
	                        float yaw = yawAndPitch[0];
	                        float pitch = yawAndPitch[1];
	                        player.rotationYaw = yaw;
	                        player.setRotationYawHead(yaw);
	                        player.rotationPitch = pitch;
	                        player.cameraPitch = pitch;
	                        player.swingItem();
	                    }
	                }
	            }
	        }
	    }
	};
    
    private float[] getAnglesForThisEntityToHitYou(EntityLivingBase entityLiving) {
        double difX = mc.player.posX - entityLiving.posX;
        double difY = mc.player.posY - entityLiving.posY + (double) (mc.player.getEyeHeight() / 1.4f);
        double difZ = mc.player.posZ - entityLiving.posZ;
        double hypo = entityLiving.getDistanceToEntity((Entity) mc.player);
        float yaw = (float) Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(difY, hypo)));
        return new float[]{yaw, pitch};
    }
    
	private float normalizeAngle(float angle) {
        return (angle + 360.0F) % 360.0F;
    }

    private float getDistanceBetweenAngles(final float angle1, final float angle2) {
        return Math.abs(angle1 % 360.0f - angle2 % 360.0f) % 360.0f;
    }
}