package cc.unknown.util.render.shader;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class ShaderUtil implements Accessor {

    private final IResourceManager RESOURCE_MANAGER = mc.getResourceManager();

    public int createShader(final String fragmentResource, final String vertexResource) {
        final String fragmentSource = getShaderResource(fragmentResource);
        final String vertexSource = getShaderResource(vertexResource);

        if (fragmentResource == null || vertexResource == null) {
            System.out.println("An error occurred whilst creating shader");
            System.out.println("Fragment: " + fragmentSource == null);
            System.out.println("Vertex: " + vertexSource == null);
            return -1;
        }

        final int fragmentId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        final int vertexId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(fragmentId, fragmentSource);
        GL20.glShaderSource(vertexId, vertexSource);
        GL20.glCompileShader(fragmentId);
        GL20.glCompileShader(vertexId);

        if (!compileShader(fragmentId)) return -1;
        if (!compileShader(vertexId)) return -1;

        final int programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, fragmentId);
        GL20.glAttachShader(programId, vertexId);
        GL20.glValidateProgram(programId);
        GL20.glLinkProgram(programId);
        GL20.glDeleteShader(fragmentId);
        GL20.glDeleteShader(vertexId);

        return programId;
    }

    private boolean compileShader(final int shaderId) {
        final boolean compiled = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;
        if (compiled) return true;

        final String shaderLog = GL20.glGetShaderInfoLog(shaderId, 8192);
        return false;
    }

    public String getShaderResource(final String resource) {
        try {
            final InputStream inputStream = RESOURCE_MANAGER.getResource(new ResourceLocation("sakura/shader/" + resource)).getInputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String source = "";

            try {
                for (String s; (s = bufferedReader.readLine()) != null; source += s + System.lineSeparator()) ;
            } catch (final IOException ignored) {
            }

            return source;
        } catch (final IOException | NullPointerException e) {
            System.out.println("An error occurred while getting a shader resource");
            e.printStackTrace();
            return null;
        }
    }

}
