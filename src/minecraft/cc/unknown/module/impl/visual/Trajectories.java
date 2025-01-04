package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Trajectories", description = "Renderiza la trayección de los proyectiles", category = Category.VISUALS)
public final class Trajectories extends Module {
	
	private final ArrayList<Vec3> positions = new ArrayList<Vec3>();

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		positions.clear();
		final ItemStack itemStack = mc.player.getCurrentEquippedItem();
		MovingObjectPosition m = null;
		if (itemStack != null && (itemStack.getItem() instanceof ItemSnowball || itemStack.getItem() instanceof ItemEgg || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemEnderPearl)) {
			final EntityLivingBase player = mc.player;
			float rotationYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getPartialTicks();
			float rotationPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.getPartialTicks();
			double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double posY = player.lastTickPosY + player.getEyeHeight() + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
			posX -= MathHelper.cos((float) (rotationYaw / 180.0f * Math.PI)) * 0.16f;
			posY -= 0.10000000149011612;
			posZ -= MathHelper.sin((float) (rotationYaw / 180.0f * Math.PI)) * 0.16f;
			float multipicator = 0.4f;
			if (itemStack.getItem() instanceof ItemBow) {
				multipicator = 1.0f;
			}
			double motionX = -MathHelper.sin((float) (rotationYaw / 180.0f * Math.PI)) * MathHelper.cos((float) (rotationPitch / 180.0f * Math.PI)) * multipicator;
			double motionZ = MathHelper.cos((float) (rotationYaw / 180.0f * Math.PI)) * MathHelper.cos((float) (rotationPitch / 180.0f * Math.PI)) * multipicator;
			double motionY = -MathHelper.sin((float) (rotationPitch / 180.0f * Math.PI)) * multipicator;
			double x = motionX;
			double y = motionY;
			double z = motionZ;
			final float inaccuracy = 0.0f;
			float velocity = 1.5f;
			if (itemStack.getItem() instanceof ItemBow) {
				final int i = mc.player.getItemInUseDuration() - mc.player.getItemInUseCount();
				float f = i / 20.0f;
				f = (f * f + f * 2.0f) / 3.0f;
				if (f < 0.1) {
					return;
				}
				if (f > 1.0f) {
					f = 1.0f;
				}
				velocity = f * 2.0f * 1.5f;
			}
			final Random rand = new Random();
			final float ff = MathHelper.sqrt_double(x * x + y * y + z * z);
			double value = 0.007499999832361937;
			x /= ff;
			y /= ff;
			z /= ff;
			x += rand.nextGaussian() * value * inaccuracy;
			y += rand.nextGaussian() * value * inaccuracy;
			z += rand.nextGaussian() * value * inaccuracy;
			x *= velocity;
			y *= velocity;
			z *= velocity;
			motionX = x;
			motionY = y;
			motionZ = z;
			float prevRotationYaw;
			rotationYaw = (prevRotationYaw = (float) (MathHelper.atan2(x, z) * 180.0 / Math.PI));
			float prevRotationPitch;
			rotationPitch = (prevRotationPitch = (float) (MathHelper.atan2(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / Math.PI));
			boolean b = true;
			int ticksInAir = 0;
			while (b) {
				if (ticksInAir > 300) {
					b = false;
				}
				++ticksInAir;
				Vec3 vec3 = new Vec3(posX, posY, posZ);
				Vec3 vec4 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
				MovingObjectPosition movingobjectposition = mc.theWorld.rayTraceBlocks(vec3, vec4);
				vec3 = new Vec3(posX, posY, posZ);
				vec4 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
				if (movingobjectposition != null) {
					vec4 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
							movingobjectposition.hitVec.zCoord);
				}
				for (final Entity entity : mc.theWorld.loadedEntityList) {
					if (entity != mc.player && entity instanceof EntityLivingBase) {
						final float f2 = 0.3f;
						final AxisAlignedBB localAxisAlignedBB = entity.getEntityBoundingBox().expand(f2, f2, f2);
						final MovingObjectPosition localMovingObjectPosition = localAxisAlignedBB
								.calculateIntercept(vec3, vec4);
						if (localMovingObjectPosition != null) {
							movingobjectposition = localMovingObjectPosition;
							break;
						}
						continue;
					}
				}
				if (movingobjectposition != null) {
					b = false;
				}
				m = movingobjectposition;
				posX += motionX;
				posY += motionY;
				posZ += motionZ;
				final float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
				rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * 180.0 / Math.PI);
				for (rotationPitch = (float) (MathHelper.atan2(motionY, f3) * 180.0 / Math.PI); rotationPitch - prevRotationPitch < -180.0f; prevRotationPitch -= 360.0f) {
				} while (rotationPitch - prevRotationPitch >= 180.0f) {
					prevRotationPitch += 360.0f;
				} while (rotationYaw - prevRotationYaw < -180.0f) {
					prevRotationYaw -= 360.0f;
				} while (rotationYaw - prevRotationYaw >= 180.0f) {
					prevRotationYaw += 360.0f;
				}
				final float f4 = 0.99f;
				float f5 = 0.03f;
				if (itemStack.getItem() instanceof ItemBow) {
					f5 = 0.05f;
				}
				motionX *= f4;
				motionY *= f4;
				motionZ *= f4;
				motionY -= f5;
				positions.add(new Vec3(posX, posY, posZ));
			}
			if (positions.size() > 1) {
				Color color = getTheme().getFirstColor();
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glEnable(2848);
				GL11.glDisable(3553);
				GlStateManager.disableCull();
				GL11.glDepthMask(false);
				GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.7f);
				GL11.glLineWidth((float) 6.0D / 2.0f);
				final Tessellator tessellator = Tessellator.getInstance();
				final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				worldrenderer.begin(3, DefaultVertexFormats.POSITION);
				for (final Vec3 vec5 : positions) {
					worldrenderer.pos((float) vec5.xCoord - mc.getRenderManager().renderPosX, (float) vec5.yCoord - mc.getRenderManager().renderPosY, (float) vec5.zCoord - mc.getRenderManager().renderPosZ).endVertex();
				}
				tessellator.draw();
				if (m != null) {
					GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.3f);
					final Vec3 hitVec = m.hitVec;
					final EnumFacing enumFacing1 = m.sideHit;
					float minX = (float) (hitVec.xCoord - mc.getRenderManager().renderPosX);
					float maxX = (float) (hitVec.xCoord - mc.getRenderManager().renderPosX);
					float minY = (float) (hitVec.yCoord - mc.getRenderManager().renderPosY);
					float maxY = (float) (hitVec.yCoord - mc.getRenderManager().renderPosY);
					float minZ = (float) (hitVec.zCoord - mc.getRenderManager().renderPosZ);
					float maxZ = (float) (hitVec.zCoord - mc.getRenderManager().renderPosZ);
					if (enumFacing1 == EnumFacing.SOUTH) {
						minX -= (float) 0.4;
						maxX += (float) 0.4;
						minY -= (float) 0.4;
						maxY += (float) 0.4;
						maxZ += (float) 0.02;
						minZ += (float) 0.05;
					} else if (enumFacing1 == EnumFacing.NORTH) {
						minX -= (float) 0.4;
						maxX += (float) 0.4;
						minY -= (float) 0.4;
						maxY += (float) 0.4;
						maxZ -= (float) 0.02;
						minZ -= (float) 0.05;
					} else if (enumFacing1 == EnumFacing.EAST) {
						maxX += (float) 0.02;
						minX += (float) 0.05;
						minY -= (float) 0.4;
						maxY += (float) 0.4;
						minZ -= (float) 0.4;
						maxZ += (float) 0.4;
					} else if (enumFacing1 == EnumFacing.WEST) {
						maxX -= (float) 0.02;
						minX -= (float) 0.05;
						minY -= (float) 0.4;
						maxY += (float) 0.4;
						minZ -= (float) 0.4;
						maxZ += (float) 0.4;
					} else if (enumFacing1 == EnumFacing.UP) {
						minX -= (float) 0.4;
						maxX += (float) 0.4;
						maxY += (float) 0.02;
						minY += (float) 0.05;
						minZ -= (float) 0.4;
						maxZ += (float) 0.4;
					} else if (enumFacing1 == EnumFacing.DOWN) {
						minX -= (float) 0.4;
						maxX += (float) 0.4;
						maxY -= (float) 0.02;
						minY -= (float) 0.05;
						minZ -= (float) 0.4;
						maxZ += (float) 0.4;
					}
					worldrenderer.begin(7, DefaultVertexFormats.POSITION);
					worldrenderer.pos(minX, minY, minZ).endVertex();
					worldrenderer.pos(minX, minY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, minZ).endVertex();
					worldrenderer.pos(minX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, maxY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, minZ).endVertex();
					worldrenderer.pos(maxX, maxY, minZ).endVertex();
					worldrenderer.pos(maxX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, minZ).endVertex();
					worldrenderer.pos(minX, minY, minZ).endVertex();
					worldrenderer.pos(minX, maxY, minZ).endVertex();
					worldrenderer.pos(maxX, maxY, minZ).endVertex();
					worldrenderer.pos(minX, minY, minZ).endVertex();
					worldrenderer.pos(minX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, minZ).endVertex();
					worldrenderer.pos(minX, maxY, minZ).endVertex();
					worldrenderer.pos(minX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, maxY, minZ).endVertex();
					worldrenderer.endVertex();
					tessellator.draw();
					GL11.glLineWidth(2.0f);
					worldrenderer.begin(3, DefaultVertexFormats.POSITION);
					worldrenderer.pos(minX, minY, minZ).endVertex();
					worldrenderer.pos(minX, minY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, minZ).endVertex();
					worldrenderer.pos(minX, minY, minZ).endVertex();
					worldrenderer.pos(maxX, minY, minZ).endVertex();
					worldrenderer.pos(maxX, maxY, minZ).endVertex();
					worldrenderer.pos(maxX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, maxZ).endVertex();
					worldrenderer.pos(maxX, minY, minZ).endVertex();
					worldrenderer.pos(maxX, minY, maxZ).endVertex();
					worldrenderer.pos(minX, minY, maxZ).endVertex();
					worldrenderer.pos(minX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, maxY, maxZ).endVertex();
					worldrenderer.pos(maxX, maxY, minZ).endVertex();
					worldrenderer.pos(minX, maxY, minZ).endVertex();
					worldrenderer.endVertex();
					tessellator.draw();
				}
				GL11.glLineWidth(1.0f);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glDepthMask(true);
				GlStateManager.enableCull();
				GL11.glEnable(3553);
				GL11.glEnable(2929);
				GL11.glDisable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glDisable(2848);
			}
		}
	};
}
