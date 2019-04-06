package engine.feature.primitives;

public interface Primitive {

    boolean isBufferValidForPrimitive(final float[] buffer);

    void validateSuppliedData(final float[]... dataArrays);
}
