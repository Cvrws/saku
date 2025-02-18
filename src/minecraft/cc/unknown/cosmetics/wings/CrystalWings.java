package cc.unknown.cosmetics.wings;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CrystalWings extends CosmeticBase {
	private CrytsalWingsModel crytsalWingsModel;

	public CrystalWings(RenderPlayer playerRenderer) {
		super(playerRenderer);
		this.crytsalWingsModel = new CrytsalWingsModel(playerRenderer);
	}

	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.crytsalWingsModel.render((Entity) player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
	}

	public class CrytsalWingsModel extends CosmeticModelBase {
		private ModelRenderer model;

		ResourceLocation resourceLocation = new ResourceLocation("sakura/cosmes/crystal.png");

		public CrytsalWingsModel(RenderPlayer player) {
			super(player);
			int i = 30;
			int j = 24;
			this.model = (new ModelRenderer((ModelBase) this)).setTextureSize(i, j).setTextureOffset(0, 8);
			this.model.setRotationPoint(-0.0F, 1.0F, 0.0F);
			this.model.addBox(0.0F, -3.0F, 0.0F, 14, 7, 1);
			this.model.isHidden = true;
			ModelRenderer modelrenderer = (new ModelRenderer((ModelBase) this)).setTextureSize(i, j).setTextureOffset(0, 16);
			modelrenderer.setRotationPoint(-0.0F, 0.0F, 0.2F);
			modelrenderer.addBox(0.0F, -3.0F, 0.0F, 14, 7, 1);
			this.model.addChild(modelrenderer);
			ModelRenderer modelrenderer1 = (new ModelRenderer((ModelBase) this)).setTextureSize(i, j).setTextureOffset(0, 0);
			modelrenderer1.setRotationPoint(-0.0F, 0.0F, 0.2F);
			modelrenderer1.addBox(0.0F, -3.0F, 0.0F, 14, 7, 1);
			modelrenderer.addChild(modelrenderer1);
		}

		@Override
		public void render(Entity entityIn, float limbSwing, float walkingSpeed, float tickValue, float netHeadYaw, float headPitch, float scale) {
		    if (entityIn == mc.player && !entityIn.isInvisible() && CosmeticController.shouldRenderCosmeticForPlayer((AbstractClientPlayer) entityIn, CosmeticType.CRYSTAL)) {
		        float animationOffset = (float) Math.cos(tickValue / 12.0F) / 25.0F - 0.025F - walkingSpeed / 22.0F;

		        ModelRenderer mainPart = (ModelRenderer) this.model.childModels.get(0);
		        ModelRenderer subPart = (ModelRenderer) mainPart.childModels.get(0);

		        this.model.rotateAngleZ = animationOffset * 3.2F;
		        mainPart.rotateAngleZ = animationOffset / 1.8F;
		        subPart.rotateAngleZ = animationOffset / 1.8F;
		        this.model.rotateAngleY = -0.35F - walkingSpeed / 3.2F;
		        this.model.rotateAngleX = 0.3F;

		        GlStateManager.pushMatrix();
		        GlStateManager.scale(1.7D, 1.7D, 1.0D);
		        GlStateManager.translate(0.0D, 0.05D, 0.05D);

		        if (entityIn.isSneaking()) {
		            GlStateManager.translate(0.0D, 0.08D, 0.03D);
		            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
		            this.model.rotateAngleZ = 0.75F;
		            mainPart.rotateAngleZ = 0.0F;
		            subPart.rotateAngleZ = 0.0F;
		        } else {
		            RenderManager renderManager = mc.getRenderManager();
		            if (renderManager != null) {
		                GlStateManager.rotate(renderManager.playerViewX / 3.2F, 1.0F, 0.0F, 0.0F);
		            }
		        }

		        this.model.isHidden = false;

		        for (int i = -1; i <= 1; i += 2) {
		            GlStateManager.pushMatrix();
		            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.35F);
		            GlStateManager.depthMask(false);
		            GlStateManager.enableBlend();
		            GlStateManager.blendFunc(770, 771);
		            GlStateManager.alphaFunc(516, 0.003921569F);
		            GlStateManager.disableLighting();

		            mc.getTextureManager().bindTexture(this.resourceLocation);
		            if (i == 1)
		                GlStateManager.scale(-1.0F, 1.0F, 1.0F);
		            
		            GlStateManager.translate(0.05D, 0.0D, 0.0D);
		            this.model.render(scale);

		            GlStateManager.disableBlend();
		            GlStateManager.alphaFunc(516, 0.1F);
		            GlStateManager.popMatrix();
		            GlStateManager.depthMask(true);
		        }

		        this.model.isHidden = true;
		        GlStateManager.popMatrix();
		    }
		}
	}
}
