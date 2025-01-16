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
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;

public class TargetUtil implements Accessor {
    private static KillAura killAura;

    public static EntityLivingBase getTarget(double range) {
        return getTargets(range).stream().findFirst().orElse(null);
    }

    public static List<EntityLivingBase> getTargets(double range) {
        if (killAura == null) {
            killAura = Sakura.instance.getModuleManager().get(KillAura.class);
        }

        return getTargets(killAura.player.getValue(), killAura.invisibles.getValue(), killAura.animals.getValue(), killAura.mobs.getValue(), killAura.teams.getValue()).stream().filter(entity -> mc.player.getDistanceToEntity(entity) <= range).collect(Collectors.toList());
    }

    public static List<EntityLivingBase> getTargets(double range, boolean players, boolean invisibles, boolean animals, boolean mobs, boolean teams) {
        return getTargets(players, invisibles, animals, mobs, teams).stream().filter(entity -> mc.player.getDistanceToEntity(entity) <= range).collect(Collectors.toList());
    }

    public static List<EntityLivingBase> getTargets() {
        if (killAura == null) {
            killAura = Sakura.instance.getModuleManager().get(KillAura.class);
        }

        return getTargets(killAura.player.getValue(), killAura.invisibles.getValue(), killAura.animals.getValue(), killAura.mobs.getValue(), killAura.teams.getValue(), killAura.friends.getValue());
    }

    public static List<EntityLivingBase> getTargets(boolean players, boolean invisibles, boolean animals, boolean mobs, boolean teams) {
        return getTargets(players, invisibles, animals, mobs, teams, teams);
    }
    
    public static List<EntityLivingBase> getTargets(boolean players, boolean invisibles, boolean animals, boolean mobs,
            boolean teams, boolean friends) {
        return mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase && entity != mc.getRenderViewEntity()
                && (friends || FriendUtil.isFriend(entity.getName()))
                && (!(entity instanceof EntityPlayer) || players)
                && (!(entity instanceof IMob || entity instanceof INpc) || mobs)
                && (!(entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntityWaterMob) || animals)
                && (!entity.isInvisible() || invisibles) && !(entity instanceof EntityArmorStand))
                .map(entity -> ((EntityLivingBase) entity))
                .filter(entity -> !entity.getName().contains("[NPC]"))
                .filter(entity -> !entity.getName().contains("CLICK DERECHO"))
                .filter(entity -> !entity.getName().contains("MEJORAS"))
                .filter(entity -> !(entity instanceof EntityPlayer && PlayerUtil.isTeam((EntityPlayer) entity, killAura.scoreboardCheckTeam.getValue(), killAura.checkArmorColor.getValue()) && teams))
                .collect(Collectors.toList());
    }
}