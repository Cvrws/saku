package cc.unknown.component.impl.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.MouseOverEvent;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public final class HitboxFixComponent extends Component {

    @EventLink
    public final Listener<MouseOverEvent> onMouseOver = event -> {
        if (ViaLoadingBase.getInstance().getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
//            event.setExpand(event.getExpand() - 0.1F);
        }
    };
}
