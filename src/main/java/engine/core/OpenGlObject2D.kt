package engine.core

import com.hackoeur.jglm.Mat4
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.feature.collision.BoundingBox
import engine.feature.matrix.MatrixInteractor
import engine.feature.shader.Shader
import engine.feature.shader.ShaderVariableKey
import engine.feature.texture.TextureLoader

import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.ArrayList

open class OpenGlObject2D(bufferParamsCount: Int,
                          private val verticesCount: Int,
                          protected val gl: GL4,
                          private val textureId: Int) : OpenGlBuffered, Entity {

    protected var texture: Texture? = null
    protected var textureArray: IntBuffer? = null

    val isTextured: Boolean
        get() = this.texture != null || this.textureArray != null

    private val buffers: IntBuffer
    private val boxBuffer: IntBuffer
    private val paramsCount: ArrayList<Int>
    private var buffersFilled: Int = 0
    private val buffersCount: Int = bufferParamsCount

    private val vertexArray: IntBuffer
    private val boxVertexArray: IntBuffer

    private val boxVerticesCount = 8

    private var boundingBox: BoundingBox? = null
    private var uniformName: String? = null

    var box: BoundingBox?
        get() = boundingBox
        set(value) {
            boundingBox = value
        }

    init {
        this.buffersFilled = 0
        this.buffers = IntBuffer.allocate(buffersCount)
        this.boxBuffer = IntBuffer.allocate(1)
        this.vertexArray = IntBuffer.allocate(1)
        this.boxVertexArray = IntBuffer.allocate(1)
        this.paramsCount = ArrayList()
        this.uniformName = null
        this.texture = null
        this.textureArray = null
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

        val model = MatrixInteractor.getFinalMatrix(x, y, xSize, ySize, rotationAngle)

        defineTextureState(shader)

        shader.setMatrix4f(ShaderVariableKey.Mat.model, model, true)

        with(gl) {
            glBindVertexArray(vertexArray.get(0))
            glDrawArrays(GL.GL_TRIANGLES, 0, verticesCount)
        }
    }

    fun drawBox(shader: Shader) {
        boundingBox?.let {
            shader.setMatrix4f(ShaderVariableKey.Mat.model,
                               MatrixInteractor.getFinalMatrix(it.posX, it.posY, it.width, it.height, 0f),
                     true)

            with(gl) {
                glBindVertexArray(boxVertexArray.get(0))
                glDrawArrays(GL4.GL_LINES, 0, boxVerticesCount)
            }
        }
    }

    fun dispose() {
        with(gl) {
            glDeleteBuffers(buffersCount, buffers)
            glDeleteVertexArrays(1, vertexArray)
        }
        texture?.destroy(gl)
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

        shader.setMatrix4f(ShaderVariableKey.Mat.model, model, true)

        with(gl) {
            glBindVertexArray(vertexArray.get(0))
            glDrawArrays(GL4.GL_TRIANGLES, 0, verticesCount)
        }
    }

    private fun defineTextureState(shader: Shader) {
        if (texture != null) {
            setUniformName(ShaderVariableKey.Uni.textureSample)
            defineSingleTextureState(shader)
        } else if (textureArray != null) {
            setUniformName(ShaderVariableKey.Uni.textureArray)
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
            glGenBuffers(1, boxBuffer)

            val bbVerticesArray = floatArrayOf(0f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 1f, 0f, 0f)
            val bbVerticesBuffer = FloatBuffer.wrap(bbVerticesArray)

            glBindBuffer(GL4.GL_ARRAY_BUFFER, boxBuffer.get(0))
            glBufferData(GL4.GL_ARRAY_BUFFER, 4 * (bbVerticesArray.size).toLong(), bbVerticesBuffer, GL4.GL_STATIC_DRAW)
        }
    }

    private fun genBoundingBoxVertexArray() {
        with(gl) {
            glGenVertexArrays(1, boxVertexArray)
            glBindVertexArray(boxVertexArray.get(0))

            glEnableVertexAttribArray(0)
            glBindBuffer(GL4.GL_ARRAY_BUFFER, boxBuffer.get(0))
            glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 0, 0)
        }
    }

    // TODO figure out if it is really needed
    private fun setUniformName(newName: String) {
        this.uniformName = newName
    }

    companion object {
        private const val BUFFER_ILLEGAL_ARG_MSG = "Number of buffers supplied must be the number of buffers created for the object"
    }
}
