package engine.feature.primitives;

import org.jetbrains.annotations.NotNull;

public interface Primitive {

    boolean isBufferValidForPrimitive(@NotNull final float[] buffer);

    void validateSuppliedData(@NotNull final float[]... dataArrays);
}
