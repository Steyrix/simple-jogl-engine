package engine.core;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import engine.collision.BoundingBox;
import engine.shader.Shader;
import engine.texture.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class OpenGlObject extends BoundingBox {

    protected final GL4 gl;

    private IntBuffer buffers;
    private ArrayList<Integer> paramsCount;
    private int buffersFilled;
    private int buffersCount;

    protected int verticesCount;
    protected IntBuffer vertexArray;

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
        return this.texture != null || this.textureArray != null;
    }

    protected void loadTexture(String filePath) {
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

    protected void loadTextureArray(String... filePaths) {
        try {
            ArrayList<TextureData> images = new ArrayList<>();
            ArrayList<BufferedImage> buffImages = new ArrayList<>();

            var tl = new TextureLoader();
            int width, height = 0;
            for (String path : filePaths) {
                images.add(tl.loadTextureData(path,gl));
                buffImages.add(ImageIO.read(new File(path)));
            }

            width = images.get(0).getWidth();
            height = images.get(0).getHeight();

            for(TextureData td : images){
                System.out.println(td.getBuffer().toString());
            }

            this.textureArray = TextureLoader.loadTextureArray(buffImages, gl, width, height, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRenderData(String[] textureFilePaths, boolean texArray, float[]... dataArrays) {
        addBuffers(dataArrays);
        genVertexArray();
        if (textureFilePaths != null && textureFilePaths.length == 1 && !texArray) {
            System.out.println("Loading only single texture");
            loadTexture(textureFilePaths[0]);
        }
        if ((textureFilePaths != null && textureFilePaths.length > 1) || texArray) {
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
    }

    protected void genVertexArray() {
        gl.glGenVertexArrays(1, this.vertexArray);
        gl.glBindVertexArray(this.vertexArray.get(0));

        for (int i = 0; i < this.buffersFilled; i++) {
            gl.glEnableVertexAttribArray(i);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(i));
            gl.glVertexAttribPointer(i, this.paramsCount.get(i), GL4.GL_FLOAT, false, 0, 0);
        }
    }

    public void dispose() {
        gl.glDeleteBuffers(buffersCount, buffers);
        gl.glDeleteVertexArrays(1, this.vertexArray);
        if (this.texture != null)
            this.texture.destroy(gl);

    }

    public void draw(float x, float y, float xSize, float ySize, float rotationAngle, Shader shader) {

        shader.use();

        this.width = xSize;
        this.height = ySize;

        Mat4 model = getFinalMatrix(x, y, xSize, ySize, rotationAngle);

        if(this.texture != null)
            defineSingleTextureState(shader, "textureSample");
        if(this.textureArray != null)
            defineArrayTextureState(shader, "textureArray");

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        //System.out.println(gl.glGetError() + " draw1");
    }

    public void draw(float xSize, float ySize, float rotationAngle, Shader shader) {

        shader.use();

        this.width = xSize;
        this.height = ySize;

        Mat4 model = getFinalMatrix(xSize, ySize, rotationAngle);

        if(this.texture != null)
            defineSingleTextureState(shader, "textureSample");
        if(this.textureArray != null)
            defineArrayTextureState(shader, "textureArray");

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);
    }

    private void defineSingleTextureState(Shader shader, String uniformName){
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        this.texture.enable(gl);
        this.texture.bind(gl);
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), uniformName), 0);
    }

    private void defineArrayTextureState(Shader shader, String uniformName){
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, textureArray.get(0));
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), uniformName), 0);
    }

    private Mat4 getFinalMatrix(float xSize, float ySize, float rotationAngle){
        var model = Mat4.MAT4_IDENTITY;
        var rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        var scale = getScaleMatrix(xSize, ySize);

        model = model.translate(new Vec3(this.posX, this.posY, 0.0f));

        applyRotation(xSize, ySize, rotation, model);

        model = model.multiply(scale);

        return model;
    }

    private Mat4 getFinalMatrix(float x, float y, float xSize, float ySize, float rotationAngle){
        var model = Mat4.MAT4_IDENTITY;
        var rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        var scale = getScaleMatrix(xSize, ySize);

        model = model.translate(new Vec3(x, y, 0.0f));

        applyRotation(xSize, ySize, rotation, model);

        model = model.multiply(scale);

        return model;
    }

    private Mat4 getScaleMatrix(float xSize, float ySize){
        return new Mat4(xSize, 0.0f, 0.0f, 0.0f,
                0.0f, ySize, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);


    }

    private void applyRotation (float xSize, float ySize, Mat4 rotation, Mat4 model){
        model = model.translate(new Vec3(0.5f * xSize, 0.5f * ySize, 0.0f));
        model = model.multiply(rotation);
        model = model.translate(new Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f));
    }
}
