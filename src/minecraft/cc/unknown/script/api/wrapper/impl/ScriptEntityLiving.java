package cc.unknown.script.api.wrapper.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public class ScriptEntityLiving extends ScriptEntity {

    public EntityLivingBase wrappedLiving;

    public ScriptEntityLiving(final EntityLivingBase wrapped) {
        super(wrapped);

        this.wrappedLiving = wrapped;
    }

    private static ScriptEntityLiving tryInstantiate(final Entity entity) {
        if (entity instanceof EntityLiving) {
            return new ScriptEntityLiving((EntityLiving) entity);
        }
        return null;
    }

    public boolean isAnimal() {
        return this.wrappedLiving instanceof EntityAnimal;
    }

    public boolean isMob() {
        return this.wrappedLiving instanceof EntityMob;
    }

    public boolean isPlayer() {
        return this.wrappedLiving instanceof EntityPlayer;
    }

    public float getHealth() {
        return this.wrappedLiving.getHealth();
    }

    public float getMaxHealth() {
        return this.wrappedLiving.getMaxHealth();
    }

    public int getHurtTime() {
        return this.wrappedLiving.hurtTime;
    }

    public int getMaxHurtTime() {
        return this.wrappedLiving.maxHurtTime;
    }

    public ScriptItemStack getHeldItemStack() {
        return new ScriptItemStack(this.wrappedLiving.getHeldItem());
    }

    public boolean isDead() {
        return this.wrappedLiving.isDead;
    }
}