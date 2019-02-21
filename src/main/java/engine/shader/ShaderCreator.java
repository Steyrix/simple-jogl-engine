package engine.shader;

import com.jogamp.opengl.GL4;

public interface ShaderCreator {
    Shader create(String vertexSource, String fragmentSource, GL4 gl);
    Shader create(String vertexSource, String fragmentSource, String geometrySource, GL4 gl);
}
