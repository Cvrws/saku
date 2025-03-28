package cc.unknown.util.render.shader;

import java.awt.Color;

import net.minecraft.client.renderer.GlStateManager;

public class RQShader {

    private final ShaderProgram program = new ShaderProgram("rq.frag", "vertex.vsh");

    public void draw(final float x, final float y, final float width, final float height, final float radius, final Color color, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        final int programId = this.program.getProgramId();
        this.program.start();

        ShaderUniforms.uniform2f(programId, "u_size", width,  height);
        ShaderUniforms.uniform1f(programId, "u_radius", radius );
        ShaderUniforms.uniform4f(programId, "u_color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(programId, "u_edges", leftTop ? 1.0F : 0.0F, rightTop ? 1.0F : 0.0F, rightBottom ? 1.0F : 0.0F, leftBottom ? 1.0F : 0.0F);

        GlStateManager.enableBlend();
        ShaderProgram.drawQuad(x, y, width, height);
        GlStateManager.disableBlend();
        ShaderProgram.stop();
    }

    public void draw(final double x, final double y, final double width, final double height, final double radius, final Color color, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        draw((float) x, (float) y, (float) width, (float) height, (float) radius, color, leftTop, rightTop, rightBottom, leftBottom);
    }

    public void draw(final double x, final double y, final double width, final double height, final double radius, final Color color) {
        draw((float) x, (float) y, (float) width, (float) height, (float) radius, color, true, true, true, true);
    }
}