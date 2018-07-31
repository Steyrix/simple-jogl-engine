package engine.core;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import engine.collision.BoundingBox;
import engine.shader.Shader;
import engine.texture.TextureLoader;
import engine.texture.Textured;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class OpenGlObject extends BoundingBox implements Textured {

    protected final GL3 gl;

    protected int buffersFilled;
    protected int buffersCount;
    protected int verticesCount;

    protected IntBuffer buffers;
    protected IntBuffer vertexArray;

    protected ArrayList<Integer> paramsCount;

    protected Texture texture;

    public OpenGlObject(int bufferParamsCount, int verticesCount, GL3 gl, Dimension boxDim) {
        super(0.0f, 0.0f, boxDim.width, boxDim.height);
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.vertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();

        this.texture = null;
    }

    public OpenGlObject(int bufferParamsCount, int verticesCount, GL3 gl, float posX, float posY, Dimension boxDim) {
        super(posX, posY, boxDim.width, boxDim.height);
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.vertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();

        this.posX = posX;
        this.posY = posY;

        this.texture = null;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    @Override
    public void loadTexture(String filePath) {
        try {
            this.texture = TextureLoader.loadTexture(filePath);

            texture.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRenderData(String textureFilePath, float[]... dataArrays) {
        addBuffers(dataArrays);
        genVertexArray();
        if (textureFilePath != null)
            loadTexture(textureFilePath);
    }

    protected void addBuffers(float[]... dataArrays) {
        gl.glGenBuffers(buffersCount, buffers);
        for (float[] fData : dataArrays) {
            FloatBuffer floatBuffer = FloatBuffer.wrap(fData);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, this.buffers.get(buffersFilled++));
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, 4 * fData.length, floatBuffer, GL3.GL_STATIC_DRAW);

            paramsCount.add(fData.length / this.verticesCount);
        }

        //System.out.println(gl.glGetError() + " addBuffers");
    }

    protected void genVertexArray() {
        gl.glGenVertexArrays(1, this.vertexArray);
        gl.glBindVertexArray(this.vertexArray.get(0));

        for (int i = 0; i < this.buffersFilled; i++) {
            gl.glEnableVertexAttribArray(i);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffers.get(i));
            gl.glVertexAttribPointer(i, this.paramsCount.get(i), GL.GL_FLOAT, false, 0, 0);
        }

        //System.out.println(gl.glGetError() + " genVertexArray");
    }

    public void dispose() {
        gl.glDeleteBuffers(buffersCount, buffers);
        gl.glDeleteVertexArrays(1, this.vertexArray);
        if (this.texture != null)
            this.texture.destroy(gl);
    }

    public void draw(float x, float y, float xSize, float ySize, float rotationAngle, Shader shader) {
        this.width = xSize;
        this.height = ySize;

        Mat4 model = Mat4.MAT4_IDENTITY;
        Mat4 rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        Mat4 scale = new Mat4(xSize, 0.0f, 0.0f, 0.0f,
                0.0f, ySize, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);


        model = model.translate(new Vec3(x, y, 0.0f));
        model = model.translate(new Vec3(0.5f * xSize, 0.5f * ySize, 0.0f));
        model = model.multiply(rotation);
        model = model.translate(new Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f));

        model = model.multiply(scale);

        if (this.texture != null) {
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            this.texture.enable(gl);
            this.texture.bind(gl);
            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSample"), 0);
        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        //System.out.println(gl.glGetError() + " draw1");
    }

    public void draw(float xSize, float ySize, float rotationAngle, Shader shader) {
        this.width = xSize;
        this.height = ySize;

        Mat4 model = Mat4.MAT4_IDENTITY;
        Mat4 rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        Mat4 scale = new Mat4(xSize, 0.0f, 0.0f, 0.0f,
                0.0f, ySize, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);


        model = model.translate(new Vec3(this.posX, this.posY, 0.0f));
        model = model.translate(new Vec3(0.5f * xSize, 0.5f * ySize, 0.0f));
        model = model.multiply(rotation);
        model = model.translate(new Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f));

        model = model.multiply(scale);

        if (this.texture != null) {
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            this.texture.enable(gl);
            this.texture.bind(gl);
            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSample"), 0);
        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
    }
}
