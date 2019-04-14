package demos.labrynth;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import engine.feature.ResourceLoader;
import engine.feature.animation.BasicAnimation;
import engine.core.ControllableObject;
import engine.core.OpenGlObject;
import engine.feature.primitives.Rectangle;
import engine.feature.shader.Shader;
import engine.feature.shader.ShaderCreator;
import engine.feature.text.TextRenderer;
import engine.feature.texture.TextureLoader;
import engine.util.utilgeometry.PointF;
import engine.core.state.GameState;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

//TODO: load every texture with its own unique id
//TODO: DI
public class GameLabyrinth implements GameState {

    private ArrayList<ControllableObject> controls;
    private ArrayList<OpenGlObject> boundObjects;

    private ResourceLoader loader;

    private TextRenderer myRenderer;
    private LabyrinthCharacter animObj;
    private ShaderCreator shaderCreator;
    private Shader texShader;
    private Shader boundShader;
    private Shader texArrayShader;
    private Shader animShader;
    private Shader textRenderShader;
    private Shader colorShader;

    private Rectangle rect;
    private OpenGlObject background;
    private int screenWidth;
    private int screenHeight;
    private Mat4 renderProjection;

    public GameLabyrinth(Dimension dim, ShaderCreator shaderCreator) {
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;

        this.controls = new ArrayList<>();
        this.boundObjects = new ArrayList<>();

        this.shaderCreator = shaderCreator;

        this.loader = new ResourceLoader();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {

        GL4 gl = glAutoDrawable.getGL().getGL4();

        loadShaders(gl);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        try {
            animObj = new LabyrinthCharacter(2, 6, gl, 25, 25,
                    new Dimension(50, 70), 0,
                    0.1f, 0.333f,
                    new BasicAnimation("WALK", 1, 0, 6, 1, 100f),
                    new BasicAnimation("JUMP", 2, 0, 3, 1, 200f),
                    new BasicAnimation("IDLE", 3, 0, 1, 1, 100f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final float[] UV = new float[]{0f, 0f,
                0.1f, 0.333f,
                0f, 0.333f,
                0f, 0f,
                0.1f, 0f,
                0.1f, 0.333f};

        animObj.initRenderData(new String[]{loader.get("textures/labyrinth/base_dark.png")},
                false, Rectangle.RECTANGLE_BUFFER, UV);

        this.controls.add(animObj);

        initLevelGeography(gl);
        this.renderProjection = Matrices.ortho(0.0f, (float) screenWidth, (float) screenHeight,
                0.0f, 0.0f, 1.0f);

        rect = new Rectangle(gl, 200, 200, 100, 50, 0);
        rect.init(Color.WHITE);

        Character[] charArr = new Character[]{
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '±',
                '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
                '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
                ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
        };
        ArrayList<Character> chars = new ArrayList<>();
        chars.addAll(Arrays.asList(charArr));

        myRenderer = TextRenderer.getRenderer(new Dimension(64, 64),
                this.getClass().getClassLoader().getResource("textures/simpleFontAtlas.png").getPath(), chars);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        for (OpenGlObject o : this.boundObjects) {
            o.dispose();
        }

        for (ControllableObject c : this.controls) {
            c.dispose();
        }
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

        GL4 gl = glAutoDrawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);

        texShader.setMatrix4f("projection", renderProjection, false);
        background.draw(0f, 0f, 1280f, 720f, 0.0f, texShader);

        boundShader.setMatrix4f("projection", renderProjection, false);

        for (OpenGlObject o : boundObjects) {
            o.draw(o.getSize().width, o.getSize().height, 0.0f, boundShader);
        }

        animShader.setMatrix4f("projection", renderProjection, false);
        animObj.draw(animObj.getSize().width, animObj.getSize().height, 0.0f, animShader);

        textRenderShader.setMatrix4f("projection", renderProjection, false);
        myRenderer.drawText("Hello \n World!", new Dimension(50, 50), gl, new PointF(600, 200), textRenderShader);

        colorShader.setMatrix4f("projection", renderProjection, false);
        rect.draw(50, 100, 0, colorShader);
        //texArrayShader.setMatrix4f("projection", renderProjection, false);
        //texArrayObj.draw(texArrayObj.getSize().width, texArrayObj.getSize().height, 0.0f, texArrayShader);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        this.renderProjection = Matrices.ortho(0.0f, (float) screenWidth, (float) screenHeight, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void update(float deltaTime) {
        //System.out.println("upd" + "; " + elapsedTime + ";" + cnt++);
        for (ControllableObject c : controls) {

            for (OpenGlObject o : boundObjects)
                c.reactToCollision(o);

            c.update(deltaTime);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        for (ControllableObject c : controls)
            c.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for (ControllableObject c : controls)
            c.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (ControllableObject c : controls)
            c.keyReleased(e);
    }

    private void loadShaders(GL4 gl) {
        //-----------------------SHADER TEST------------------------
        texShader = shaderCreator.create("shaders/texturedVertexShader.glsl",
                "shaders/texturedFragmentShader.glsl", gl);

        textRenderShader = shaderCreator.create("shaders/textRenderVertexShader.glsl",
                "shaders/textRenderFragmentShader.glsl", gl);


        boundShader = shaderCreator.create("shaders/boundVertexShader.glsl",
                "shaders/boundFragmentShader.glsl", gl);


        texArrayShader = shaderCreator.create("shaders/texArrayVertexShader.glsl",
                "shaders/texArrayFragmentShader.glsl", gl);

        animShader = shaderCreator.create("shaders/animVertexShader.glsl",
                "shaders/animFragmentShader.glsl", gl);

        colorShader = shaderCreator.create("shaders/coloredVertexShader.glsl",
                "shaders/coloredFragmentShader.glsl", gl);
        //--------------------------------------------------------
    }


    private void initLevelGeography(GL4 gl) {

        LabyrinthLevelCreator lc = new LabyrinthLevelCreator();
        ArrayList<OpenGlObject> perimeter = lc.createLevelFromFile(gl,loader.get("config/labyrinthlevels/defaultlevel/defaultlevel.ini"));

        this.boundObjects.addAll(perimeter);

        background = new OpenGlObject(2, 6, gl, 0f, 0f, new Dimension(1280, 720), 0) {
            @Override
            public void loadTexture(String filePath) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    initRepeatableTexParameters(texture, gl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final float[] bgVertices = new float[]{0f, 1f,
                                                1f, 0f,
                                                0f, 0f,
                                                0f, 1f,
                                                1f, 1f,
                                                1f, 0f};

        final float[] bgUVdata = new float[]{10f, 0f,
                                                0f, 10f,
                                                10f, 10f,
                                                10f, 0f,
                                                0f, 0f,
                                                0f, 10f};

        background.initRenderData(new String[]{loader.get("textures/labyrinth/abbey_base.jpg")},
                false, bgVertices, bgUVdata);
    }

    static void initRepeatableTexParameters(Texture texture, GL4 gl) {
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
    }
}
