package engine.feature.shader;

import com.jogamp.opengl.GL4;
import org.jetbrains.annotations.NotNull;

public interface ShaderCreator {

    @NotNull
    Shader create(@NotNull String vertexSource, @NotNull String fragmentSource, @NotNull GL4 gl);

    @NotNull
    Shader create(@NotNull String vertexSource, @NotNull String fragmentSource, @NotNull String geometrySource, @NotNull GL4 gl);
}
