package engine;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import engine.shaderutil.Shader;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class OpenGlObject {

    private final GL3 gl;

    private int buffersFilled;
    private int buffersCount;
    private int verticesCount;
    private IntBuffer buffers;
    private IntBuffer vertexArray;

    private ArrayList<Integer> paramsCount;

    //BufferParamsCount states for number of buffers the object will have (i.e. vertices, colors and etc.)
    public OpenGlObject(int bufferParamsCount, int verticesCount, GL3 gl) {
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.vertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();
    }

    public void initRenderData(float[]... dataArrays){
        addBuffers(dataArrays);
        genVertexArray();
    }

    public void addBuffers(float[]... dataArrays){
        gl.glGenBuffers(buffersCount,buffers);
        for(float[] fData : dataArrays) {
            FloatBuffer floatBuffer = FloatBuffer.wrap(fData);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, this.buffers.get(buffersFilled++));
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, 4 * fData.length, floatBuffer, GL3.GL_STATIC_DRAW);

            paramsCount.add(fData.length / this.verticesCount);
        }
        System.out.println(gl.glGetError() + " addBuffers");
    }

    private void genVertexArray(){
        gl.glGenVertexArrays(1, this.vertexArray);
        gl.glBindVertexArray(this.vertexArray.get(0));

        for(int i = 0; i < this.buffersFilled; i++){
            gl.glEnableVertexAttribArray(i);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffers.get(i));
            gl.glVertexAttribPointer(i, this.paramsCount.get(i), GL.GL_FLOAT, false, 0, 0);
        }

        System.out.println(gl.glGetError() + " genVertexArray");
    }

    public void draw(float x, float y, float xSize, float ySize, float rotationAngle, Shader shader){
        shader.use();

        Mat4 model = Mat4.MAT4_IDENTITY;
        Mat4 rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f,0.0f,1.0f));
        Mat4 scale = new Mat4(xSize, 0.0f, 0.0f, 0.0f,
                0.0f, ySize, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);


        model = model.translate(new Vec3(x,y,0.0f));
        model = model.translate(new Vec3(0.5f * xSize, 0.5f * ySize, 0.0f));
        model = model.multiply(rotation);
        model = model.translate(new Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f));

        model = model.multiply(scale);

        shader.setMatrix4f("model", model, false);
        System.out.println(gl.glGetError() + " draw0");

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        System.out.println(gl.glGetError() + " draw1");
    }

}
