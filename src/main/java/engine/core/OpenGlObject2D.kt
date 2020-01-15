package engine.core

import com.hackoeur.jglm.Mat4
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.feature.collision.BoundingBox
import engine.feature.matrix.MatrixInteractor
import engine.feature.shader.Shader
import engine.feature.texture.TextureLoader

import java.awt.*
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.ArrayList

open class OpenGlObject2D : BoundingBox, OpenGlBuffered {

    protected val gl: GL4
    protected var texture: Texture? = null
    protected var textureArray: IntBuffer? = null

    private val buffers: IntBuffer
    private val bbBuffer: IntBuffer
    private val paramsCount: ArrayList<Int>
    private var buffersFilled: Int = 0
    private val buffersCount: Int

    private val verticesCount: Int
    private val vertexArray: IntBuffer
    private val bbVertexArray: IntBuffer

    private val textureId: Int
    private var uniformName: String? = null

    private val isTextured: Boolean
        get() = this.texture != null || this.textureArray != null

    private val boxVerticesCount = 8

    constructor(bufferParamsCount: Int,
                verticesCount: Int,
                gl: GL4,
                boxDim: Dimension,
                textureId: Int) : super(0.0f, 0.0f, boxDim.width.toFloat(), boxDim.height.toFloat()) {
        this.gl = gl

        this.buffersFilled = 0
        this.buffersCount = bufferParamsCount
        this.verticesCount = verticesCount
        this.buffers = IntBuffer.allocate(buffersCount)
        this.bbBuffer = IntBuffer.allocate(1)
        this.vertexArray = IntBuffer.allocate(1)
        this.bbVertexArray = IntBuffer.allocate(1)

        this.paramsCount = ArrayList()

        this.uniformName = null
        this.texture = null
        this.textureArray = null
        this.textureId = textureId
    }

    constructor(bufferParamsCount: Int,
                verticesCount: Int,
                gl: GL4,
                posX: Float,
                posY: Float,
                boxDim: Dimension,
                textureId: Int) : super(posX, posY, boxDim.width.toFloat(), boxDim.height.toFloat()) {
        this.gl = gl

        this.buffersFilled = 0
        this.buffersCount = bufferParamsCount
        this.verticesCount = verticesCount
        this.buffers = IntBuffer.allocate(buffersCount)
        this.bbBuffer = IntBuffer.allocate(1)
        this.vertexArray = IntBuffer.allocate(1)
        this.bbVertexArray = IntBuffer.allocate(1)

        this.paramsCount = ArrayList()

        this.posX = posX
        this.posY = posY

        this.uniformName = null
        this.texture = null
        this.textureArray = null
        this.textureId = textureId
    }

    fun dispose() {
        with(gl) {
            glDeleteBuffers(buffersCount, buffers)
            glDeleteVertexArrays(1, vertexArray)
        }
        texture?.destroy(gl)
    }

    open fun initRenderData(textureFilePaths: Array<String>,
                            texArray: Boolean,
                            vararg dataArrays: FloatArray) {
        addBuffers(*dataArrays)
        genVertexArray()

        initBoundingBoxBuffer()
        genBoundingBoxVertexArray()

        if (textureFilePaths.size == 1 && !texArray) {
            println("Loading only single texture")
            loadTexture(textureFilePaths[0])
        }
        if (textureFilePaths.size > 1 || texArray) {
            println("Loading texture array")
            loadTextureArray(*textureFilePaths)
        }
    }

    open fun initRenderData(texture: Texture?, vararg dataArrays: FloatArray) {
        addBuffers(*dataArrays)
        genVertexArray()

        initBoundingBoxBuffer()
        genBoundingBoxVertexArray()

        texture?.let {
            this.texture = it
            setTexParameters()
        }
    }

    open fun draw(x: Float, y: Float, xSize: Float, ySize: Float, rotationAngle: Float, shader: Shader) {
        shader.use()

        this.width = xSize
        this.height = ySize

        //val model = getFinalMatrix(x, y, xSize, ySize, rotationAngle)
        val model = MatrixInteractor.getFinalMatrix(x, y, xSize, ySize, rotationAngle)

        defineTextureState(shader)

        shader.setMatrix4f("model", model, true)

        with(gl) {
            glBindVertexArray(vertexArray.get(0))
            glDrawArrays(GL.GL_TRIANGLES, 0, verticesCount)
        }
    }

    open fun draw(xSize: Float, ySize: Float, rotationAngle: Float, shader: Shader) {
        this.width = xSize
        this.height = ySize

        //val model = getFinalMatrix(xSize, ySize, rotationAngle)
        val model = MatrixInteractor.getFinalMatrix(this.posX, this.posY, xSize, ySize, rotationAngle)

        doDraw(shader, model)
    }

    open fun draw(rotationAngle: Float, shader: Shader) {
        //val model = getFinalMatrix(this.width, this.height, rotationAngle)
        val model = MatrixInteractor.getFinalMatrix(this.posX, this.posY, this.width, this.height, rotationAngle)

        doDraw(shader, model)
    }

    open fun update(deltaTime: Float) {}

    fun drawBox(shader: Shader) {
        shader.setMatrix4f("model", MatrixInteractor.getFinalMatrix(posX, posY, width, height, 0f), true)

        with(gl) {
            glBindVertexArray(bbVertexArray.get(0))
            glDrawArrays(GL4.GL_LINES, 0, boxVerticesCount)
        }
    }

    override fun addBuffers(vararg dataArrays: FloatArray) {
        require(dataArrays.size == buffersCount) { BUFFER_ILLEGAL_ARG_MSG }

        gl.glGenBuffers(buffersCount, buffers)

        dataArrays.forEach {
            val floatBuffer = FloatBuffer.wrap(it)

            with(gl) {
                glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(buffersFilled++))
                glBufferData(GL4.GL_ARRAY_BUFFER, (4 * it.size).toLong(), floatBuffer, GL4.GL_STATIC_DRAW)
            }

            paramsCount.add(it.size / this.verticesCount)
        }
    }

    override fun genVertexArray() {
        with(gl) {
            glGenVertexArrays(1, vertexArray)
            glBindVertexArray(vertexArray.get(0))

            for (i in 0 until buffersFilled) {
                glEnableVertexAttribArray(i)
                glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(i))
                glVertexAttribPointer(i, paramsCount[i], GL4.GL_FLOAT, false, 0, 0)
            }
        }
    }

    override fun toString(): String {
        return "OpenGlObject: \n Number of vertices: $verticesCount" +
                "\n Number of buffers: $buffersCount \n" +
                if (isTextured) " Texture id: $textureId" else " Not textured"
    }

    protected open fun loadTexture(filePath: String) {
        try {
            texture = TextureLoader.loadTexture(filePath)
            setTexParameters()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setTexParameters() =
        with(texture) {
            this?.let {
                setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR)
                setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR)
                setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE)
                setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE)
            }
        }


    private fun loadTextureArray(vararg filePaths: String) {
        textureArray = TextureLoader.loadTextureArray(gl, textureId, *filePaths)
    }

    private fun doDraw(shader: Shader, model: Mat4) {
        defineTextureState(shader)

        shader.setMatrix4f("model", model, true)

        with(gl) {
            glBindVertexArray(vertexArray.get(0))
            glDrawArrays(GL4.GL_TRIANGLES, 0, verticesCount)
        }
    }

    private fun defineTextureState(shader: Shader) {
        if (this.texture != null) {
            this.setUniformName("textureSample")
            defineSingleTextureState(shader)
        } else if (this.textureArray != null) {
            this.setUniformName("textureArray")
            defineArrayTextureState(shader)
        }
    }

    private fun defineSingleTextureState(shader: Shader) {
        with(gl) {
            glActiveTexture(GL4.GL_TEXTURE0)

            texture?.let {
                it.enable(this)
                it.bind(this)
            }

            glUniform1i(this.glGetUniformLocation(shader.id, uniformName), 0)
        }
    }

    private fun defineArrayTextureState(shader: Shader) {
        with(gl) {
            glActiveTexture(GL4.GL_TEXTURE0)
            glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, textureArray!!.get(0))
            glUniform1i(this.glGetUniformLocation(shader.id, uniformName), 0)
        }
    }

    private fun initBoundingBoxBuffer() {
        with(gl) {
            glGenBuffers(1, bbBuffer)

            val bbVerticesArray = floatArrayOf(0f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 1f, 0f, 0f)
            val bbVerticesBuffer = FloatBuffer.wrap(bbVerticesArray)

            glBindBuffer(GL4.GL_ARRAY_BUFFER, bbBuffer.get(0))
            glBufferData(GL4.GL_ARRAY_BUFFER, 4 * (bbVerticesArray.size).toLong(), bbVerticesBuffer, GL4.GL_STATIC_DRAW)
        }
    }

    private fun genBoundingBoxVertexArray() {
        with(gl) {
            glGenVertexArrays(1, bbVertexArray)
            glBindVertexArray(bbVertexArray.get(0))

            glEnableVertexAttribArray(0)
            glBindBuffer(GL4.GL_ARRAY_BUFFER, bbBuffer.get(0))
            glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 0, 0)
        }
    }

    private fun setUniformName(newName: String) {
        this.uniformName = newName
    }

    companion object {
        private const val BUFFER_ILLEGAL_ARG_MSG = "Number of buffers supplied must be the number of buffers created for the object"
    }
}
