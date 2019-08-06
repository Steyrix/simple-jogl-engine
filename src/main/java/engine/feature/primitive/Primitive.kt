package engine.feature.primitive

interface Primitive {
    fun isBufferValidForPrimitive(buffer: FloatArray): Boolean
    fun validateSuppliedData(vararg dataArrays: FloatArray)
}
