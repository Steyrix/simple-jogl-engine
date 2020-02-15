package engine.feature.primitive

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.OpenGlObject2D
import engine.util.color.ColorUtil

import java.awt.*
import java.util.Arrays


class Rectangle : OpenGlObject2D, Primitive {

    constructor(gl: GL4, width: Float, height: Float, textureId: Int) :
            super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, Dimension(width.toInt(), height.toInt()), textureId)

    constructor(gl: GL4, posX: Float, posY: Float, width: Float, height: Float, textureId: Int) :
            super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, posX, posY, Dimension(width.toInt(), height.toInt()), textureId)

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

    fun init(color: Color) {
        super.initRenderData(null, RECTANGLE_BUFFER, ColorUtil.getBufferForColor(RECTANGLE_VERTICES_COUNT, color))
    }

    override fun initRenderData(textureFilePaths: Array<String>, texArray: Boolean, vararg dataArrays: FloatArray) {
        validatedData(*dataArrays)
        super.initRenderData(textureFilePaths, texArray, *dataArrays)
    }

    override fun initRenderData(texture: Texture?, vararg dataArrays: FloatArray) {
        validatedData(*dataArrays)
        super.initRenderData(texture, *dataArrays)
    }

    override fun validatedData(vararg dataArrays: FloatArray) {
        var errMsg = "Rectangle primitive can only have 2 buffers (vertex and attrib)."
        if (dataArrays.size != RECTANGLE_BUFFER_PARAMS_COUNT)
            throw IllegalArgumentException(errMsg)

        errMsg = "Rectangular vertex data should be supplied as the first argument for rectangle primitive. \n" + "You can use Rectangle.init() instead"
        if (!Arrays.equals(RECTANGLE_BUFFER, dataArrays[0]))
            throw IllegalArgumentException(errMsg)

        if (!isBufferValid(dataArrays[1]))
            throw IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER_MSG)
    }

    override fun isBufferValid(dataArray: FloatArray): Boolean {
        return dataArray.size % RECTANGLE_VERTICES_COUNT == 0
    }

    companion object {
        var RECTANGLE_BUFFER = floatArrayOf(0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)
        var RECTANGLE_REVERSED_BUFFER = floatArrayOf(0f, 0f, 1f, 1f, 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f)

        private const val RECTANGLE_BUFFER_PARAMS_COUNT = 2
        private const val RECTANGLE_VERTICES_COUNT = 6

        private const val ERR_NOT_VALID_ATTRIB_BUFFER_MSG = "The buffer supplied is not valid for rectangle"
    }
}
