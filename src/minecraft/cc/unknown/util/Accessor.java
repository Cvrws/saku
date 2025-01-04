package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Sakura;
import cc.unknown.module.impl.Module;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.theme.Themes;
import net.minecraft.client.Minecraft;

public interface Accessor {
    Minecraft mc = Minecraft.getMinecraft();

    default Sakura getInstance() {
        return Sakura.instance;
    }
    
    default boolean isInGame() {
        return mc != null || mc.player != null || mc.theWorld != null;
    }
    
    default RiceGui getClickGUI() {
        return getInstance().getClickGui();
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
}
