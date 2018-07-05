import com.jogamp.opengl.*;
import engine.OpenGlObject;
import engine.shaderutil.Shader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BaseFrame implements GLEventListener, KeyListener
{
    private int screenWidth;
    private int screenHeight;
    private int program;
    private Shader shader;
    private OpenGlObject myObj;

    public BaseFrame(Dimension dim){
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();

        //-----------------------SHADER TEST------------------------
        String[] vertexShaderSource = new String[1];
        vertexShaderSource[0] = "#version 330\n" +
                "layout(location=0) in vec2 position;\n" +
                "layout(location=1) in vec3 color;\n" +
                "out vec4 vColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "gl_Position = vec4(position, 0.0, 1.0);\n" +
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
        //-----------------------SHADER TEST-----------------------

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glClearDepthf(10.0f);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDepthFunc(GL3.GL_LEQUAL);

        myObj = new OpenGlObject(null, 2, 6, gl);
        myObj.addBuffers(new float[]{-0.5f, -0.5f,
                                    0.5f, -0.5f,
                                    0.5f, 0.5f,
                                    0.5f, 0.5f,
                                    -0.5f, 0.5f,
                                    -0.5f, -0.5f,},
                            new float[]{0.1f, 0.2f, 0.3f,
                                    0.6f, 0.5f, 0.4f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.7f, 0.8f, 0.9f});

        System.out.println(gl.glGetError() + " init end");
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
        System.out.println(gl.glGetError() + " display0");

        shader.use();

        System.out.println(gl.glGetError() + " display1");

        myObj.draw();
        System.out.println(gl.glGetError() + " display2");
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
