package cc.unknown.cosmetics.aura;

import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.api.CosmeticType;
import cc.unknown.util.Accessor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EnchantingAura implements LayerRenderer<AbstractClientPlayer>, Accessor {
	protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("sakura/cosmes/enchantGlint.png");
	private final RenderPlayer renderPlayer;
	private final ModelPlayer playerModel;

	public EnchantingAura(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
        this.playerModel = new ModelPlayer(0.2F, false);
    }

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (CosmeticController.shouldRenderCosmeticForPlayer(entitylivingbaseIn, CosmeticType.ENCHANTING) && entitylivingbaseIn == mc.player && !entitylivingbaseIn.isInvisible()) {
			this.createEnchantGlint(entitylivingbaseIn, renderPlayer.getMainModel(), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}

	private void createEnchantGlint(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
		float f = (float) entitylivingbaseIn.ticksExisted + p_177183_5_;
		this.renderPlayer.bindTexture(ENCHANTED_ITEM_GLINT_RES);

		GlStateManager.enableBlend();
		GlStateManager.depthFunc(514);
		GlStateManager.depthMask(false);
		float f1 = 0.5F;
		GlStateManager.color(f1, f1, f1, 1.0F);

		for (int i = 0; i < 2; ++i) {
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(768, 1);
			float f2 = 0.76F;
			GlStateManager.color(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f3 = 0.33333334F;
			GlStateManager.scale(f3, f3, f3);
			GlStateManager.rotate(30.0F - (float) i * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(0.0F, f * (0.001F + (float) i * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
		}

		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();

	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
