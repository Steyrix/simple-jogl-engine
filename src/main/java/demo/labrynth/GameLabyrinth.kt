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
import engine.core.OpenGlObject
import engine.feature.primitive.Rectangle
import engine.feature.shader.Shader
import engine.feature.shader.ShaderCreator
import engine.feature.text.TextRenderer
import engine.feature.texture.TextureLoader
import engine.util.geometry.PointF
import engine.core.state.GameState

import java.awt.*
import java.awt.event.*
import java.util.ArrayList
import java.util.Arrays

//TODO: load every texture with its own unique id
class GameLabyrinth(dim: Dimension, private val shaderCreator: ShaderCreator) : GameState {

    private val controls: ArrayList<ControllableObject> = ArrayList()
    private val boundObjects: ArrayList<OpenGlObject> = ArrayList()

    private var myRenderer: TextRenderer? = null
    private var animObj: LabyrinthCharacter? = null
    private var texShader: Shader? = null
    private var boundShader: Shader? = null
    private var texArrayShader: Shader? = null
    private var animShader: Shader? = null
    private var textRenderShader: Shader? = null
    private var colorShader: Shader? = null

    private var rect: Rectangle? = null
    private var background: OpenGlObject? = null
    private val screenWidth: Int = dim.width
    private val screenHeight: Int = dim.height
    private var renderProjection: Mat4? = null

    override fun init(glAutoDrawable: GLAutoDrawable) {
        val gl = glAutoDrawable.gl.gL4

        loadShaders(gl)

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

        val UV = floatArrayOf(0f, 0f, 0.1f, 0.333f, 0f, 0.333f, 0f, 0f, 0.1f, 0f, 0.1f, 0.333f)

        animObj!!.initRenderData(arrayOf(ResourceLoader.getAbsolutePath("textures/labyrinth/base_dark.png")),
                false, Rectangle.RECTANGLE_BUFFER, UV)

        this.controls.add(animObj!!)

        initLevelGeography(gl)
        this.renderProjection = Matrices.ortho(0.0f, screenWidth.toFloat(), screenHeight.toFloat(),
                0.0f, 0.0f, 1.0f)

        rect = Rectangle(gl, 200f, 200f, 100f, 50f, 0)
        rect!!.init(Color.WHITE)

        val charArr = arrayOf('p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '±', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/')
        val chars = ArrayList<Char>()
        chars.addAll(Arrays.asList(*charArr))

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

        texShader!!.setMatrix4f("projection", renderProjection!!, false)
        background!!.draw(0f, 0f, 1280f, 720f, 0.0f, texShader!!)

        boundShader!!.setMatrix4f("projection", renderProjection!!, false)

        for (o in boundObjects) {
            o.draw(o.size.width.toFloat(), o.size.height.toFloat(), 0.0f, boundShader!!)
        }

        animShader!!.setMatrix4f("projection", renderProjection!!, false)
        animObj!!.draw(animObj!!.size.width.toFloat(), animObj!!.size.height.toFloat(), 0.0f, animShader!!)

        textRenderShader!!.setMatrix4f("projection", renderProjection!!, false)
        myRenderer!!.drawText("Hello \n World!", Dimension(50, 50), gl, PointF(600f, 200f), textRenderShader!!)

        colorShader!!.setMatrix4f("projection", renderProjection!!, false)
        rect!!.draw(50f, 100f, 0f, colorShader!!)
        //texArrayShader.setMatrix4f("projection", renderProjection, false);
        //texArrayObj.draw(texArrayObj.getSize().width, texArrayObj.getSize().height, 0.0f, texArrayShader);
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
        //-----------------------SHADER TEST------------------------
        texShader = shaderCreator.create("shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl", gl)

        textRenderShader = shaderCreator.create("shaders/textRenderVertexShader.glsl",
                "shaders/textRenderFragmentShader.glsl", gl)


        boundShader = shaderCreator.create("shaders/boundVertexShader.glsl",
                "shaders/boundFragmentShader.glsl", gl)


        texArrayShader = shaderCreator.create("shaders/texArrayVertexShader.glsl",
                "shaders/texArrayFragmentShader.glsl", gl)

        animShader = shaderCreator.create("shaders/animVertexShader.glsl",
                "shaders/animFragmentShader.glsl", gl)

        colorShader = shaderCreator.create("shaders/coloredVertexShader.glsl",
                "shaders/coloredFragmentShader.glsl", gl)
        //--------------------------------------------------------
    }


    private fun initLevelGeography(gl: GL4) {
        val levelCreator = LabyrinthLevelCreator()
        val levelPath = "config/labyrinthlevels/defaultlevel/defaultlevel.ini"
        val perimeter = levelCreator.createLevelFromFile(gl, ResourceLoader.getAbsolutePath(levelPath))

        this.boundObjects.addAll(perimeter)

        background = object : OpenGlObject(2, 6, gl, 0f, 0f, Dimension(1280, 720), 0) {
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
