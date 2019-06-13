package demos.textureArray

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import engine.core.OpenGlObject
import engine.core.state.GameState
import engine.feature.ResourceLoader
import engine.feature.shader.Shader
import engine.feature.shader.ShaderCreator
import engine.feature.primitives.Rectangle
import java.awt.Dimension
import java.awt.event.KeyEvent

class TextureArrayDemo(private val dim: Dimension,
                       private val shaderCreator: ShaderCreator) : GameState {

    private lateinit var textureArrayShader: Shader
    private lateinit var renderProjection: Mat4
    private lateinit var textureArrayObject: OpenGlObject

    override fun init(glAutoDrawable: GLAutoDrawable) {

        val gl = glAutoDrawable.gl.gL4

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY)
        gl.glClearColor(1f, 1f, 1f, 1.0f)

        textureArrayShader = shaderCreator.create("shaders/texArrayVertexShader.glsl",
                "shaders/texArrayFragmentShader.glsl", gl)

        renderProjection = Matrices.ortho(0.0f, dim.width.toFloat(), dim.height.toFloat(),
                0.0f, 0.0f, 1.0f)

        textureArrayObject =
                OpenGlObject(2, 6, gl, 0f, 0f, Dimension(1024,512), 0)

        textureArrayObject.initRenderData(
                arrayOf(
                        ResourceLoader.getAbsolutePath("textures/Idle2.png"),
                        ResourceLoader.getAbsolutePath("textures/Idle3.png")),
                true,
                Rectangle.RECTANGLE_BUFFER, Rectangle.RECTANGLE_REVERSED_BUFFER)
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT)

        textureArrayShader.setMatrix4f("projection", renderProjection, false)
        textureArrayObject.draw(0f, textureArrayShader)
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) {
    }

    override fun update(deltaTime: Float) {
    }

    override fun keyTyped(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
    }

    override fun keyReleased(e: KeyEvent) {
    }
}