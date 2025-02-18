package cc.unknown.cosmetics.hat;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class DougDimmadome extends CosmeticBase {
	
	private final ModelDougDimmadomeHat modelDougDimmadomeHat;
	
	public DougDimmadome(RenderPlayer renderPlayer) {
		super(renderPlayer);
		modelDougDimmadomeHat = new ModelDougDimmadomeHat(renderPlayer);
	}
	
	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.DIMMADOME) && player == mc.player && !player.isInvisible()) {
			
			GlStateManager.pushMatrix();
			playerRenderer.bindTexture(new ResourceLocation("sakura/cosmes/DougDimmadomeHat.png"));
			
			if (player.isSneaking()) {
				GL11.glTranslated(0D, 0.225D, 0D);
			}
			
			float[] color = CosmeticController.getTophatColor(player);
			modelDougDimmadomeHat.render(player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
			GlStateManager.popMatrix();	
		}
	}
	
	private class ModelDougDimmadomeHat extends CosmeticModelBase{
		
		private ModelRenderer rim;
		private ModelRenderer hatBody;
		
		public ModelDougDimmadomeHat(RenderPlayer player) {
			super(player);
			
			rim = new ModelRenderer(playerModel, 0, 0);
			rim.addBox(-5.5f, -9f, -5.5f, 11, 2, 11);
			
			hatBody = new ModelRenderer(playerModel, 0, 13);
			hatBody.addBox(-3.5f, -52f, -3.5f, 7, (int) 44.4f, 7);
			
		}
		
		@Override
		public void render(Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
			rim.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			rim.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			rim.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			rim.rotationPointX = 0.0f;
			rim.rotationPointY = 0.0f;
			rim.render(scale);
			
			hatBody.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			hatBody.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			hatBody.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			hatBody.rotationPointX = 0.0f;
			hatBody.rotationPointY = 0.0f;
			hatBody.render(scale);
			
		}
		
	}
}