package demos.map

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.jogamp.opengl.GLAutoDrawable
import engine.core.state.GameState
import engine.feature.ResourceLoader
import engine.feature.shader.DefaultShaderCreator
import engine.feature.shader.Shader
import engine.feature.tilemap.TileMap
import java.awt.Dimension
import java.awt.event.KeyEvent

class MapDemo(private val dim: Dimension) : GameState {

    private var texShader: Shader? = null
    private var renderProjection: Mat4? = null
    private var map: TileMap? = null

    override fun init(glAutoDrawable: GLAutoDrawable) {

        val gl = glAutoDrawable.gl.gL4
        val shaderCreator = DefaultShaderCreator()

        map = TileMap.createTileMap(ResourceLoader.getFileFromAbsolutePath("maps/map2.xml"))

        texShader = shaderCreator.create("shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl", gl)

        renderProjection = Matrices.ortho(0.0f, dim.width.toFloat(), dim.height.toFloat(),
                0.0f, 0.0f, 1.0f)
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4

        texShader?.setMatrix4f("projection", renderProjection, false)

        map?.draw(gl, texShader!!)
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