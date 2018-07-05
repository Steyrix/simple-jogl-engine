package engine.shaderutil;

import com.jogamp.opengl.GL3;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Shader {
    private int id;
    private final GL3 gl;

    public Shader(GL3 gl){
        this.gl = gl;
    }

    public void use(){
        gl.glUseProgram(id);
    }

    public void compile(String[] vertexShaderSource, String[] fragmentShaderSource,
                        String[] geometryShaderSource) {
        int sVertex = 0, sFragment = 0, sGeometry = 0;

        sVertex = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        gl.glShaderSource(sVertex, 1, vertexShaderSource, null);
        gl.glCompileShader(sVertex);
        checkCompileErrors(sVertex, "VERTEX");

        sFragment = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        gl.glShaderSource(sFragment, 1, fragmentShaderSource, null);
        gl.glCompileShader(sFragment);
        checkCompileErrors(sFragment, "FRAGMENT");

        if(geometryShaderSource != null){
            sGeometry = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);
            gl.glShaderSource(sGeometry,1, geometryShaderSource, null);
            gl.glCompileShader(sGeometry);
            checkCompileErrors(sGeometry, "GEOMETRY");
        }

        id = gl.glCreateProgram();
        gl.glAttachShader(id, sVertex);
        gl.glAttachShader(id, sFragment);

        if(geometryShaderSource != null)
            gl.glAttachShader(id, sGeometry);

        gl.glLinkProgram(id);
        checkCompileErrors(id, "PROGRAM");

        gl.glDeleteShader(sVertex);
        gl.glDeleteShader(sFragment);

        if(geometryShaderSource != null)
            gl.glDeleteShader(sGeometry);
    }

    private void checkCompileErrors(int obj, String type){
        IntBuffer success = IntBuffer.allocate(1);
        ByteBuffer infoLog = ByteBuffer.allocate(1024);

        if(type != "PROGRAM"){
            gl.glGetShaderiv(obj, GL3.GL_COMPILE_STATUS, success);
            if(success.get(0) <= 0){
                gl.glGetShaderInfoLog(obj, 1024, null, infoLog);
                System.out.println("| ERROR::SHADER: Compile-time error: Type: " + type + "\n"
                        + infoLog + "\n -- --------------------------------------------------- -- ");
            }
        }
        else {
            gl.glGetProgramiv(obj, GL3.GL_LINK_STATUS, success);
            if(success.get(0) <= 0){
                gl.glGetProgramInfoLog(obj, 1024, null, infoLog);
                System.out.println("| ERROR::SHADER: Link-time error: Type: " + type + "\n"
                        + infoLog + "\n -- --------------------------------------------------- -- ");
            }
        }
    }

}
