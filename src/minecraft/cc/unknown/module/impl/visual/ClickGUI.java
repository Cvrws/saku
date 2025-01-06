package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.GuiKeyBoardEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Click GUI", description = "Abre la interfaz que permite editar la configuración de cada modulo..", category = Category.VISUALS, keyBind = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Rice"))
			.setDefault("Rice");
	
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onEnable() {
    	if (mode.is("Rice")) {
	        mc.displayGuiScreen(Sakura.instance.getClickGui());
	        stopWatch.reset();
    	}
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
            	if (mode.is("Rice")) {
            		mc.displayGuiScreen(Sakura.instance.getClickGui());
            	}
                this.toggle();
            } else {
                mc.displayGuiScreen(null);
                this.toggle();
            }

            stopWatch.reset();
        }
    };
}
