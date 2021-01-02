package engine.core

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.buffered.Buffered
import engine.core.buffered.OpenGlBuffered
import engine.feature.collision.BoundingBox
import engine.feature.matrix.MatrixInteractor
import engine.feature.shader.Shader
import engine.feature.shader.ShaderVariableKey
import engine.feature.texture.TextureLoader
import java.nio.FloatBuffer
import java.nio.IntBuffer

open class OpenGlObject2D(bufferParamsCount: Int,
                          private val verticesCount: Int,
                          protected val gl: GL4,
                          private val textureId: Int) : OpenGlBuffered, Entity {

    protected var texture: Texture? = null
    protected var textureArray: IntBuffer? = null

    val isTextured: Boolean
        get() = texture != null || textureArray != null

    private val buffersCount: Int = bufferParamsCount
    private val buffers = IntBuffer.allocate(buffersCount)
    private val boxBuffer: IntBuffer = IntBuffer.allocate(1)
    private val paramsCount = mutableListOf<Int>()
    private var buffersFilled: Int = 0

    private val vertexArray = IntBuffer.allocate(1)
    private val boxVertexArray = IntBuffer.allocate(1)

    private val boxVerticesCount = 8

    private var boundingBox: BoundingBox? = null
    private var uniformName: String? = null

    var box: BoundingBox?
        get() = boundingBox
        set(value) {
            boundingBox = value
        }

    open fun initRenderData(textureFilePaths: Array<String>,
                            texArray: Boolean,
                            vararg dataArrays: FloatArray) {
        addBuffers(*dataArrays)
        genVertexArray()

        initBoundingBoxBuffer()
        genBoundingBoxVertexArray()

        if (textureFilePaths.size == 1 && !texArray) {
            loadTexture(textureFilePaths[0])
        }
        if (textureFilePaths.size > 1 || texArray) {
            loadTextureArray(*textureFilePaths)
        }
    }

    open fun initRenderData(itTexture: Texture?, vararg dataArrays: FloatArray) {
        addBuffers(*dataArrays)
        genVertexArray()

        initBoundingBoxBuffer()
        genBoundingBoxVertexArray()

        itTexture?.let {
            texture = it
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
            val bufferSize = (4 * it.size).toLong()

            with(gl) {
                glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(buffersFilled++))
                glBufferData(GL4.GL_ARRAY_BUFFER, bufferSize, floatBuffer, GL4.GL_STATIC_DRAW)
            }

            paramsCount.add(it.size / verticesCount)
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
                if (isTextured) {
                    " Texture id: $textureId"
                } else {
                    " Not textured"
                }
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

            val bbVerticesArray = Buffered.RECTANGLE_VERTICES
            val bbVerticesBuffer = FloatBuffer.wrap(bbVerticesArray)

            val bufferSize = 4 * (bbVerticesArray.size).toLong()

            glBindBuffer(GL4.GL_ARRAY_BUFFER, boxBuffer.get(0))
            glBufferData(GL4.GL_ARRAY_BUFFER, bufferSize, bbVerticesBuffer, GL4.GL_STATIC_DRAW)
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

    private fun setUniformName(newName: String) {
        uniformName = newName
    }

    companion object {
        private const val BUFFER_ILLEGAL_ARG_MSG =
                "Number of buffers supplied must be the number of buffers created for the object"
    }
}
