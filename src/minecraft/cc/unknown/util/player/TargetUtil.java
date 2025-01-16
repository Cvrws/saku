package cc.unknown.util.player;

import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.Accessor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;

public class TargetUtil implements Accessor {
    private static KillAura killAura;
    
    public static List<EntityLivingBase> getTarget(double range) {
    	if (killAura == null) {
            killAura = Sakura.instance.getModuleManager().get(KillAura.class);
        }

    	return mc.world.loadedEntityList.stream()
    			.filter(entity -> entity instanceof EntityLivingBase)
    			.map(entity -> (EntityLivingBase) entity)
    			.filter(living -> {
    				String name = living.getName();
    				if (living == mc.player) return false;
    				if (living.deathTime > 0 || living.ticksExisted < 1 || living.isDead) return false;
    				if (living.isInvisible() && !killAura.invisibles.getValue()) return false;

    				if (living instanceof EntityPlayer) {
    					EntityPlayer player = (EntityPlayer) living;
    					if (!killAura.player.getValue()) return false;
    					if (FriendUtil.isFriend(name) && !killAura.friends.getValue()) return false;
    					if (PlayerUtil.isTeam(player, killAura.scoreboardCheckTeam.getValue(), killAura.checkArmorColor.getValue())) return false;
    				}

    				if (living instanceof IAnimals && !killAura.animals.getValue()) return false;
    				if (living instanceof IMob && !killAura.mobs.getValue()) return false;
    				if (name.contains("[NCP]") || name.contains("CLICK DERECHO") || name.contains("MEJORAS")) return false;
    				if (living instanceof EntityArmorStand || living instanceof INpc) return false;
    				
    				return mc.player.getDistanceToEntity(living) < range;
    			})
                .collect(Collectors.toList());
    }
}