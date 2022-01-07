package demo.map

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import engine.core.AnimatedObject
import engine.core.OpenGlObject2D
import engine.core.state.Scene
import engine.feature.ResourceLoader
import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderCreator
import engine.feature.shader.`interface`.ShaderInteractor
import engine.feature.tiled.TileMap
import engine.feature.tiled.TiledResourceParser
import java.awt.Dimension
import java.awt.event.KeyEvent

// TODO: test out moving controllable object on tile map
// TODO: implement colliding with specific tiles
// TODO: implement tile properties
// TODO: extend tile demo and make it playable
class MapDemo(
        private val dim: Dimension,
        private val shaderCreator: ShaderCreator,
        private val shaderInteractor: ShaderInteractor
) : Scene {

    private var texShader: Shader? = null
    private var renderProjection: Mat4? = null
    private var map: TileMap? = null

    private var character: MapDemoCharacter? = null
    private val animationComponent: AnimatedObject? = null
    private var graphicalComponent: OpenGlObject2D? = null
    private val colliderTilesIds: MutableList<Int> = mutableListOf()
    private val colliderLayersIds: MutableList<Int> = mutableListOf()

    override fun init(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4

        shaderCreator.attachGl(gl)

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY)
        gl.glClearColor(1f, 1f, 1f, 1.0f)

        val mapSource = ResourceLoader.getFileFromAbsolutePath("maps/cave/cave_level.xml")

        map = TiledResourceParser.createTileMapFromXml(mapSource)

        map?.layers?.forEachIndexed { index, it ->
            val collision = it.getProperty("collision")
            if (collision != null && collision.value == true) {
                colliderLayersIds.add(index)
                colliderTilesIds.addAll(it.tiles)
            }
        }

        texShader = shaderCreator.create(
                "shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl"
        )

        renderProjection = Matrices.ortho(
                0.0f,
                dim.width.toFloat(),
                dim.height.toFloat(),
                0.0f,
                0.0f,
                1.0f)
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT)

        texShader?.setMatrix4f("projection", renderProjection!!, false)

        map?.draw(gl, 500f, 500f, texShader!!)
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