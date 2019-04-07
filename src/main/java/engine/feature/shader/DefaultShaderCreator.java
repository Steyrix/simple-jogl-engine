package engine.feature.shader;

import com.jogamp.opengl.GL4;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class DefaultShaderCreator implements ShaderCreator {

    public DefaultShaderCreator() {
    }

    @NotNull
    private String getShaderSource(@NotNull String resourceName) {
        URL fileURL = getClass().getClassLoader().getResource(resourceName);
        String out = null;
        try {
            out = Shader.readFromFile(fileURL.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    @NotNull
    private String[] getShaderBuffer(@NotNull String resourceName) {
        return new String[]{getShaderSource(resourceName)};
    }

    @NotNull
    @Override
    public Shader create(@NotNull String vertexResName, @NotNull String fragmentResName, @NotNull GL4 gl) {
        Shader out = new Shader(gl);
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), null);

        return out;
    }

    @NotNull
    @Override
    public Shader create(@NotNull String vertexResName, @NotNull String fragmentResName, @NotNull String geometryResName, @NotNull GL4 gl) {
        Shader out = new Shader(gl);
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), getShaderBuffer(geometryResName));

        return out;
    }
}
