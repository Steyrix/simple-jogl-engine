package engine.feature.primitive

interface Primitive {
    fun isBufferValid(buffer: FloatArray): Boolean
    fun validateData(vararg dataArrays: FloatArray)
}
