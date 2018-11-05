package demos.labrynth;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import engine.animation.BasicAnimation;
import engine.core.ControllableObject;
import engine.core.OpenGlObject;
import engine.shader.Shader;
import engine.text.TextRenderer;
import engine.texture.TextureLoader;
import engine.utilgeometry.PointF;
import states.GameState;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//TODO: load every texture with its own unique id
public class GameLabyrinth implements GameState {

    private ArrayList<ControllableObject> controls;
    private ArrayList<OpenGlObject> boundObjects;

    //TEST
    //private OpenGlObject texArrayObj;
    //private OpenGlObject testObject;
    private TextRenderer myRenderer;
    private LabyrinthCharacter animObj;
    private Shader texShader;
    private Shader boundShader;
    private Shader texArrayShader;
    private Shader animShader;

    private OpenGlObject background;
    private int screenWidth;
    private int screenHeight;
    private Mat4 renderProjection;

    public GameLabyrinth(Dimension dim) {
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;

        this.controls = new ArrayList<>();
        this.boundObjects = new ArrayList<>();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {

        GL4 gl = glAutoDrawable.getGL().getGL4();

        loadShader(gl);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        texArrayObj = new OpenGlObject(2, 6, gl, 500, 300, new Dimension(1024, 512), 2);
//        texArrayObj.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/Idle.png").getPath()}, true,
//                new float[]{0f, 1f,
//                        1f, 0f,
//                        0f, 0f,
//                        0f, 1f,
//                        1f, 1f,
//                        1f, 0f},
//                new float[]{0f, 0f,
//                        1f, 1f,
//                        0f, 1f,
//                        0f, 0f,
//                        1f, 0f,
//                        1f, 1f});
        try {
            animObj = new LabyrinthCharacter(2, 6, gl, 25, 25,
                    new Dimension(50, 70), 5,
                    0.1f, 0.333f,
                    new BasicAnimation("WALK", 1, 0, 6, 1, 100f),
                    new BasicAnimation("JUMP", 2, 0, 3, 1, 200f),
                    new BasicAnimation("IDLE", 3, 0, 1, 1, 100f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        animObj.initRenderData(new String[]{this.
                        getClass().
                        getClassLoader().
                        getResource("textures/labyrinth/base_dark.png").
                        getPath()},
                false,
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]{0f, 0f,
                        0.1f, 0.333f,
                        0f, 0.333f,
                        0f, 0f,
                        0.1f, 0f,
                        0.1f, 0.333f});
        this.controls.add(animObj);

        initLevelGeography(gl);
        this.renderProjection = Matrices.ortho(0.0f, (float) screenWidth, (float) screenHeight,
                0.0f, 0.0f, 1.0f);

        //System.out.println(gl.glGetError() + " init end");

        //TODO: test text renderer
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
        //Collections.reverse(chars);

        myRenderer = TextRenderer.getRenderer(new Dimension(64,64),
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

        texShader.setMatrix4f("projection", renderProjection, false);
        myRenderer.drawCharacter('!', new Dimension(100,100), gl, new PointF(200,200), texShader);
        //texArrayShader.setMatrix4f("projection", renderProjection, false);
        //texArrayObj.draw(texArrayObj.getSize().width, texArrayObj.getSize().height, 0.0f, texArrayShader);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

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

    private void loadShader(GL4 gl) {
        //-----------------------SHADER TEST------------------------
        String[] textVertexSource = new String[1];
        String[] textFragmSource = new String[1];
        try {
            textVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texturedVertexShader.glsl").getPath());
            textFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texturedFragmentShader.glsl").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        texShader = new Shader(gl);
        texShader.compile(textVertexSource, textFragmSource, null);

        String[] boundVertexSource = new String[1];
        String[] boundFragmSource = new String[1];
        try {
            boundVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/boundVertexShader.glsl").getPath());
            boundFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/boundFragmentShader.glsl").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boundShader = new Shader(gl);
        boundShader.compile(boundVertexSource, boundFragmSource, null);

        String[] arrayVertexSource = new String[1];
        String[] arrayFragmSource = new String[1];
        try {
            arrayVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texArrayVertexShader.glsl").getPath());
            arrayFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texArrayFragmentShader.glsl").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        texArrayShader = new Shader(gl);
        texArrayShader.compile(arrayVertexSource, arrayFragmSource, null);

        String[] animVertexSource = new String[1];
        String[] animFragmSource = new String[1];
        try {
            animVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/animVertexShader.glsl").getPath());
            animFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/animFragmentShader.glsl").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        animShader = new Shader(gl);
        animShader.compile(animVertexSource, animFragmSource, null);
        //--------------------------------------------------------
    }

    private void initLevelGeography(GL4 gl) {

        LabyrinthLevelCreator lc = new LabyrinthLevelCreator();
        ArrayList<OpenGlObject> perimeter = lc.createLevelFromFile(gl, this.getClass().getClassLoader().
                getResource("config/labyrinthlevels/defaultlevel/defaultlevel.ini").getPath());

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

        background.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/labyrinth/abbey_base.jpg").getPath()}, false,
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]{10f, 0f,
                        0f, 10f,
                        10f, 10f,
                        10f, 0f,
                        0f, 0f,
                        0f, 10f});
    }

    static void initRepeatableTexParameters(Texture texture, GL4 gl){
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
    }
}
