package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;

@Getter
@Setter
@AllArgsConstructor
public class PreRenderLivingEntityEvent extends CancellableEvent {
    private final EntityLivingBase target;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float rotationYawHead;
    private float rotationPitch;
    private float chestRot;
    private float offset;
}
