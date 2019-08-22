package demo.labrynth

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.util.texture.Texture
import engine.feature.ResourceLoader
import engine.feature.animation.BasicAnimation
import engine.core.ControllableObject
import engine.core.OpenGlObject2D
import engine.feature.primitive.Rectangle
import engine.feature.shader.`interface`.ShaderCreator
import engine.feature.text.TextRenderer
import engine.feature.texture.TextureLoader
import engine.util.geometry.PointF
import engine.core.state.GameState
import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderInteractor

import java.awt.*
import java.awt.event.*
import java.util.ArrayList

//TODO: load every texture with its own unique id
class GameLabyrinth(dim: Dimension,
                    private val shaderCreator: ShaderCreator,
                    private val shaderInteractor: ShaderInteractor) : GameState {

    private val controls: ArrayList<ControllableObject> = ArrayList()
    private val boundObjects: ArrayList<OpenGlObject2D> = ArrayList()

    private val textureShaderId = "TEXTURED"
    private val animationShaderId = "ANIMATED"
    private val boundShaderId = "BOUND"
    private val textShaderId = "TEXT_SHADER"
    private val colorShaderId = "COLORED"
    private val boxShaderId = "BOX"

    private var myRenderer: TextRenderer? = null
    private var animObj: LabyrinthCharacter? = null

    private var rect: Rectangle? = null
    private var background: OpenGlObject2D? = null
    private val screenWidth: Int = dim.width
    private val screenHeight: Int = dim.height
    private var renderProjection: Mat4? = null

    override fun init(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4

        loadShaders(gl)
        initShaders()

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY)
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        try {
            animObj = LabyrinthCharacter(2, 6, gl, 25f, 25f,
                    Dimension(50, 70), 0,
                    0.1f, 0.333f,
                    BasicAnimation("WALK", 1, 0, 6, 1, 100f),
                    BasicAnimation("JUMP", 2, 0, 3, 1, 200f),
                    BasicAnimation("IDLE", 3, 0, 1, 1, 100f))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val uvCoords = floatArrayOf(0f, 0f, 0.1f, 0.333f, 0f, 0.333f, 0f, 0f, 0.1f, 0f, 0.1f, 0.333f)

        animObj!!.initRenderData(arrayOf(ResourceLoader.getAbsolutePath("textures/labyrinth/base_dark.png")),
                false, Rectangle.RECTANGLE_BUFFER, uvCoords)

        this.controls.add(animObj!!)

        initLevelGeography(gl)
        this.renderProjection = Matrices.ortho(0.0f, screenWidth.toFloat(), screenHeight.toFloat(),
                0.0f, 0.0f, 1.0f)

        rect = Rectangle(gl, 200f, 200f, 100f, 50f, 0)
        rect!!.init(Color.WHITE)

        val charArr = arrayOf('p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '±', '`',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'O', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<',
                '=', '>', '?', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/')

        val chars = ArrayList<Char>()
        chars.addAll(charArr.asList())

        myRenderer = TextRenderer.getRenderer(Dimension(64, 64),
                this.javaClass.classLoader.getResource("textures/simpleFontAtlas.png")!!.path, chars)
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
        for (o in this.boundObjects) {
            o.dispose()
        }

        for (c in this.controls) {
            c.dispose()
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
            o.draw(o.size.width.toFloat(), o.size.height.toFloat(), 0.0f, curr)
        }

        curr = shaderInteractor.getShader(animationShaderId)
        shaderInteractor.activateShader(animationShaderId)
        shaderInteractor.updateShaders()
        animObj!!.draw(animObj!!.size.width.toFloat(), animObj!!.size.height.toFloat(), 0.0f, curr)

        curr = shaderInteractor.getShader(boxShaderId)
        shaderInteractor.activateShader(boxShaderId)
        animObj!!.drawBox(curr)

        curr = shaderInteractor.getShader(textShaderId)
        shaderInteractor.activateShader(textShaderId)
        myRenderer!!.drawText("Hello \n World!", Dimension(50, 50), gl, PointF(600f, 200f), curr)

        curr = shaderInteractor.getShader(colorShaderId)
        shaderInteractor.activateShader(colorShaderId)
        rect!!.draw(50f, 100f, 0f, curr)
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) {
        this.renderProjection = Matrices.ortho(0.0f, screenWidth.toFloat(), screenHeight.toFloat(), 0.0f, 0.0f, 1.0f)
    }

    override fun update(deltaTime: Float) {
        for (c in controls) {
            for (o in boundObjects)
                c.reactToCollision(o)
            c.update(deltaTime)
        }
    }

    override fun keyTyped(e: KeyEvent) {
        for (c in controls)
            c.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        for (c in controls)
            c.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        for (c in controls)
            c.keyReleased(e)
    }

    private fun loadShaders(gl: GL4) {
        val textureShaderObject = shaderCreator.create("shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl", gl)

        val textShaderObject = shaderCreator.create("shaders/textRenderVertexShader.glsl",
                "shaders/textRenderFragmentShader.glsl", gl)

        val boundShaderObject = shaderCreator.create("shaders/boundVertexShader.glsl",
                "shaders/boundFragmentShader.glsl", gl)

        val animationShaderObject = shaderCreator.create("shaders/animVertexShader.glsl",
                "shaders/animFragmentShader.glsl", gl)

        val colorShaderObject = shaderCreator.create("shaders/coloredVertexShader.glsl",
                "shaders/coloredFragmentShader.glsl", gl)

        val boxShaderObject = shaderCreator.create("shaders/boxVertexShader.glsl",
                "shaders/boxFragmentShader.glsl", gl)

        shaderInteractor.addShader(textureShaderId, textureShaderObject)
        shaderInteractor.addShader(textShaderId, textShaderObject)
        shaderInteractor.addShader(boundShaderId, boundShaderObject)
        shaderInteractor.addShader(animationShaderId, animationShaderObject)
        shaderInteractor.addShader(colorShaderId, colorShaderObject)
        shaderInteractor.addShader(boxShaderId, boxShaderObject)
    }

    private fun initShaders() {
        val defaultFunc: (Shader) -> Unit = {
            it.setMatrix4f("projection", renderProjection!!, true)
        }

        shaderInteractor.setShaderActivateFunction(textureShaderId, defaultFunc)
        shaderInteractor.setShaderActivateFunction(boundShaderId, defaultFunc)
        shaderInteractor.setShaderActivateFunction(animationShaderId, defaultFunc)
        shaderInteractor.setShaderActivateFunction(textShaderId, defaultFunc)
        shaderInteractor.setShaderActivateFunction(colorShaderId, defaultFunc)
        shaderInteractor.setShaderActivateFunction(boxShaderId, defaultFunc)

        shaderInteractor.setShaderUpdateFunction(animationShaderId) { animObj!!.defineAnimationVariables(it) }
    }

    private fun initLevelGeography(gl: GL4) {
        val levelCreator = LabyrinthLevelCreator()
        val levelPath = "config/labyrinthlevels/defaultlevel/defaultlevel.ini"
        val perimeter = levelCreator.createLevelFromFile(gl, ResourceLoader.getAbsolutePath(levelPath))

        this.boundObjects.addAll(perimeter)

        background = object : OpenGlObject2D(2, 6, gl, 0f, 0f, Dimension(1280, 720), 0) {
            public override fun loadTexture(filePath: String) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath)
                    initRepeatableTexParameters(texture!!, gl)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        val bgVertices = floatArrayOf(0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)
        val bgUVdata = floatArrayOf(10f, 0f, 0f, 10f, 10f, 10f, 10f, 0f, 0f, 0f, 0f, 10f)

        val texturePath = "textures/labyrinth/abbey_base.jpg"
        background!!.initRenderData(arrayOf(ResourceLoader.getAbsolutePath(texturePath)), false, bgVertices, bgUVdata)
    }

    companion object {
        internal fun initRepeatableTexParameters(texture: Texture, gl: GL4) {
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR)
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR)
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT)
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT)
        }
    }
}
