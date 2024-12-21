package cc.unknown.bindable;

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

public class BindableManager {
    
    public void init() {
        // Has to be a listener to handle the key presses
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

        getBinds().stream()
                .filter(bind -> bind.getKey() == event.getKeyCode())
                .forEach(Bindable::onKey);
    };
    
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<MouseEvent> onMouse = event -> {
        if (event.isCancelled()) return;

        getBinds().stream()
                .filter(bind -> bind.getKey() == (100 + event.getCode()))
                .forEach(Bindable::onKey);
    };

    public <T extends Bindable> T get(final String name) {
        return (T) getBinds().stream()
                .filter(module -> Arrays.stream(module.getAliases()).anyMatch(alias -> 
                        alias.replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))))
                .findAny().orElse(null);
    }
}
