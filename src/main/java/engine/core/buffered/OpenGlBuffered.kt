package engine.core.buffered

interface OpenGlBuffered {
    fun genVertexArray()
    fun addBuffers(vararg dataArrays: FloatArray)
}
