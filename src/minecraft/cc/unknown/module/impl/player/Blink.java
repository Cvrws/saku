package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PostUpdateEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.BlinkUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

@ModuleInfo(aliases = "Blink", description = "Bloquea temporalmente los datos que se envían al servidor.", category = Category.PLAYER)
public class Blink extends Module {

    private final BooleanValue outbound = new BooleanValue("Outbound", this, true);
    private final BooleanValue inbound = new BooleanValue("Inbound", this, false);
    private final BooleanValue pulse = new BooleanValue("Pulse", this, false);
    private final NumberValue delayValue = new NumberValue("Delay", this, 300, 0, 2000, 25, () -> !pulse.getValue());
    private final BooleanValue disableOnAttack = new BooleanValue("Disable On Attack", this, false);
    private int delay;

    @Override
    public void onDisable() {
    	BlinkUtil.disable();
    }
	
    @EventLink
    public final Listener<PostUpdateEvent> onPost = event -> {
        if (!pulse.getValue()) return;
        if (++delay > delayValue.getValue().intValue() / 50) {
        	BlinkUtil.releasePackets();
            delay = 0;
        }
    };
    
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		handleDelay();
	};
	
	@EventLink
	public final Listener<PostMotionEvent> onPostMotion = event -> {
		handleDelay();
	};
	
	private void handleDelay() {
        if (this.isEnabled() && mc.player != null) {
        	BlinkUtil.enable(inbound.getValue(), outbound.getValue());
        }
	}

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (disableOnAttack.getValue()) {
            toggle();
        }
    };
}