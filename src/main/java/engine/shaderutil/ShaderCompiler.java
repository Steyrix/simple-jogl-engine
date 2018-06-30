package engine.shaderutil;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;

public class ShaderCompiler {
    public static void createShader(GL3 gl, int program){
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

        System.out.println(gl.glGetError());
    }
}
