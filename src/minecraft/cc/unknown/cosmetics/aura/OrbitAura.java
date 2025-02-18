package cc.unknown.cosmetics.aura;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class OrbitAura extends CosmeticBase {
    private final ModelSkullAura skullModel;
    public static final ResourceLocation texture = new ResourceLocation("textures/blocks/obsidian.png");

    public OrbitAura(RenderPlayer player) {
        super(player);
        this.skullModel = new ModelSkullAura(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.ORBIT) && player == mc.player && !player.isInvisible()) {
            GlStateManager.pushMatrix();
            playerRenderer.bindTexture(texture);
            
            float time = player.ticksExisted + partialTicks;
            int skullCount = 3;
            for (int i = 0; i < skullCount; i++) {
                GlStateManager.pushMatrix();
                
                double angle = ((double) i / skullCount) * 2.0 * Math.PI;
                double xOffset = Math.cos(time * 0.05 + angle) * 0.8;
                double zOffset = Math.sin(time * 0.05 + angle) * 0.8;
                double yOffset = Math.sin(time * 0.1) * 0.2;
                
                GlStateManager.translate(xOffset, 0.5 + yOffset, zOffset);
                GlStateManager.rotate((float) Math.toDegrees(time * 0.3 + angle), 0, 1, 0);
                
                this.skullModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
                
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
    }

    static class ModelSkullAura extends CosmeticModelBase {
        private final ModelRenderer skull;

        public ModelSkullAura(RenderPlayer player) {
            super(player);
            this.skull = new ModelRenderer(this, 0, 0);
            this.skull.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
        }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
            this.skull.render(scale);
        }
    }
}
