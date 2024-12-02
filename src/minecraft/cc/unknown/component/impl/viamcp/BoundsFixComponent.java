package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.util.AxisAlignedBB;

public final class BoundsFixComponent extends Component {

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            mc.player.setEntityBoundingBox(new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY,
                    mc.player.posZ - 0.3, mc.player.posX + 0.3, mc.player.posY + 1.8,
                    mc.player.posZ + 0.3));
        }
    };
}