package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(aliases = "Velocity", description = "Te vuelve un gordito come hamburguesas haciendo que no tengas kb.", category = Category.COMBAT)
public final class Velocity extends Module {
	
	private final NumberValue vertical = new NumberValue("Vertical", this, 90, 0, 100, 1);
	private final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1);
	
	private final BooleanValue delay = new BooleanValue("Delay", this, false);
    private final NumberValue delayHorizontal = new NumberValue("Delayed Horizontal", this, 100, 0, 100, 1, () -> !delay.getValue());
    private final NumberValue delayVertical = new NumberValue("Delayed Vertical", this, 90, 0, 100, 1, () -> !delay.getValue());
    
    private final BooleanValue attack = new BooleanValue("Attack", this, false);
    private final NumberValue attackHorizontal = new NumberValue("Horizontal Attack", this, 100, 0, 100, 1, () -> !attack.getValue());
    private final NumberValue attackVertical = new NumberValue("Vertical Attack", this, 90, 0, 100, 1, () -> !attack.getValue());
    
    private final BooleanValue onlyAir = new BooleanValue("Only in Air", this, false);
	
	private int ticks;
	private double motionY, motionX, motionZ;
	
	@EventLink
	public final Listener<PacketReceiveEvent> onReceive = event -> {
		if (mc.player.onGround && onlyAir.getValue()) return;
		
		final Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
            final double horizontal = this.horizontal.getValue().doubleValue();
            final double vertical = this.vertical.getValue().doubleValue();

            if (horizontal == 0.0 && vertical == 0.0) {
                event.setCancelled();
                return;
            }
            
            wrapper.motionX *= horizontal / 100.0;
            wrapper.motionY *= vertical / 100.0;
            wrapper.motionZ *= horizontal / 100.0;
            event.setPacket(wrapper);
        }
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		 ticks++;
		 
         if (mc.player.hurtTime == 9) {
             ticks = 0;
         }

         assert mc.player != null;
         
         if (mc.player.hurtTime == 9) {
             motionX = mc.player.motionX;
             motionY = mc.player.motionY;
             motionZ = mc.player.motionZ;
         }
         
         final double horizontal = this.delayHorizontal.getValue().doubleValue();
         final double vertical = this.delayVertical.getValue().doubleValue();

         if (mc.player.hurtTime == 8) {
             mc.player.motionX *= horizontal / 100;
             mc.player.motionY *= vertical / 100;
             mc.player.motionZ *= horizontal / 100;
         }
	};
	
	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		if (mc.player.onGround && onlyAir.getValue()) return;
		
        final double horizontal = this.attackHorizontal.getValue().doubleValue();
        final double vertical = this.attackVertical.getValue().doubleValue();
		
        if (mc.player.hurtTime > 0) {
            mc.player.motionZ *= horizontal;
            mc.player.motionY *= vertical;
            mc.player.motionX *= horizontal;
        }
	};
}