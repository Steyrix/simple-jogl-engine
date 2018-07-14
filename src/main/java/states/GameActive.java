package states;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import engine.OpenGlObject;
import engine.shaderutil.Shader;

import java.awt.*;
import java.awt.event.*;

public class GameActive implements GameState {

    private Shader shader;
    private OpenGlObject myObj;
    private int screenWidth;
    private int screenHeight;

    private float dx;
    private float dy;
    private float posX;
    private float posY;

    public GameActive(Dimension dim){
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;

        this.dx = 0.0f;
        this.dy = 0.0f;

        this.posX = 0.0f;
        this.posY = 0.0f;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();

        //-----------------------SHADER TEST------------------------
        String[] vertexShaderSource = new String[1];
        vertexShaderSource[0] = "#version 330\n" +
                "layout(location=0) in vec2 position;\n" +
                "layout(location=1) in vec3 color;\n" +
                "out vec4 vColor;\n" +
                "uniform mat4 model;" +
                "uniform mat4 projection;" +
                "void main(void)\n" +
                "{\n" +
                "gl_Position = projection * model * vec4(position, 0.0, 1.0);\n" +
                "vColor = vec4(color, 1.0);\n" +
                "}\n";
        String[] fragmentShaderSource = new String[1];
        fragmentShaderSource[0] = "#version 330\n" +
                "in vec4 vColor;\n" +
                "out vec4 fColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "fColor = vColor;\n" +
                "}\n";

        shader = new Shader(gl);
        shader.compile(vertexShaderSource,fragmentShaderSource,null);

        //--------------------------------------------------------

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

        myObj = new OpenGlObject( 2, 6, gl);
        myObj.initRenderData(new float[]{0.0f, 1f,
                        1f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1f,
                        1f, 1f,
                        1f, 0.0f},
                new float[]{0.1f, 0.2f, 0.3f,
                        0.6f, 0.5f, 0.4f,
                        0.0f, 0.0f, 0.0f,
                        2.0f, 1.0f, 3.0f,
                        0.0f, 0.0f, 0.0f,
                        0.7f, 0.8f, 0.9f});

        System.out.println(gl.glGetError() + " init end");

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);

        System.out.println(gl.glGetError() + " display0");

        Mat4 projection = Matrices.ortho(0.0f, (float)screenWidth, (float)screenHeight,
                0.0f, 0.0f, 1.0f);
        shader.setMatrix4f("projection", projection, false);

        myObj.draw(posX, posY, 100f, 100f, 0.0f, shader);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);

        System.out.println(gl.glGetError() + " reshape0");

        Mat4 projection = Matrices.ortho(0.0f, (float)screenWidth, (float)screenHeight,
                0.0f, 0.0f, 1.0f);
        shader.setMatrix4f("projection", projection, false);

        myObj.draw(posX, posY, 100f, 100f, 0.0f, shader);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        posX += dx;
        if(dx >= 0.0f && dx - 1.0f >= 0.0f)
            dx -= 1.0f;
        else if(dx <= 0.0f && dx + 1.0f <= 0.0f)
            dx += 1.0f;


        posY += dy;
        if(dy >= 0.0f && dy - 1.0f >= 0.0f)
            dy -= 1.0f;
        else if(dy <= 0.0f && dy + 1.0f <= 0.0f)
            dy += 1.0f;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PRESSED");
        switch (e.getKeyCode()){
            case KeyEvent.VK_D:
                this.dx += 10.0f;
                break;
            case KeyEvent.VK_A:
                this.dx -= 10.0f;
                break;
            case KeyEvent.VK_W:
                this.dy -= 10.0f;
                break;
            case KeyEvent.VK_S:
                this.dy += 10.0f;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


}
