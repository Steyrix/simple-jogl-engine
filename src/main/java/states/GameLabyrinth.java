package states;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import engine.BoundingBox;
import engine.ControllableObject;
import engine.OpenGlObject;
import engine.shader.Shader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

//TODO: implement multishadering or adaptive shader
public class GameLabyrinth implements GameState {

    private Shader shader;
    private Shader texShader;

    private ArrayList<ControllableObject> controls;
    private ArrayList<OpenGlObject> coloredObjects;
    private ArrayList<OpenGlObject> texturedObjects;
    private int screenWidth;
    private int screenHeight;
    private Mat4 renderProjection;
    private float[] mapX;
    private float[] mapY;

    public GameLabyrinth(Dimension dim) {
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;

        this.controls = new ArrayList<>();
        this.coloredObjects = new ArrayList<>();
        this.texturedObjects = new ArrayList<>();

        this.mapX = new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 2, 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        this.mapY = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                4, 4, 4, 4, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 0, 1, 2, 3, 4, 8, 9, 10, 11, 12};
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();

        loadShaders(glAutoDrawable);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

        ControllableObject myObj = new ControllableObject(2, 6, gl, 50, 25, new Dimension(50, 50)) {
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


                System.out.println("Pos: " + posX + "; " + posY + "\nVelocity: " + velocityX + "; " + velocityY + "\n \n");
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

        myObj.initRenderData(null,
                new float[]{0.0f, 1f,
                        1f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1f,
                        1f, 1f,
                        1f, 0.0f},
                new float[]{0.1f, 0.2f, 0.3f,
                        0.7f, 0.8f, 0.9f,
                        0.7f, 0.8f, 0.9f,
                        0.1f, 0.2f, 0.3f,
                        0.7f, 0.8f, 0.9f,
                        0.7f, 0.8f, 0.9f});

        this.controls.add(myObj);
        this.coloredObjects.add(myObj);

        int count = mapX.length;
        for (int k = 0; k < count; k++) {
            OpenGlObject boundObject = new OpenGlObject(2, 6, gl, mapX[k] * 25f,
                    mapY[k] * 25f, new Dimension(25, 25));
            boundObject.initRenderData(null,
                    new float[]{0.0f, 1f,
                            1f, 0.0f,
                            0.0f, 0.0f,
                            0.0f, 1f,
                            1f, 1f,
                            1f, 0.0f},
                    new float[]{0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f});
            this.coloredObjects.add(boundObject);

        }

        OpenGlObject texObj = new OpenGlObject(2, 6, gl,
                250, 250, new Dimension(100, 100));


        texObj.initRenderData(this.getClass().getClassLoader().getResource("angryBird.png").getPath(),
                new float[]
                        {0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]
                        {1f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f});

        this.texturedObjects.add(texObj);


        this.renderProjection = Matrices.ortho(0.0f, (float) screenWidth, (float) screenHeight,
                0.0f, 0.0f, 1.0f);

        //System.out.println(gl.glGetError() + " init end");

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        for (OpenGlObject o : this.coloredObjects) {
            o.dispose();
        }
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);

        shader.setMatrix4f("projection", renderProjection, false);

        for (OpenGlObject o : coloredObjects) {
            if (o instanceof ControllableObject)
                o.draw(50f, 50f, 0.0f, shader);
            else
                o.draw(25f, 25f, 0.0f, shader);
        }

        texShader.setMatrix4f("projection", renderProjection, false);

        for(OpenGlObject o : texturedObjects){
            o.draw(50f, 50f, 0.0f, texShader);
        }

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ControllableObject c : controls) {
            c.actionPerformed(e);

            for (OpenGlObject o : coloredObjects)
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

    private void loadShaders(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
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

        //--------------------------------------------------------
    }

}
