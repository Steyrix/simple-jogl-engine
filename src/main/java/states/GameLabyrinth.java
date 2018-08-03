package states;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import engine.collision.BoundingBox;
import engine.core.ControllableObject;
import engine.core.OpenGlObject;
import engine.shader.Shader;
import engine.texture.TextureLoader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


//TODO: normal collider
public class GameLabyrinth implements GameState {

    private Shader shader;
    private Shader texShader;
    private Shader boundShader;
    private Shader texArrayShader;

    private ArrayList<ControllableObject> controls;
    private ArrayList<OpenGlObject> boundObjects;
    private ArrayList<OpenGlObject> texturedObjects;

    //TEST
    private OpenGlObject texArrayObj;

    private OpenGlObject background;
    private int screenWidth;
    private int screenHeight;
    private Mat4 renderProjection;

    public GameLabyrinth(Dimension dim) {
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;

        this.controls = new ArrayList<>();
        this.boundObjects = new ArrayList<>();
        this.texturedObjects = new ArrayList<>();

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();

        loadShaders(gl);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

        ControllableObject bird = new ControllableObject(2, 6, gl, 50, 25, new Dimension(50, 50)) {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("PRESSED");
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        this.velocityX += 10.0f;
                        break;
                    case KeyEvent.VK_A:
                        this.velocityX -= 10.0f;
                        break;
                    case KeyEvent.VK_W:
                        this.velocityY -= 10.0f;
                        break;
                    case KeyEvent.VK_S:
                        this.velocityY += 10.0f;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                this.posX += this.velocityX + this.velocityCollX;
                if (velocityX >= 0.0f && velocityX - 1.0f >= 0.0f)
                    velocityX -= 1.0f;
                else if (velocityX < 0.0f && velocityX + 1.0f <= 0.0f)
                    velocityX += 1.0f;
                else if (velocityX >= 0.0f && velocityX - 1.0f < 0.0f ||
                        velocityX < 0.0f && velocityX + 1.0f > 0.0f)
                    velocityX = 0.0f;

                if (velocityCollX >= 0.0f && velocityCollX - 0.1f >= 0.0f)
                    velocityCollX -= 0.1f;
                else if (velocityCollX <= 0.0f && velocityCollX + 0.1f <= 0.0f)
                    velocityCollX += 0.1f;
                else if (velocityCollX >= 0.0f && velocityCollX - 0.1f < 0.0f ||
                        velocityCollX < 0.0f && velocityCollX + 0.1f > 0.0f)
                    velocityCollX = 0.0f;

                this.posY += this.velocityY + this.velocityCollY;
                if (velocityY >= 0.0f && velocityY - 1.0f >= 0.0f)
                    velocityY -= 1.0f;
                else if (velocityY <= 0.0f && velocityY + 1.0f <= 0.0f)
                    velocityY += 1.0f;
                else if (velocityY >= 0.0f && velocityY - 1.0f < 0.0f ||
                        velocityY < 0.0f && velocityY + 1.0f > 0.0f)
                    velocityY = 0.0f;

                if (velocityCollY >= 0.0f && velocityCollY - 0.1f >= 0.0f)
                    velocityCollY -= 0.1f;
                else if (velocityCollY <= 0.0f && velocityCollY + 0.1f <= 0.0f)
                    velocityCollY += 0.1f;
                else if (velocityCollY >= 0.0f && velocityCollY - 0.1f < 0.0f ||
                        velocityCollY < 0.0f && velocityCollY + 0.1f > 0.0f)
                    velocityCollY = 0.0f;


                //System.out.println("Pos: " + posX + "; " + posY + "\nVelocity: " + velocityX + "; " + velocityY + "\n \n");
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            protected void reactToCollision(BoundingBox anotherBox) {
                if (intersects(anotherBox)) {
                    if (this.velocityX != 0.0f && this.velocityY != 0.0f) {

                        this.velocityCollX = -1.0f * this.velocityX * 0.2f;
                        this.velocityX = 0.0f;
                        this.velocityCollY = -1.0f * this.velocityY * 0.2f;
                        this.velocityY = 0.0f;

                    } else if (this.velocityX != 0.0f) {
                        if (this.velocityX > 0.0f)
                            this.posX = anotherBox.getPosX() - this.width;
                        else
                            this.posX = anotherBox.getRight();

                        this.velocityCollX = -1.0f * this.velocityX * 0.2f;
                        this.velocityX = 0.0f;

                    } else if (this.velocityY != 0.0f) {
                        if (this.velocityY > 0.0f)
                            this.posY = anotherBox.getPosY() - this.height;
                        else
                            this.posY = anotherBox.getBottom();

                        this.velocityCollY = -1.0f * this.velocityY * 0.2f;
                        this.velocityY = 0.0f;
                    }
                }

            }
        };

        bird.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/angryBird.png").getPath()}, false,
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]{1f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f});

        this.controls.add(bird);

        texArrayObj = new OpenGlObject(2,6,gl, 500,300, new Dimension(100,100));
        texArrayObj.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/Idle.png").getPath()}, true,
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f});


        initLevelGeography( new float[]{1, 2, 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                        10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 12, 13, 14},
                            new float[]{4, 4, 4, 4, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                                    1, 2, 3, 4, 8, 9, 10, 11, 12, 12, 12, 12, 12},
                            gl);

        this.renderProjection = Matrices.ortho(0.0f, (float) screenWidth, (float) screenHeight,
                0.0f, 0.0f, 1.0f);

        //System.out.println(gl.glGetError() + " init end");

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        for (OpenGlObject o : this.boundObjects) {
            o.dispose();
        }

        for (OpenGlObject t : this.texturedObjects) {
            t.dispose();
        }

        for(ControllableObject c : this.controls)
        {
            c.dispose();
        }

        texArrayObj.dispose();
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

        texShader.setMatrix4f("projection", renderProjection, false);

        for (OpenGlObject o : texturedObjects) {
            o.draw(50f, 50f, 0.0f, texShader);
        }

        for (ControllableObject c : controls) {
            Shader usedShader;
            if (c.isTextured()) {
                usedShader = texShader;
                texShader.setMatrix4f("projection", renderProjection, false);
            } else {
                usedShader = shader;
                shader.setMatrix4f("projection", renderProjection, false);
            }
            c.draw(50f, 50f, 0.0f, usedShader);
        }

        texArrayShader.setMatrix4f("projection", renderProjection, false);
        texArrayObj.draw(300f, 300f, 0.0f, texArrayShader);

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ControllableObject c : controls) {
            c.actionPerformed(e);

            for (OpenGlObject o : boundObjects)
                if (o != c && c.intersects(o) && !c.isTouching(o))
                    c.collide(o);

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
        String[] textVertexSource = new String[1];
        String[] textFragmSource = new String[1];
        try {
            textVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texturedVertexShader").getPath());
            textFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texturedFragmentShader").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        texShader = new Shader(gl);
        texShader.compile(textVertexSource, textFragmSource, null);

        String[] vertexSource = new String[1];
        String[] fragmSource = new String[1];
        try {
            vertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/coloredVertexShader").getPath());
            fragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/coloredFragmentShader").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        shader = new Shader(gl);
        shader.compile(vertexSource, fragmSource, null);

        String[] boundVertexSource = new String[1];
        String[] boundFragmSource = new String[1];
        try {
            boundVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/boundVertexShader").getPath());
            boundFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/boundFragmentShader").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boundShader = new Shader(gl);
        boundShader.compile(boundVertexSource, boundFragmSource, null);

        String[] arrayVertexSource = new String[1];
        String[] arrayFragmSource = new String[1];
        try {
            arrayVertexSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texArrayVertexShader").getPath());
            arrayFragmSource[0] = Shader.readFromFile(getClass().getClassLoader().getResource("shaders/texArrayFragmentShader").getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        texArrayShader = new Shader(gl);
        texArrayShader.compile(arrayVertexSource, arrayFragmSource, null);

        //--------------------------------------------------------
    }

    private void initLevelGeography(float[] mapHorizontal, float[] mapVertical, GL4 gl) {

        int count = mapHorizontal.length;
        for (int k = 0; k < count; k++) {
            OpenGlObject boundObject = new OpenGlObject(2, 6, gl, mapHorizontal[k] * 25f,
                    mapVertical[k] * 25f, new Dimension(25, 25));
            boundObject.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
                    new float[]{0f, 1f,
                            1f, 0f,
                            0f, 0f,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f},
                    new float[]{1f, 0f,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            0f, 1f});
            this.boundObjects.add(boundObject);
        }
        //-----------PERIMETER TEST---------------
        OpenGlObject topHorizontalBound = new OpenGlObject(2, 6, gl, 0, 0, new Dimension(1280, 25)){
            @Override
            public void loadTexture(String filePath){
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        topHorizontalBound.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
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
        this.boundObjects.add(topHorizontalBound);

        OpenGlObject bottomHorizontalBound = new OpenGlObject(2, 6, gl, 0, 695, new Dimension(1280, 25)){
            @Override
            public void loadTexture(String filePath){
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bottomHorizontalBound.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
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
        this.boundObjects.add(bottomHorizontalBound);

        OpenGlObject leftVerticalBound = new OpenGlObject(2, 6, gl, 0, 25, new Dimension(25, 670)){
            @Override
            public void loadTexture(String filePath){
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        leftVerticalBound.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
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
        this.boundObjects.add(leftVerticalBound);

        OpenGlObject rightVerticalBound = new OpenGlObject(2, 6, gl, 1255, 25, new Dimension(25, 670)){
            @Override
            public void loadTexture(String filePath){
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        rightVerticalBound.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
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
        this.boundObjects.add(rightVerticalBound);
        //

        background = new OpenGlObject(2, 6, gl, 0f, 0f, new Dimension(1280, 720)) {
            @Override
            public void loadTexture(String filePath){
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
                    texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        background.initRenderData(new String[]{this.getClass().getClassLoader().getResource("textures/abbey_base.jpg").getPath()}, false,
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


    //TODO: implement reading level geography from files
    public void loadLevel(String levelConfigFilePath){
        Scanner configReader = new Scanner(System.in);

    }

}
