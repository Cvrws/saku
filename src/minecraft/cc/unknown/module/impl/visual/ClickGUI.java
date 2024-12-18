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

@ModuleInfo(aliases = "Click GUI", description = "Abre la interfaz que permite encender/apagar módulos y editar su configuración.", category = Category.VISUALS, keyBind = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Old"))
			.add(new SubMode("Beta [Bug]"))
			.setDefault("Old");
	
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onEnable() {
    	if (mode.is("Old")) {
	        mc.displayGuiScreen(Sakura.instance.getClickGui());
	        stopWatch.reset();
    	} else if (mode.is("Beta [Bug]")){
    		mc.displayGuiScreen(Sakura.instance.getBetaGui());
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
            	if (mode.is("Old")) {
            		mc.displayGuiScreen(Sakura.instance.getClickGui());
            	} else if (mode.is("Beta [Bug]")){
            		mc.displayGuiScreen(Sakura.instance.getBetaGui());
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
