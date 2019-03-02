package engine.feature.shader;

import com.jogamp.opengl.GL4;

import java.io.IOException;
import java.net.URL;

public class DefaultShaderCreator implements ShaderCreator {

    public DefaultShaderCreator() {
    }

    private String getShaderSource(String resourceName) {
        URL fileURL = getClass().getClassLoader().getResource(resourceName);
        String out = null;
        try {
            out = Shader.readFromFile(fileURL.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    private String[] getShaderBuffer(String resourceName) {
        return new String[]{getShaderSource(resourceName)};
    }


    @Override
    public Shader create(String vertexResName, String fragmentResName, GL4 gl) {
        Shader out = new Shader(gl);
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), null);

        return out;
    }

    @Override
    public Shader create(String vertexResName, String fragmentResName, String geometryResName, GL4 gl) {
        Shader out = new Shader(gl);
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), getShaderBuffer(geometryResName));

        return out;
    }
}
