package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptPreMotionEvent extends CancellableScriptEvent<PreMotionEvent> {

    public ScriptPreMotionEvent(final PreMotionEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setPosX(final double posX) {
        this.wrapped.setPosX(posX);
    }

    public void setPosY(final double posY) {
        this.wrapped.setPosY(posY);
    }

    public void setPosZ(final double posZ) {
        this.wrapped.setPosZ(posZ);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public void setPitch(final float pitch) {
        this.wrapped.setPitch(pitch);
    }

    public void setOnGround(final boolean onGround) {
        this.wrapped.setOnGround(onGround);
    }

    public void setSprinting(final boolean sprinting) {
        this.wrapped.setSprinting(sprinting);
    }

    public double getPosX() {
        return this.wrapped.getPosX();
    }

    public double getPosY() {
        return this.wrapped.getPosY();
    }

    public double getPosZ() {
        return this.wrapped.getPosZ();
    }

    public float getYaw() {
        return this.wrapped.getYaw();
    }

    public float getPitch() {
        return this.wrapped.getPitch();
    }

    public boolean isOnGround() {
        return this.wrapped.isOnGround();
    }
    
    public boolean isSneaking() {
    	return this.wrapped.isSneaking();			
    }

    public boolean isSprinting() {
        return this.wrapped.isSprinting();
    }
    
    public void setSneaking(final boolean sneaking) {
        this.wrapped.setSneaking(sneaking);
    }

    @Override
    public String getHandlerName() {
        return "onPreMotion";
    }
}
