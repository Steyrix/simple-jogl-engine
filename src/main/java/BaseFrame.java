import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.*;
import engine.OpenGlObject;
import engine.shaderutil.ShaderCompiler;

import java.awt.*;

public class BaseFrame implements GLEventListener
{
    private int screenWidth;
    private int screenHeight;
    private int program;
    private OpenGlObject myObj;

    public BaseFrame(Dimension dim){
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();

        // Create program.
        program = gl.glCreateProgram();

        // Create vertexShader.
        int vertexShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
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
        gl.glShaderSource(vertexShader, 1, vertexShaderSource, null);
        gl.glCompileShader(vertexShader);
        System.out.println(gl.glGetError() + " init vertex shader");
        // Create and fragment shader.
        int fragmentShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        String[] fragmentShaderSource = new String[1];
        fragmentShaderSource[0] = "#version 330\n" +
                "in vec4 vColor;\n" +
                "out vec4 fColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "fColor = vColor;\n" +
                "}\n";
        gl.glShaderSource(fragmentShader, 1, fragmentShaderSource, null);
        gl.glCompileShader(fragmentShader);
        System.out.println(gl.glGetError() + " init fragment shader");
        // Attach shaders to program.
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);

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
                            new float[]{0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f});

        System.out.println(gl.glGetError() + " init end");
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
        System.out.println(gl.glGetError() + " display0");

        gl.glUseProgram(program);
        System.out.println(gl.glGetError() + " display1");

        myObj.draw();
        System.out.println(gl.glGetError() + " display2");
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
