package demo.labrynth

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices

import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.util.texture.Texture
import demo.labrynth.character.LabyrinthCharacter
import demo.labrynth.level.LabyrinthLevelCreator

import engine.core.AnimatedObject
import engine.feature.ResourceLoader
import engine.core.OpenGlObject2D
import engine.core.buffered.Buffered
import engine.feature.primitive.Rectangle
import engine.feature.shader.`interface`.ShaderCreator
import engine.feature.text.TextRenderer
import engine.feature.texture.TextureLoader
import engine.util.geometry.PointF
import engine.core.state.Scene
import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderInteractor
import engine.feature.text.data.TextDataUtils

import java.awt.*
import java.awt.event.*
import java.util.ArrayList

//TODO: load every texture with its own unique id
class GameLabyrinth(
        dim: Dimension,
        private val shaderCreator: ShaderCreator,
        private val shaderInteractor: ShaderInteractor
) : Scene {

    private val presets = LabyrinthPresets()
    private val characterAnimations = presets.characterPresets.animation.animations

    private val boundObjects: ArrayList<OpenGlObject2D> = ArrayList()

    private val textureShaderId = "TEXTURED"
    private val animationShaderId = "ANIMATED"
    private val boundShaderId = "BOUND"
    private val textShaderId = "TEXT_SHADER"
    private val colorShaderId = "COLORED"
    private val boxShaderId = "BOX"

    private var myRenderer: TextRenderer? = null
    private var labyrinthCharacter: LabyrinthCharacter? = null
    private var animationComponent: AnimatedObject? = null
    private var graphicalComponent: OpenGlObject2D? = null

    private var rect: Rectangle? = null
    private var background: OpenGlObject2D? = null
    private val screenWidth: Int = dim.width
    private val screenHeight: Int = dim.height
    private var renderProjection: Mat4? = null

    override fun init(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4

        shaderCreator.attachGl(gl)
        loadShaders()
        initShaders()

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY)
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        val frameSizeX = 0.1f
        val frameSizeY = 0.333f

        animationComponent = AnimatedObject(frameSizeX, frameSizeY, characterAnimations)

        graphicalComponent = OpenGlObject2D(2, 6, gl, 0)
                .apply {
                    val UV = Buffered.getRectangleSectorVertices(frameSizeX, frameSizeY)
                    initRenderData(
                            listOf(ResourceLoader.getAbsolutePath("textures/labyrinth/base_character.png")),
                            false,
                            Buffered.RECTANGLE_INDICES,
                            UV)
                }

        labyrinthCharacter = LabyrinthCharacter(
                posX = 25f,
                posY = 25f,
                width = 50f,
                height = 70f,
                animatedObject = animationComponent!!,
                graphicalObject = graphicalComponent!!)

        initLevelGeography(gl)

        this.renderProjection = Matrices.ortho(
                0.0f,
                screenWidth.toFloat(),
                screenHeight.toFloat(),
                0.0f,
                0.0f,
                1.0f)

        rect = Rectangle(gl, 0).apply {
            init(Color.WHITE)
        }

        val fontAtlasPath = ResourceLoader.getAbsolutePath("textures/simpleFontAtlas.png")

        myRenderer = TextRenderer.getRenderer(
                Dimension(64, 64),
                fontAtlasPath,
                TextDataUtils.symbolSetSimple())
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
        for (o in this.boundObjects) {
            o.dispose()
        }
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT)

        var curr = shaderInteractor.getShader(textureShaderId)

        shaderInteractor.activateShader(textureShaderId)
        background!!.draw(0f, 0f, 1280f, 720f, 0.0f, curr)

        curr = shaderInteractor.getShader(boundShaderId)
        shaderInteractor.activateShader(boundShaderId)

        for (o in boundObjects) {
            o.box?.let {
                o.draw(it.posX, it.posY, it.width, it.height, 0f, curr)
            }
        }

        curr = shaderInteractor.getShader(animationShaderId)
        shaderInteractor.activateShader(animationShaderId)
        shaderInteractor.updateShaders()
        labyrinthCharacter!!.draw(curr)

        curr = shaderInteractor.getShader(boxShaderId)
        shaderInteractor.activateShader(boxShaderId)
        labyrinthCharacter!!.drawBoundingBox(curr)

        curr = shaderInteractor.getShader(textShaderId)
        shaderInteractor.activateShader(textShaderId)
        myRenderer!!.drawText("Sample \n Text", Dimension(50, 50), gl, PointF(600f, 200f), curr)

        // curr = shaderInteractor.getShader(colorShaderId)
        // shaderInteractor.activateShader(colorShaderId)
        // rect!!.draw(600f, 100f, 50f, 100f, 0f, curr)
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) {
        this.renderProjection = Matrices.ortho(
                0.0f,
                screenWidth.toFloat(),
                screenHeight.toFloat(),
                0.0f,
                0.0f,
                1.0f)
    }

    override fun update(deltaTime: Float) {
        boundObjects.forEach {
            labyrinthCharacter?.react(it)
        }

        labyrinthCharacter?.update(deltaTime)
    }

    override fun keyTyped(e: KeyEvent) {
        labyrinthCharacter?.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        labyrinthCharacter?.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        labyrinthCharacter?.keyReleased(e)
    }

    private fun loadShaders() {
        val textureShaderObject = shaderCreator.create(
                "shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl"
        )

        val textShaderObject = shaderCreator.create(
                "shaders/textRenderVertexShader.glsl",
                "shaders/textRenderFragmentShader.glsl"
        )

        val boundShaderObject = shaderCreator.create(
                "shaders/boundVertexShader.glsl",
                "shaders/boundFragmentShader.glsl"
        )

        val animationShaderObject = shaderCreator.create(
                "shaders/animVertexShader.glsl",
                "shaders/animFragmentShader.glsl"
        )

        val colorShaderObject = shaderCreator.create(
                "shaders/coloredVertexShader.glsl",
                "shaders/coloredFragmentShader.glsl"
        )

        val boxShaderObject = shaderCreator.create(
                "shaders/boxVertexShader.glsl",
                "shaders/boxFragmentShader.glsl"
        )

        shaderInteractor.apply {
            addShader(textureShaderId, textureShaderObject)
            addShader(textShaderId, textShaderObject)
            addShader(boundShaderId, boundShaderObject)
            addShader(animationShaderId, animationShaderObject)
            addShader(colorShaderId, colorShaderObject)
            addShader(boxShaderId, boxShaderObject)
        }
    }

    private fun initShaders() {
        val defaultFunc: (Shader) -> Unit = {
            it.setMatrix4f("projection", renderProjection!!, true)
        }

        shaderInteractor.apply {
            forEach {
                shaderInteractor.setShaderActivateFunction(it, defaultFunc)
            }

            setShaderUpdateFunction(animationShaderId) {
                animationComponent!!.defineAnimationVariables(graphicalComponent!!, it)
            }
        }
    }

    private fun initLevelGeography(gl: GL4) {
        val levelCreator = LabyrinthLevelCreator()
        val levelPath = "config/labyrinthlevels/defaultlevel/defaultlevel.ini"
        val levelAbsolutePath = ResourceLoader.getAbsolutePath(levelPath)
        val perimeter = levelCreator.createLevelFromFile(gl, levelAbsolutePath)

        this.boundObjects.addAll(perimeter)

        background = object : OpenGlObject2D(2, 6, gl, 0) {
            public override fun loadTexture(filePath: String) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath)
                    initRepeatableTexParameters(texture!!, gl)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.apply {
            val bgIndices = Buffered.RECTANGLE_INDICES
            val bgUVdata = Buffered.getIndicesScaled(25f, 14f)
            val texturePath = "textures/labyrinth/mossy_bg.png"
            initRenderData(
                    listOf(ResourceLoader.getAbsolutePath(texturePath)),
                    false,
                    bgIndices,
                    bgUVdata)
        }
    }

    companion object {
        internal fun initRepeatableTexParameters(texture: Texture, gl: GL4) =
                texture.apply {
                    setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR)
                    setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR)
                    setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT)
                    setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT)
                }

    }
}
