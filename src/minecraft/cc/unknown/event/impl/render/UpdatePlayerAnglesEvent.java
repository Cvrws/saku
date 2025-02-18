package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

@RequiredArgsConstructor
@Getter
public final class UpdatePlayerAnglesEvent extends CancellableEvent {
    private final EntityPlayer entityPlayer;
    private final ModelBiped modelBiped;
}