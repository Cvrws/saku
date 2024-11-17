package cc.unknown.module.impl.movement;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Parkour", description = "Automatically jumps at the edge of blocks", category = Category.MOVEMENT)
public class Parkour extends Module {

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		double posX = mc.player.posX;
		double posZ = mc.player.posZ;
		double blockX = Math.floor(posX);
		double blockZ = Math.floor(posZ);

		double difX = posX - blockX;
		double difZ = posZ - blockZ;

		double edgeMargin = 0D;
		double lowerThreshold = 0.2 - edgeMargin;
		double upperThreshold = 0.4 + edgeMargin;

		boolean onEdgeX = difX <= lowerThreshold || difX >= upperThreshold;
		boolean onEdgeZ = difZ <= lowerThreshold || difZ >= upperThreshold;
		BlockPos blockInFront = null;
		
		float moveForward = mc.player.moveForward;
		float moveStrafing = mc.player.moveStrafing;
		Vec3 lookVec = mc.player.getLookVec();
		boolean jump = mc.gameSettings.keyBindJump.pressed;
		
		if (moveForward > 0) {
			blockInFront = mc.player.getPosition().add(lookVec.xCoord, -1, lookVec.zCoord);
		} else if (moveForward < 0) {
			blockInFront = mc.player.getPosition().add(-lookVec.xCoord, -1, -lookVec.zCoord);
		} else if (moveStrafing > 0) {
			blockInFront = mc.player.getPosition().add(lookVec.zCoord, -1, -lookVec.xCoord);
		} else if (moveStrafing < 0) {
			blockInFront = mc.player.getPosition().add(-lookVec.zCoord, -1, lookVec.xCoord);
		}

		boolean isBlockAir = blockInFront != null && mc.player.getEntityWorld().isAirBlock(blockInFront);
		boolean shouldJump = (onEdgeX || onEdgeZ);
		if (shouldJump && isBlockAir && mc.player.onGround && moveForward > 0 && !mc.player.isSneaking()) {
			jump = true;
		} else {
			jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		}

	};

}
