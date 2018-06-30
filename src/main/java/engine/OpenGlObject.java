package engine;

import com.jogamp.openal.sound3d.Buffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.sun.prism.impl.BufferUtil;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class OpenGlObject implements Textured {

    private final GL3 gl;

    private final Texture texture;

    private int buffersFilled;
    private int buffersCount;
    private int verticesCount;
    private IntBuffer buffers;
    private IntBuffer vertexArray;

    private ArrayList<Integer> paramsCount;

    //BufferParamsCount states for number of buffers the object will have (i.e. vertices, colors and etc.)
    public OpenGlObject(String texPath, int bufferParamsCount, int verticesCount, GL3 gl) {
        this.gl = gl;
        if(texPath != null)
            this.texture = Textured.loadTexture(texPath);
        else texture = null;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.vertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();
    }

    public void addBuffers(float[]... dataArrays){
        gl.glGenBuffers(buffersCount,buffers);
        for(float[] fData : dataArrays) {
            FloatBuffer floatBuffer = FloatBuffer.wrap(fData);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffers.get(buffersFilled++));
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, 4 * fData.length, floatBuffer, GL3.GL_STATIC_DRAW);

            paramsCount.add(fData.length / verticesCount);
        }
        System.out.println(gl.glGetError() + " addBuffers");
        genVertexArray();
    }

    public void genVertexArray(){
        gl.glGenVertexArrays(1, vertexArray);
        gl.glBindVertexArray(vertexArray.get(0));

        for(int i = 0; i < buffersFilled; i++){
            gl.glEnableVertexAttribArray(i);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffers.get(i));
            gl.glVertexAttribPointer(i, paramsCount.get(i), GL.GL_FLOAT, false, 0, 0);
        }

        System.out.println(gl.glGetError() + " genVertexArray");
    }

    public void draw(){
        System.out.println(gl.glGetError() + " draw0");

        gl.glBindVertexArray(vertexArray.get(0));
        System.out.println(gl.glGetError() + " draw1");

        gl.glDrawArrays(GL.GL_TRIANGLES, 0, verticesCount);
        System.out.println(gl.glGetError() + " draw2");
    }
}
