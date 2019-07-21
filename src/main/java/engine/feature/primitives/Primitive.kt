package engine.feature.primitives

interface Primitive {
    fun isBufferValidForPrimitive(buffer: FloatArray): Boolean
    fun validateSuppliedData(vararg dataArrays: FloatArray)
}
