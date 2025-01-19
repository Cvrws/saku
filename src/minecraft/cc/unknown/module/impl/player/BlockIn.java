package cc.unknown.module.impl.player;

import java.util.NoSuchElementException;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.EnumFacingOffset;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Triple;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.util.structure.geometry.Vector3d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleInfo(aliases = "Block In", description = "si", category = Category.PLAYER)
public class BlockIn extends Module {

	private final NumberValue placeDelay = new NumberValue("Place Delay", this, 50, 0, 500, 1);
	private final BooleanValue silentSwing = new BooleanValue("Silent Swing", this, false);

	private Vector2f currentRot = null;
	private long lastPlace = 0;
	private Vec3 targetBlock;
	private EnumFacingOffset enumFacing;
	private BlockPos blockFace;

	@Override
	public void onDisable() {
		currentRot = null;
		lastPlace = 0;
		targetBlock = null;
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (currentRot == null)
			return;
		event.setYaw(currentRot.x);
		event.setPitch(currentRot.y);
	};

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {

	};
}
