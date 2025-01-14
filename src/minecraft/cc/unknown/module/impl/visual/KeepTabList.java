package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(aliases = "Keep Tab List", description = "Manten presionado siempre el tab", category = Category.VISUALS)
public final class KeepTabList extends Module {
	
    @EventLink
    public final Listener<PreUpdateEvent> onPreLiving = event -> {
        if (!isInGame()) return;

        mc.gameSettings.keyBindPlayerList.pressed = true;
    };

    @Override
    public void onDisable() {
    	mc.gameSettings.keyBindPlayerList.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindPlayerList);
    }
}