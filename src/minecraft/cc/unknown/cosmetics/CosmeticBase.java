package cc.unknown.cosmetics;

import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public abstract class CosmeticBase implements LayerRenderer<AbstractClientPlayer>, Accessor {

	protected final RenderPlayer playerRenderer;

	public CosmeticBase(RenderPlayer playerRenderer) {
		this.playerRenderer = playerRenderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		if (player.hasPlayerInfo() && !player.isInvisible() && player == Minecraft.getInstance().player) {
			render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch, scale);
		}
	}

	public abstract void render(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float HeadPitch, float scale);

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}