package engine.feature.primitive

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.OpenGlObject2D
import engine.util.color.ColorUtil
import engine.util.geometry.PointF

import java.awt.*

class Triangle(gl: GL4, boxDim: Dimension, textureId: Int) :
        OpenGlObject2D(TRIANGLE_BUFFER_PARAMS_COUNT, TRIANGLE_VERTICES_COUNT, gl, textureId), Primitive {

    fun init(color: Color, vertices: FloatArray) {
        if (isBufferValid(vertices) && isValidVerticesForTriangle(vertices)) {
            super.initRenderData(null, vertices, ColorUtil.getBufferForColor(TRIANGLE_VERTICES_COUNT, color))
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(textureFilePaths: Array<String>, texArray: Boolean, vertices: FloatArray, attribDataArray: FloatArray) {
        if (isBufferValid(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValid(attribDataArray)) {
            super.initRenderData(textureFilePaths, texArray, vertices, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(texture: Texture?, vertices: FloatArray, attribDataArray: FloatArray) {
        if (isBufferValid(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValid(attribDataArray)) {
            super.initRenderData(texture, vertices, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }

    }

    override fun initRenderData(textureFilePaths: Array<String>, texArray: Boolean, vararg dataArrays: FloatArray) {
        validateData(*dataArrays)
        super.initRenderData(textureFilePaths, texArray, *dataArrays)
    }

    override fun initRenderData(itTexture: Texture?,
                                vararg dataArrays: FloatArray) {
        validateData(*dataArrays)
        super.initRenderData(itTexture, *dataArrays)
    }

    override fun validateData(vararg dataArrays: FloatArray) {

        var errMsg = "Triangle primitive can only have 2 buffers (vertex and attrib)."
        if (dataArrays.size != TRIANGLE_BUFFER_PARAMS_COUNT) {
            throw IllegalArgumentException(errMsg)
        }

        errMsg = "Triangular vertex data should be supplied as the first argument for triangle primitive. \n" + "You can use Rectangle.init() instead"
        if (!isBufferValid(dataArrays[0]) || !isValidVerticesForTriangle(dataArrays[0]))
            throw IllegalArgumentException(errMsg)

        if (isBufferValid(dataArrays[1]))
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
    }

    override fun isBufferValid(buffer: FloatArray): Boolean {
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
