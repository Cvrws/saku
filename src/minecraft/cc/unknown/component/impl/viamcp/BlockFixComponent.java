package cc.unknown.component.impl.viamcp;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;

import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemSword;

public final class BlockFixComponent extends Component {

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
			if (getComponent(Slot.class).getItemStack() != null
					&& getComponent(Slot.class).getItemStack().getItem() instanceof ItemSword) {
                PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem.write(Type.VAR_INT, 1);
                PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
			}
		}
	};
}