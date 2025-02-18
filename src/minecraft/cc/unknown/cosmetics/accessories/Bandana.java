package cc.unknown.cosmetics.accessories;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.cosmetics.api.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class Bandana extends CosmeticBase {
    private final ModelBandana modelBandana;

    public Bandana(RenderPlayer renderPlayer) {
        super(renderPlayer);
        modelBandana = new ModelBandana(renderPlayer);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {

        if(CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.BANDANA) && player == mc.player && !player.isInvisible()) {
            GL11.glPushMatrix();
            playerRenderer.bindTexture(new ResourceLocation("sakura/cosmes/bandana.png"));
            float[] color = {255, 255, 255};

            GL11.glColor3f(color[0], color[1], color[2]);
            modelBandana.render(player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
            GL11.glPopMatrix();
        }
    }

    private class ModelBandana extends CosmeticModelBase {

        private final ModelRenderer[] bandanaParts;

        public ModelBandana(RenderPlayer player) {
            super(player);
            this.bandanaParts = new ModelRenderer[4];

            this.bandanaParts[0] = createModelPart(-4.5f, -7.0f, -4.7f, 9, 2, 1);
            this.bandanaParts[1] = createModelPart(3.5f, -7.0f, -3.5f, 1, 2, 8);
            this.bandanaParts[2] = createModelPart(-4.5f, -7.0f, -3.5f, 1, 2, 8);
            this.bandanaParts[3] = createModelPart(-4.5f, -7.0f, 4.0f, 9, 2, 1);
        }

        private ModelRenderer createModelPart(float x, float y, float z, int width, int height, int depth) {
            ModelRenderer part = new ModelRenderer(playerModel, 0, 0);
            part.addBox(x, y, z, width, height, depth);
            return part;
        }

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
            if (entityIn.isSneaking()) {
                GL11.glTranslated(0.0, 0.225, 0.0);
            }

            for (ModelRenderer part : bandanaParts) {
                applyHeadRotation(part);
                part.render(scale);
            }
        }

        private void applyHeadRotation(ModelRenderer part) {
            part.rotateAngleX = playerModel.bipedHead.rotateAngleX;
            part.rotateAngleY = playerModel.bipedHead.rotateAngleY;
            part.rotationPointX = 0.0f;
            part.rotationPointY = 0.0f;
        }
    }

}