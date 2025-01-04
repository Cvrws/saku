package cc.unknown.util.render.shader;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShaderUniforms {

    public void uniformFB(final int programId, final String name, final FloatBuffer floatBuffer) {
        GL20.glUniform1(getLocation(programId, name), floatBuffer);
    }

    public void uniform1i(final int programId, final String name, final int i) {
        GL20.glUniform1i(getLocation(programId, name), i);
    }

    public void uniform2i(final int programId, final String name, final int i, final int j) {
        GL20.glUniform2i(getLocation(programId, name), i, j);
    }

    public void uniform1f(final int programId, final String name, final float f) {
        GL20.glUniform1f(getLocation(programId, name), f);
    }

    public void uniform2f(final int programId, final String name, final float f, final float g) {
        GL20.glUniform2f(getLocation(programId, name), f, g);
    }

    public void uniform3f(final int programId, final String name, final float f, final float g, final float h) {
        GL20.glUniform3f(getLocation(programId, name), f, g, h);
    }

    public void uniform4f(final int programId, final String name, final float f, final float g, final float h, final float i) {
        GL20.glUniform4f(getLocation(programId, name), f, g, h, i);
    }

    private int getLocation(final int programId, final String name) {
        return GL20.glGetUniformLocation(programId, name);
    }
}
