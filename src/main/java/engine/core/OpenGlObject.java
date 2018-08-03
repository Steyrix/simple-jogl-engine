package engine.core;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import engine.collision.BoundingBox;
import engine.shader.Shader;
import engine.texture.TextureLoader;
import engine.texture.Textured;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class OpenGlObject extends BoundingBox implements Textured {

    protected final GL4 gl;

    protected int buffersFilled;
    protected int buffersCount;
    protected int verticesCount;

    protected IntBuffer buffers;
    protected IntBuffer vertexArray;

    protected ArrayList<Integer> paramsCount;

    protected Texture texture;
    protected IntBuffer textureArray;

    public OpenGlObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim) {
        super(0.0f, 0.0f, boxDim.width, boxDim.height);
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.vertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();

        this.texture = null;
        this.textureArray = null;
    }

    public OpenGlObject(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim) {
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
        this.textureArray = null;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    @Override
    public void loadTexture(String filePath) {
        try {
            this.texture = TextureLoader.loadTexture(filePath);

            texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
            texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadTextureArray(String... filePaths) {
        try {
            ArrayList<BufferedImage> images = new ArrayList<>();
            int width, height = 0;
            for(String path : filePaths)
                images.add(ImageIO.read(new File(path)));
            width = images.get(0).getWidth();
            height = images.get(0).getHeight();

            this.textureArray = TextureLoader.loadTextureArray(images, gl, width, height, false);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initRenderData(String[] textureFilePaths, boolean texArray, float[]... dataArrays) {
        System.out.println("initRenderData");
        addBuffers(dataArrays);
        genVertexArray();
        if (textureFilePaths != null && textureFilePaths.length == 1) {
            System.out.println("Loading only single texture");
            loadTexture(textureFilePaths[0]);
        }
        if ((textureFilePaths != null && textureFilePaths.length > 1) || texArray){
            System.out.println("Loading texture array");
            loadTextureArray(textureFilePaths);
        }
    }

    protected void addBuffers(float[]... dataArrays) {
        gl.glGenBuffers(buffersCount, buffers);
        for (float[] fData : dataArrays) {
            FloatBuffer floatBuffer = FloatBuffer.wrap(fData);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, this.buffers.get(buffersFilled++));
            gl.glBufferData(GL4.GL_ARRAY_BUFFER, 4 * fData.length, floatBuffer, GL4.GL_STATIC_DRAW);

            paramsCount.add(fData.length / this.verticesCount);
        }

        //System.out.println(gl.glGetError() + " addBuffers");
    }

    protected void genVertexArray() {
        gl.glGenVertexArrays(1, this.vertexArray);
        gl.glBindVertexArray(this.vertexArray.get(0));

        for (int i = 0; i < this.buffersFilled; i++) {
            gl.glEnableVertexAttribArray(i);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(i));
            gl.glVertexAttribPointer(i, this.paramsCount.get(i), GL4.GL_FLOAT, false, 0, 0);
        }

        //System.out.println(gl.glGetError() + " genVertexArray");
    }

    public void dispose() {
        gl.glDeleteBuffers(buffersCount, buffers);
        gl.glDeleteVertexArrays(1, this.vertexArray);
        if (this.texture != null)
            this.texture.destroy(gl);
    }


    //TODO: fix 1280 and 1282 glErrors when trying to draw Array Texture Object
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
            gl.glActiveTexture(GL4.GL_TEXTURE0);
            this.texture.enable(gl);
            this.texture.bind(gl);
            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSample"), 0);
        }

        if (this.textureArray != null){
            gl.glActiveTexture(GL4.GL_TEXTURE0);
            gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, this.textureArray.get(0));
            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureArray"), 0);
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
            gl.glActiveTexture(GL4.GL_TEXTURE0);
            this.texture.enable(gl);
            this.texture.bind(gl);
            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSample"), 0);
        }

        if (this.textureArray != null){
            gl.glActiveTexture(GL4.GL_TEXTURE0);

            gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, textureArray.get(0));

            gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureArray"), 0);
        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);
    }
}
