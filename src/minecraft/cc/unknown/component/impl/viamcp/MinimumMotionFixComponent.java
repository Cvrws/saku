package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.MinimumMotionEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public final class MinimumMotionFixComponent extends Component {

    @EventLink
    public final Listener<MinimumMotionEvent> onMinimumMotion = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            event.setMinimumMotion(0.003D);
        }
    };
}