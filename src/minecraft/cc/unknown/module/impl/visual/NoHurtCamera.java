package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Hurt Camera", description = "Modifica la animación de la cámara al recibir daño", category = Category.VISUALS)
public final class NoHurtCamera extends Module {

}