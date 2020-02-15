package engine.feature.primitive

interface Primitive {
    fun isBufferValid(buffer: FloatArray): Boolean
    fun validatedData(vararg dataArrays: FloatArray)
}
