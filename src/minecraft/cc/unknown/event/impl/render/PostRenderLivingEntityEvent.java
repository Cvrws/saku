package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;

@Getter
@Setter
@RequiredArgsConstructor
public class PostRenderLivingEntityEvent extends CancellableEvent {
    private final EntityLivingBase target;
}
