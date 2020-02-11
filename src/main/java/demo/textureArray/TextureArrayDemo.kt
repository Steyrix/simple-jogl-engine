package demo.textureArray

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import engine.core.OpenGlObject2D
import engine.core.state.GameState
import engine.feature.ResourceLoader
import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderCreator
import engine.feature.primitive.Rectangle
import engine.feature.shader.ShaderVariableKey
import engine.feature.texture.TextureLoader
import java.awt.Dimension
import java.awt.event.KeyEvent

class TextureArrayDemo(private val dim: Dimension,
                       private val shaderCreator: ShaderCreator) : GameState {

    private lateinit var textureArrayShader: Shader
    private lateinit var textureShader: Shader
    private lateinit var renderProjection: Mat4
    private lateinit var textureArrayObject: OpenGlObject2D
    private lateinit var texturedObject: OpenGlObject2D

    override fun init(glAutoDrawable: GLAutoDrawable) {

        val gl = glAutoDrawable.gl.gL4

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY)
        gl.glClearColor(1f, 1f, 1f, 1.0f)

        textureArrayShader = shaderCreator.create("shaders/texArrayVertexShader.glsl",
                "shaders/texArrayFragmentShader.glsl", gl)

        textureShader = shaderCreator.create("shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl", gl)

        renderProjection = Matrices.ortho(0.0f, dim.width.toFloat(), dim.height.toFloat(),
                0.0f, 0.0f, 1.0f)

        // Define an object that uses array texture as render data
        textureArrayObject =
                OpenGlObject2D(2, 6, gl, 0f, 0f, Dimension(512,256), 0)

        textureArrayObject.initRenderData(
                arrayOf(ResourceLoader.getAbsolutePath("textures/idle2.png")),
                true,
                Rectangle.RECTANGLE_BUFFER, Rectangle.RECTANGLE_REVERSED_BUFFER)

        // Define an object that uses single texture as render data for comparison
        texturedObject =
                OpenGlObject2D(2, 6, gl, 512f, 0f, Dimension(512,256), 0)

        texturedObject.initRenderData(
                TextureLoader.loadTexture(ResourceLoader.getAbsolutePath("textures/idle2.png")),
                Rectangle.RECTANGLE_BUFFER, Rectangle.RECTANGLE_REVERSED_BUFFER)
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) = Unit

    override fun display(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT)

        textureArrayShader.setMatrix4f(ShaderVariableKey.Mat.projection, renderProjection, false)
        textureArrayObject.draw(0f, textureArrayShader)

        textureShader.setMatrix4f(ShaderVariableKey.Mat.projection, renderProjection, false)
        texturedObject.draw(0f, textureShader)
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) = Unit

    override fun update(deltaTime: Float) = Unit

    override fun keyTyped(e: KeyEvent) = Unit

    override fun keyPressed(e: KeyEvent) = Unit

    override fun keyReleased(e: KeyEvent) = Unit
}