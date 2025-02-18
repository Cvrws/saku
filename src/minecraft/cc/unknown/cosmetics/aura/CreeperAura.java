package cc.unknown.cosmetics.aura;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class CreeperAura extends CosmeticBase {
	private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("sakura/cosmes/creeper_armor.png");
	private final RenderPlayer renderPlayer;
	private ModelPlayer playerModel;

	public CreeperAura(RenderPlayer renderPlayer) {
		super(renderPlayer);
		this.renderPlayer = renderPlayer;
		this.playerModel = renderPlayer.getMainModel();
	}

	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.CREEPER) && player == mc.player && !player.isInvisible()) {
			boolean flag = player.isInvisible();
			GlStateManager.depthMask(!flag);
			this.playerModel = this.renderPlayer.getMainModel();
			this.renderPlayer.bindTexture(LIGHTNING_TEXTURE);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = (float) player.ticksExisted + partialTicks;
			double speed = 0.01;
			GlStateManager.translate((double) 1 / -100, (double) f * speed, 0.0D);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();

			GlStateManager.disableLighting();
			GlStateManager.blendFunc(1, 1);
			this.playerModel.setModelAttributes(this.renderPlayer.getMainModel());
			this.playerModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(flag);
			GlStateManager.disableLighting();
		}
	}
}
