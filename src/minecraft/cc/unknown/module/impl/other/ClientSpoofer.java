package cc.unknown.module.impl.other;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import cc.unknown.value.impl.TextValue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

@ModuleInfo(aliases = "Client Spoofer", description = ">:3c", category = Category.OTHER)
public final class ClientSpoofer extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Lunar"))
			.add(new SubMode("Feather"))
			.setDefault("Lunar");
	
	public final TextValue brand = new TextValue("Brand", this, "vanilla");
	
	@EventLink
	public final Listener<PacketSendEvent> onSend = event -> {
	    Packet<?> packet = event.getPacket();
	    if (packet instanceof C17PacketCustomPayload) {
	    	C17PacketCustomPayload wrapper = (C17PacketCustomPayload) packet;
	    	
	    	String data = "";
	    	switch (mode.getValue().getName()) {
	    	case "Lunar":
	    		data = "lunarclient:v2.14.5-2411";
	    		break;
	    	case "Feather":
	    		data = "Feather Forge";
	    		break;
	    	}

	        ByteBuf byteBuf = Unpooled.wrappedBuffer(data.getBytes());
	        PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(byteBuf));
	        wrapper.setData(buffer);
	    }
	};
}
