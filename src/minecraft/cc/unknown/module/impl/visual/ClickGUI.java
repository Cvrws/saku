package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.GuiKeyBoardEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.time.StopWatch;

@ModuleInfo(aliases = "Click GUI", description = "Displays a GUI that allows you to toggle modules and edit their settings", category = Category.VISUALS, keyBind = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Sakura.instance.getClickGui());
        stopWatch.reset();
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
        Sakura.instance.getConfigManager().get("latest").write();
    }

    @EventLink
    public final Listener<GuiKeyBoardEvent> onKey = event -> {
        if (!stopWatch.finished(50)) return;

        if (event.getKeyCode() == this.getKey() || event.getKeyCode() == 1) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(Sakura.instance.getClickGui());
                this.toggle();
            } else {
                mc.displayGuiScreen(null);
                this.toggle();
            }

            stopWatch.reset();
        }
    };
}
