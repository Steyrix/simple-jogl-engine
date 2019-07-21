package engine.core

interface OpenGlBuffered {
    fun genVertexArray()
    fun addBuffers(vararg dataArrays: FloatArray)
}
