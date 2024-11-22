package cc.unknown.module.impl.player;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.nofall.*;
import cc.unknown.value.impl.ModeValue;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

@ModuleInfo(aliases = {"No Fall"}, description = "Reduce o elimina los daños de caida", category = Category.PLAYER)
public class NoFall extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new PlaceNoFall("Place", this))
            .add(new LegitNofall("Legit", this))
            .add(new PacketNoFall("Packet", this))
            .add(new MatrixNoFall("Matrix", this))
            .setDefault("Legit");
}
