package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.NumberValue;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;

@ModuleInfo(aliases = "Fake Lag", description = "Bad Wifi", category = Category.LATENCY)
public final class FakeLag extends Module {

}