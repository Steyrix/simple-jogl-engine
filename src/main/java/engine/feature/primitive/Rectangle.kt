package engine.feature.primitive

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.OpenGlObject2D
import engine.core.buffered.Buffered
import engine.util.color.ColorUtil

import java.awt.*
import java.util.Arrays


class Rectangle(gl: GL4, textureId: Int) :
        OpenGlObject2D(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, textureId), Primitive {

    fun init(textureFilePaths: Array<String>, texArray: Boolean, attribDataArray: FloatArray) {
        if (isBufferValid(attribDataArray)) {
            super.initRenderData(textureFilePaths, texArray, RECTANGLE_BUFFER, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(texture: Texture?, attribDataArray: FloatArray) {
        if (isBufferValid(attribDataArray)) {
            super.initRenderData(texture, RECTANGLE_BUFFER, attribDataArray)
        } else {
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
        }
    }

    fun init(color: Color) = super.initRenderData(null,
                                                         RECTANGLE_BUFFER,
                                                         ColorUtil.getBufferForColor(RECTANGLE_VERTICES_COUNT, color))


    override fun initRenderData(textureFilePaths: Array<String>, texArray: Boolean, vararg dataArrays: FloatArray) {
        validateData(*dataArrays)
        super.initRenderData(textureFilePaths, texArray, *dataArrays)
    }

    override fun initRenderData(itTexture: Texture?, vararg dataArrays: FloatArray) {
        validateData(*dataArrays)
        super.initRenderData(itTexture, *dataArrays)
    }

    override fun validateData(vararg dataArrays: FloatArray) {
        var errMsg = "Rectangle primitive can only have 2 buffers (vertex and attrib)."
        if (dataArrays.size != RECTANGLE_BUFFER_PARAMS_COUNT)
            throw IllegalArgumentException(errMsg)

        errMsg = "Rectangular vertex data should be supplied as the first argument for rectangle primitive. \n" +
                "You can use Rectangle.init() instead"

        if (!RECTANGLE_BUFFER.contentEquals(dataArrays[0]))
            throw IllegalArgumentException(errMsg)

        if (!isBufferValid(dataArrays[1]))
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
    }

    override fun isBufferValid(dataArray: FloatArray) = dataArray.size % RECTANGLE_VERTICES_COUNT == 0

    companion object {
        var RECTANGLE_BUFFER = Buffered.RECTANGLE_INDICES
        var RECTANGLE_REVERSED_BUFFER = RECTANGLE_BUFFER.reversedArray()

        private const val RECTANGLE_BUFFER_PARAMS_COUNT = 2
        private const val RECTANGLE_VERTICES_COUNT = 6

        private const val ERR_NOT_VALID_ATTRIB_BUFFER_MSG = "The buffer supplied is not valid for rectangle"
    }
}
