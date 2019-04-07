package engine.core;

import org.jetbrains.annotations.NotNull;

public interface OpenGlBuffered {
    void genVertexArray();
    void addBuffers(@NotNull final float[]... dataArrays);
}
