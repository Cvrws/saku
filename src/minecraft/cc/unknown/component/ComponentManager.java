package cc.unknown.component;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.event.*;
import cc.unknown.component.impl.hud.*;
import cc.unknown.component.impl.patches.*;
import cc.unknown.component.impl.performance.*;
import cc.unknown.component.impl.player.*;
import cc.unknown.component.impl.viamcp.*;

public final class ComponentManager {

    private final Map<Class<Component>, Component> componentList = new HashMap<>();

    public void init() {
        // event
    	this.add(new EntityKillEventComponent());
        this.add(new EntityTickComponent());
        
        // hud
        this.add(new DragComponent());
        
        // patches
        this.add(new GuiClosePatchComponent());
        
        // perfomance
        this.add(new ParticleDistanceComponent());
        
        // player
        this.add(new LastConnectionComponent());
        this.add(new RotationComponent());
        this.add(new Slot());
        this.add(new SecurityComponent());
        this.add(new TargetComponent());
        this.add(new GameComponent());

        // viamcp
        this.add(new BlockPlacementFixComponent());
        this.add(new BlockFixComponent());
        this.add(new FlyingPacketFixComponent());
        this.add(new LadderFixComponent());
        this.add(new MinimumMotionFixComponent());
        this.add(new BoundsFixComponent());
        this.add(new TransactionFixComponent());
        this.add(new SpeedFixComponent());
        this.add(new InteractEntityFixComponent());
        
        this.componentList.forEach((componentClass, component) -> Sakura.instance.getEventBus().register(component));
        this.componentList.forEach(((componentClass, component) -> component.onInit()));
    }

    public void add(final Component component) {
        this.componentList.put((Class<Component>) component.getClass(), component);
    }

    public <T extends Component> T get(final Class<T> clazz) {
        return (T) this.componentList.get(clazz);
    }
}