package cc.unknown.cosmetics.pet;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class DogPet extends CosmeticBase {
	private ModelDogPet wolfModel;
	public static final ResourceLocation texture = new ResourceLocation("sakura/cosmes/dogPet.png");

	public DogPet(RenderPlayer player) {
		super(player);
		this.wolfModel = new ModelDogPet(player);
	}

	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.DOG) && player == mc.player && !player.isInvisible()) {
			GlStateManager.pushMatrix();
	
			GlStateManager.translate(-0.7D, 0.0D, 0.0D);
			playerRenderer.bindTexture(texture);
			if (player.isSneaking()) {
				GlStateManager.translate(0.0D, 0.045D, 0.0D);
			}
			this.wolfModel.render((Entity) player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	static class ModelDogPet extends CosmeticModelBase {
		private final ModelRenderer wolfHeadMain;
		private final ModelRenderer wolfBody;
		private final ModelRenderer wolfMane;
		private final ModelRenderer wolfLeg1;
		private final ModelRenderer wolfLeg2;
		private final ModelRenderer wolfLeg3;
		private final ModelRenderer wolfLeg4;
		private final ModelRenderer wolfTail;
		
		private float headRotationAngle;

		public ModelDogPet(RenderPlayer player) {
			super(player);
			float f = 0.0F;
			float f1 = 13.5F;
			this.wolfHeadMain = new ModelRenderer(this, 0, 0);
			this.wolfHeadMain.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, f);
			this.wolfHeadMain.setRotationPoint(-1.0F, f1, -7.0F);
			this.wolfBody = new ModelRenderer(this, 18, 14);
			this.wolfBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f);
			this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.wolfMane = new ModelRenderer(this, 21, 0);
			this.wolfMane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, f);
			this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
			this.wolfLeg1 = new ModelRenderer(this, 0, 18);
			this.wolfLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
			this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.wolfLeg2 = new ModelRenderer(this, 0, 18);
			this.wolfLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
			this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.wolfLeg3 = new ModelRenderer(this, 0, 18);
			this.wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
			this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.wolfLeg4 = new ModelRenderer(this, 0, 18);
			this.wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
			this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.wolfTail = new ModelRenderer(this, 9, 18);
			this.wolfTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
			this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.wolfHeadMain.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, f);
			this.wolfHeadMain.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, f);
			this.wolfHeadMain.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, f);
		}

		@Override
		public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
			super.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
			this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
			this.setLivingAnimations((EntityLivingBase) entityIn, p_78088_2_, p_78088_3_, 0.0F);
			this.wolfHeadMain.renderWithRotation(scale);
			this.wolfBody.render(scale);
			this.wolfLeg1.render(scale);
			this.wolfLeg2.render(scale);
			this.wolfLeg3.render(scale);
			this.wolfLeg4.render(scale);
			this.wolfTail.renderWithRotation(scale);
			this.wolfMane.render(scale);
		}

	    @Override
	    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
	    	if (entitylivingbaseIn.isSneaking()) {
	            this.wolfMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
	            this.wolfMane.rotateAngleX = ((float) Math.PI * 2F / 5F);
	            this.wolfMane.rotateAngleY = 0.0F;
	            this.wolfBody.setRotationPoint(0.0F, 18.0F, 0.0F);
	            this.wolfBody.rotateAngleX = ((float) Math.PI / 4F);
	            this.wolfTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
	            this.wolfLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
	            this.wolfLeg1.rotateAngleX = ((float) Math.PI * 3F / 2F);
	            this.wolfLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
	            this.wolfLeg2.rotateAngleX = ((float) Math.PI * 3F / 2F);
	            this.wolfLeg3.rotateAngleX = 5.811947F;
	            this.wolfLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
	            this.wolfLeg4.rotateAngleX = 5.811947F;
	            this.wolfLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
	        } else {
	            this.headRotationAngle = (float) Math.sin(entitylivingbaseIn.ticksExisted / 10.0) * 0.2F;

	            this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
	            this.wolfBody.rotateAngleX = ((float) Math.PI / 2F);
	            this.wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
	            this.wolfMane.rotateAngleX = this.wolfBody.rotateAngleX;
	            this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
	            this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
	            this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
	            this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
	            this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);

	            this.wolfLeg1.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
	            this.wolfLeg2.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
	            this.wolfLeg3.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
	            this.wolfLeg4.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
	        }
	    }

		@Override
		public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
			super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
	        this.wolfHeadMain.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI) + headRotationAngle;
	        this.wolfHeadMain.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
			this.wolfTail.rotateAngleX = p_78087_3_;
		}
	}
}