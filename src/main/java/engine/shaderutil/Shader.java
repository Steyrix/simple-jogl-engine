package engine.shaderutil;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
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
        gl.glUseProgram(this.id);
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

        this.id = gl.glCreateProgram();
        gl.glAttachShader(this.id, sVertex);
        gl.glAttachShader(this.id, sFragment);

        if(geometryShaderSource != null)
            gl.glAttachShader(this.id, sGeometry);

        gl.glLinkProgram(this.id);
        checkCompileErrors(this.id, "PROGRAM");

        gl.glDeleteShader(sVertex);
        gl.glDeleteShader(sFragment);

        if(geometryShaderSource != null)
            gl.glDeleteShader(sGeometry);
    }

    public void setFloat(String name, float value, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform1f(gl.glGetUniformLocation(this.id, name), value);
    }

    public void setInteger(String name, int value, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform1i(gl.glGetUniformLocation(this.id, name), value);
    }

    public void setVector2f(String name, float x, float y, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform2f(gl.glGetUniformLocation(this.id, name), x, y);
    }

    public void setVector3f(String name, float x, float y, float z, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform3f(gl.glGetUniformLocation(this.id, name), x, y, z);
    }

    public void setVector3f(String name, Vec3 value, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform3f(gl.glGetUniformLocation(this.id, name), value.getX(), value.getY(), value.getZ());
    }

    public void setVector4f(String name, float x, float y, float z, float w, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform4f(gl.glGetUniformLocation(this.id, name), x, y, z, w);
    }

    public void setVector4f(String name, Vec4 value, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniform4f(gl.glGetUniformLocation(this.id, name), value.getX(), value.getY(), value.getZ(), value.getW());
    }

    public void setMatrix4f(String name, Mat4 value, boolean useShader){
        if(useShader)
            this.use();
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(this.id, name), 1, false, value.getBuffer());
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
