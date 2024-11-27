package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Fake Hackers", description = ">:3c", category = Category.PLAYER)
public class FakeHackers extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
	        .add(new SubMode("Sneak"))
	        .add(new SubMode("KillAura"))
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
	                    EntityLivingBase toFace = getClosestEntityToEntity(range.getValue().floatValue(), player);
	                    if (toFace != null) {
	                        float[] rots = getFacePosEntityRemote(player, toFace);
	                        if (rots != null && rots.length == 2) {
	                            player.swingItem();
	                            player.rotationYawHead = rots[0];
	                            player.rotationPitch = rots[1];
	                        }
	                    }
	                }
	            }
	        }
	    }
	};
    
    private EntityLivingBase getClosestEntityToEntity(float range, Entity ent) {
		EntityLivingBase closestEntity = null;
		float mindistance = range;
		for (Object o : mc.world.loadedEntityList) {
			if (isNotItem(o) && !ent.isEntityEqual((EntityLivingBase) o)) {
				EntityLivingBase en = (EntityLivingBase) o;
				if (ent.getDistanceToEntity(en) < mindistance) {
					mindistance = ent.getDistanceToEntity(en);
					closestEntity = en;
				}
			}
		}
		return closestEntity;
	}

    private boolean isNotItem(Object o) {
		if (!(o instanceof EntityLivingBase)) {
			return false;
		}
		return true;
	}
    
    private float[] getFacePosEntityRemote(EntityLivingBase facing, Entity en) {
		if (en == null) {
			return new float[] { facing.rotationYawHead, facing.rotationPitch };
		}
		return getFacePosRemote(new Vec3(facing.posX, facing.posY + en.getEyeHeight(), facing.posZ),
				new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ));
	}
        
    private float[] getFacePosRemote(Vec3 src, Vec3 dest) {
		double diffX = dest.xCoord - src.xCoord;
		double diffY = dest.yCoord - (src.yCoord);
		double diffZ = dest.zCoord - src.zCoord;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) };
	}
}