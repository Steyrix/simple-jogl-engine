package engine.feature.primitive

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.OpenGlObject2D
import engine.util.color.ColorUtil
import engine.util.geometry.PointF

import java.awt.*

class Triangle : OpenGlObject2D, Primitive {

    constructor(gl: GL4, boxDim: Dimension, textureId: Int) :
            super(TRIANGLE_BUFFER_PARAMS_COUNT, TRIANGLE_VERTICES_COUNT, gl, boxDim, textureId)

    constructor(gl: GL4, posX: Float, posY: Float, boxDim: Dimension, textureId: Int) :
            super(TRIANGLE_BUFFER_PARAMS_COUNT, TRIANGLE_VERTICES_COUNT, gl, posX, posY, boxDim, textureId)

    fun init(color: Color, vertices: FloatArray) {
        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)) {
            super.initRenderData(null, vertices, ColorUtil.getBufferForColor(TRIANGLE_VERTICES_COUNT, color))
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(textureFilePaths: Array<String>, texArray: Boolean, vertices: FloatArray, attribDataArray: FloatArray) {
        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValidForPrimitive(attribDataArray)) {
            super.initRenderData(textureFilePaths, texArray, vertices, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(texture: Texture?, vertices: FloatArray, attribDataArray: FloatArray) {
        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValidForPrimitive(attribDataArray)) {
            super.initRenderData(texture, vertices, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }

    }

    override fun initRenderData(textureFilePaths: Array<String>, texArray: Boolean, vararg dataArrays: FloatArray) {
        validateSuppliedData(*dataArrays)
        super.initRenderData(textureFilePaths, texArray, *dataArrays)
    }

    override fun initRenderData(texture: Texture?,
                                vararg dataArrays: FloatArray) {
        validateSuppliedData(*dataArrays)
        super.initRenderData(texture, *dataArrays)
    }

    override fun validateSuppliedData(vararg dataArrays: FloatArray) {

        var errMsg = "Triangle primitive can only have 2 buffers (vertex and attrib)."
        if (dataArrays.size != TRIANGLE_BUFFER_PARAMS_COUNT) {
            throw IllegalArgumentException(errMsg)
        }

        errMsg = "Triangular vertex data should be supplied as the first argument for triangle primitive. \n" + "You can use Rectangle.init() instead"
        if (!isBufferValidForPrimitive(dataArrays[0]) || !isValidVerticesForTriangle(dataArrays[0]))
            throw IllegalArgumentException(errMsg)

        if (isBufferValidForPrimitive(dataArrays[1]))
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
    }

    override fun isBufferValidForPrimitive(buffer: FloatArray): Boolean {
        return buffer.size % TRIANGLE_BUFFER_LENGTH == 0
    }

    companion object {
        private const val TRIANGLE_BUFFER_PARAMS_COUNT = 2
        private const val TRIANGLE_VERTICES_COUNT = 3
        private const val TRIANGLE_BUFFER_LENGTH = 6

        private const val ERR_NOT_VALID_ATTRIB_BUFFER_MSG = "The buffer supplied is not valid for triangle"

        private fun isValidVerticesForTriangle(buffer: FloatArray): Boolean {
            val a = PointF(buffer[0], buffer[1])
            val b = PointF(buffer[2], buffer[3])
            val c = PointF(buffer[4], buffer[5])

            return a != b && a != c && b != c
        }
    }
}
