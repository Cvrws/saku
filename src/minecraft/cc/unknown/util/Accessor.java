package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Sakura;
import cc.unknown.managers.ModuleManager;
import cc.unknown.module.Module;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.theme.Themes;
import cc.unknown.util.client.CustomLogger;
import cc.unknown.util.client.StopWatch;
import net.minecraft.client.Minecraft;

public interface Accessor {
    Minecraft mc = Minecraft.getInstance();
    
    default Sakura getInstance() {
        return Sakura.instance;
    }
    
    default boolean isInGame() {
        return mc != null || mc.player != null || mc.world != null;
    }
    
    default StopWatch getStopWatch() {
    	return new StopWatch();
    }
    
    default RiceGui getClickGUI() {
        return getInstance().getClickGui();
    }
    
    default ModuleManager getModuleManager() {
    	return getInstance().getModuleManager();
    }
    
    default CustomLogger getLogger() {
    	return Sakura.instance.getLogger();
    }

    default Themes getTheme() {
        return getInstance().getThemeManager().getTheme();
    }

    default <T extends Module> T getModule(final Class<T> clazz) {
        return getInstance().getModuleManager().get(clazz);
    }

    default Gson getGSON() {
        return getInstance().getGSON();
    }

    default boolean isEnabled(Class<? extends Module>... modules) {
        for (Class<? extends Module> module : modules) {
            if (getModule(module).isEnabled()) return true;
        }
        return false;
    }
}
