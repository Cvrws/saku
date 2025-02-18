package cc.unknown.cosmetics.hat;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class WitchHat extends CosmeticBase {
	private final HatModel hatModel;

	private static final ResourceLocation TEXTURE = new ResourceLocation("sakura/cosmes/witch.png");

	public WitchHat(RenderPlayer renderPlayer) {
		super(renderPlayer);
		this.hatModel = new HatModel(renderPlayer);
	}

	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.WITCH) && player == mc.player && !player.isInvisible()) {
			GL11.glPushMatrix();

			if (player.isSneaking()) {
				GlStateManager.translate(0, 0.262, 0);
			}
			GlStateManager.rotate(netHeadYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(headPitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(-0.3125, -0.55, -0.3125);
			this.playerRenderer.bindTexture(TEXTURE);
			this.hatModel.render((Entity) player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
			GL11.glColor3d(1, 1, 1);

			float[] color = CosmeticController.getTophatColor(player);
			GL11.glColor3f(color[0], color[1], color[2]);
			hatModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GL11.glColor3f(1, 1, 1);
			GlStateManager.popMatrix();
		}

	}
}

class HatModel extends CosmeticModelBase {

	private ModelRenderer witchHat;

	public HatModel(RenderPlayer player) {
		super(player);

		this.witchHat = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
		this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
		ModelRenderer modelrenderer = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
		modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelrenderer.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
		modelrenderer.rotateAngleX = -0.05235988F;
		modelrenderer.rotateAngleZ = 0.02617994F;
		this.witchHat.addChild(modelrenderer);
		ModelRenderer modelrenderer1 = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
		modelrenderer1.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelrenderer1.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
		modelrenderer1.rotateAngleX = -0.10471976F;
		modelrenderer1.rotateAngleZ = 0.05235988F;
		modelrenderer.addChild(modelrenderer1);
		ModelRenderer modelrenderer2 = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
		modelrenderer2.setRotationPoint(1.75F, -2.0F, 2.0F);
		modelrenderer2.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
		modelrenderer2.rotateAngleX = -0.20943952F;
		modelrenderer2.rotateAngleZ = 0.10471976F;
		modelrenderer1.addChild(modelrenderer2);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw,
			float headPitch, float scale) {

		final ResourceLocation TEXTURE = new ResourceLocation("sakura/cosmes/witch.png");
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		this.witchHat.render(scale);

	}
}