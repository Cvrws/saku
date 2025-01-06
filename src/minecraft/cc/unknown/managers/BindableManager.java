package cc.unknown.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.event.impl.input.MouseEvent;
import cc.unknown.util.interfaces.Bindable;
import net.minecraft.client.Minecraft;

public class BindableManager {
    
    int[] inputs = {0, 1, 2, 3, 4, 5};
    boolean[] downs = {false, false, false, false, false, false};
	
    public void init() {
        Sakura.instance.getEventBus().register(this);
    }

    public List<Bindable> getBinds() {
        List<Bindable> bindableList = new ArrayList<>();
        bindableList.addAll(Sakura.instance.getModuleManager().getAll());
        return bindableList;
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<KeyboardInputEvent> onKey = event -> {
        if (event.getGuiScreen() != null || event.isCancelled()) return;

        getBinds().stream().filter(bind -> bind.getKey() == event.getKeyCode()).forEach(Bindable::onKey);
    };
    
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<MouseEvent> onMouse = event -> {
        if (Minecraft.getInstance().currentScreen != null || event.isCancelled()) return;

        for (int i = 0; i < inputs.length; i++) {
            int input = inputs[i];
            if (!downs[i]) {
                downs[i] = true;
                getBinds().stream().filter(bind -> bind.getKey() == (100 + input)).forEach(Bindable::onKey);
            } else if (downs[i]) {
                downs[i] = false;
            }
        }
    };

    public <T extends Bindable> T get(final String name) {
        return (T) getBinds().stream().filter(module -> Arrays.stream(module.getAliases()).anyMatch(alias -> alias.replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))).findAny().orElse(null);
    }
}
