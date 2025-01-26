package cc.unknown.util.render;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FreeLookUtil implements Accessor {
    public float cameraYaw;
    public float cameraPitch;
    public boolean freelooking;

    public void overrideMouse(float f3, float f4) {
        cameraYaw += f3 * 0.15F;
        cameraPitch -= f4 * 0.15F;
        cameraPitch = Math.max(-90.0F, Math.min(90.0F, cameraPitch));
    }

    public float getYaw() {
        return freelooking ? cameraYaw : mc.player.rotationYaw;
    }

    public float getPitch() {
        return freelooking ? cameraPitch : mc.player.rotationPitch;
    }

    public float getPrevYaw() {
        return freelooking ? cameraYaw : mc.player.prevRotationYaw;
    }

    public float getPrevPitch() {
        return freelooking ? cameraPitch : mc.player.prevRotationPitch;
    }

    public void setFreelooking(boolean setFreelook) {
        freelooking = setFreelook;
    }

    public void enable() {
        setFreelooking(true);
        cameraYaw = mc.player.rotationYaw;
        cameraPitch = mc.player.rotationPitch;
    }

    public void disable() {
        setFreelooking(false);
        cameraYaw = mc.player.rotationYaw;
        cameraPitch = mc.player.rotationPitch;
    }
}