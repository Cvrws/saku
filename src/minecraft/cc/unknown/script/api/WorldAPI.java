package cc.unknown.script.api;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.script.api.wrapper.impl.ScriptBlockPos;
import cc.unknown.script.api.wrapper.impl.ScriptEntityLiving;
import cc.unknown.script.api.wrapper.impl.ScriptWorld;
import cc.unknown.util.player.TargetUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

public class WorldAPI extends ScriptWorld {

    public WorldAPI() {
        super(MC.theWorld);

        Sakura.instance.getEventBus().register(this);
    }

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
        if (this.wrapped == null) {
            this.wrapped = MC.theWorld;
        }
    };

    public ScriptEntityLiving[] getEntities() {
        final Object[] entityLivingBases = MC.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).toArray();
        final ScriptEntityLiving[] scriptEntities = new ScriptEntityLiving[entityLivingBases.length];

        for (int index = 0; index < entityLivingBases.length; index++) {
            scriptEntities[index] = new ScriptEntityLiving((EntityLivingBase) entityLivingBases[index]);
        }

        return scriptEntities;
    }

    public ScriptEntityLiving getTargetEntity(int range) {
        EntityLivingBase entityLivingBase = TargetUtil.getTarget(range);
        return entityLivingBase != null ? new ScriptEntityLiving(entityLivingBase) : null;
    }

    public void removeEntity(int id) {
        MC.theWorld.removeEntityFromWorld(id);
    }

    public void removeEntity(ScriptEntityLiving entity) {
        removeEntity(entity.getEntityId());
    }

    public ScriptBlockPos newBlockPos(int x, int y, int z) {
        return new ScriptBlockPos(new BlockPos(x, y, z));
    }

    public String getBlockName(ScriptBlockPos blockPos) {
        return blockPos.getBlock().getName();
    }

}
