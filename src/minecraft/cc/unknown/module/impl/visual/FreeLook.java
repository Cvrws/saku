package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.FreeLookUtil;

@ModuleInfo(aliases = "Free Look", description = "Le permite mirar a su alrededor sin cambiar de dirección", category = Category.VISUALS, keyBind = Keyboard.KEY_LMENU)
public final class FreeLook extends Module {

    private boolean freeLookingactivated;

    @Override
    public void onDisable() {
        freeLookingactivated = false;
        FreeLookUtil.setFreelooking(false);
        mc.gameSettings.thirdPersonView = 0;
    }

    @EventLink(value = Priority.LOW)
    public final Listener<TickEvent> onTick = event -> {
        if (this.getKey() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKey())) {
            this.setEnabled(false);
            return;
        }

        if (mc.player.ticksExisted < 10) {
            stop();
        }
        if (Keyboard.isKeyDown(getKey())) {
            if (!freeLookingactivated) {
                freeLookingactivated = true;
                FreeLookUtil.enable();
                FreeLookUtil.cameraYaw += 180;
                mc.gameSettings.thirdPersonView = 1;
            }
        } else if (freeLookingactivated) {
            stop();
        }
    };

    private void stop() {
        toggle();
        FreeLookUtil.setFreelooking(false);
        freeLookingactivated = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}