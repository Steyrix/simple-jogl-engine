package engine.core;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import engine.feature.collision.BoundingBox;
import engine.feature.shader.Shader;
import engine.feature.texture.TextureLoader;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class OpenGlObject extends BoundingBox {

    protected final GL4 gl;
    protected Texture texture;
    protected IntBuffer textureArray;

    private final IntBuffer buffers;
    private final IntBuffer bbBuffer;
    private final ArrayList<Integer> paramsCount;
    private int buffersFilled;
    private final int buffersCount;

    private final int verticesCount;
    private final IntBuffer vertexArray;
    private final IntBuffer bbVertexArray;

    private final int textureId;
    private String uniformName;


    public OpenGlObject(final int bufferParamsCount, final int verticesCount, final GL4 gl, final Dimension boxDim, final int textureId) {
        super(0.0f, 0.0f, boxDim.width, boxDim.height);
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.bbBuffer = IntBuffer.allocate(1);
        this.vertexArray = IntBuffer.allocate(1);
        this.bbVertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();

        this.uniformName = null;
        this.texture = null;
        this.textureArray = null;
        this.textureId = textureId;
    }

    public OpenGlObject(final int bufferParamsCount, final int verticesCount, final GL4 gl,
                        final float posX, final float posY, final Dimension boxDim, final int textureId) {
        super(posX, posY, boxDim.width, boxDim.height);
        this.gl = gl;

        this.buffersFilled = 0;
        this.buffersCount = bufferParamsCount;
        this.verticesCount = verticesCount;
        this.buffers = IntBuffer.allocate(buffersCount);
        this.bbBuffer = IntBuffer.allocate(1);
        this.vertexArray = IntBuffer.allocate(1);
        this.bbVertexArray = IntBuffer.allocate(1);

        this.paramsCount = new ArrayList<>();

        this.posX = posX;
        this.posY = posY;

        this.uniformName = null;
        this.texture = null;
        this.textureArray = null;
        this.textureId = textureId;
    }

    private boolean isTextured() {
        return (this.texture != null || this.textureArray != null);
    }

    protected void loadTexture(final String filePath) {
        try {
            this.texture = TextureLoader.loadTexture(filePath);
            setTexParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTexParameters() {
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
        texture.setTexParameteri(gl, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
    }

    private void loadTextureArray(final String... filePaths) {
        try {
            ArrayList<TextureData> images = new ArrayList<>();
            var tl = new TextureLoader();
            int width, height;

            for (String path : filePaths) {
                images.add(tl.loadTextureData(path, gl));
            }

            width = images.get(0).getWidth();
            height = images.get(0).getHeight();

            for (TextureData td : images) {
                System.out.println(td.getBuffer().toString());
            }

            this.textureArray = TextureLoader.loadTextureArrayTD(images, gl, width, height, false, textureId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRenderData(final String[] textureFilePaths, final boolean texArray, final float[]... dataArrays) {
        addBuffers(dataArrays);
        genVertexArray();

        initBoundingBoxBuffer();
        genBoundingBoxVertexArray();

        if (textureFilePaths != null && textureFilePaths.length == 1 && !texArray) {
            System.out.println("Loading only single texture");
            loadTexture(textureFilePaths[0]);
        }
        if ((textureFilePaths != null && textureFilePaths.length > 1) || texArray) {
            System.out.println("Loading texture array");
            loadTextureArray(textureFilePaths);
        }
    }

    public void initRenderData(final Texture texture, final float[]... dataArrays) {
        addBuffers(dataArrays);
        genVertexArray();

        initBoundingBoxBuffer();
        genBoundingBoxVertexArray();

        if (texture != null) {
            System.out.println("Loading only single texture");
            this.texture = texture;
            setTexParameters();
        }
    }

    private void addBuffers(final float[]... dataArrays) {
        if(dataArrays.length != buffersCount)
            throw new IllegalArgumentException("Number of buffers supplied must be the number of buffers created for the object");

        gl.glGenBuffers(buffersCount, buffers);
        for (float[] fData : dataArrays) {
            FloatBuffer floatBuffer = FloatBuffer.wrap(fData);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, this.buffers.get(buffersFilled++));
            gl.glBufferData(GL4.GL_ARRAY_BUFFER, 4 * fData.length, floatBuffer, GL4.GL_STATIC_DRAW);

            paramsCount.add(fData.length / this.verticesCount);
        }
    }

    private void genVertexArray() {
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

    public void draw(final float x, final float y, final float xSize, final float ySize, final float rotationAngle, final Shader shader) {

        shader.use();

        this.width = xSize;
        this.height = ySize;

        Mat4 model = getFinalMatrix(x, y, xSize, ySize, rotationAngle);

        defineTextureState(shader);

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        //System.out.println(gl.glGetError() + " draw1");

        drawBoundingBox();
    }

    public void draw(final float xSize, final float ySize, final float rotationAngle, final Shader shader) {

        shader.use();

        this.width = xSize;
        this.height = ySize;

        Mat4 model = getFinalMatrix(xSize, ySize, rotationAngle);

        defineTextureState(shader);

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);

        drawBoundingBox();
    }

    private void defineTextureState(final Shader shader) {
        if (this.texture != null) {
            this.setUniformName("textureSample");
            defineSingleTextureState(shader);
        }
        if (this.textureArray != null) {
            this.setUniformName("textureArray");
            defineArrayTextureState(shader);
        }
    }

    private void defineSingleTextureState(final Shader shader) {
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        this.texture.enable(gl);
        this.texture.bind(gl);
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), uniformName), 0);
    }

    private void defineArrayTextureState(final Shader shader) {
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, textureArray.get(0));
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), uniformName), 0);
    }

    private Mat4 getFinalMatrix(final float xSize, final float ySize, final float rotationAngle) {
        var model = Mat4.MAT4_IDENTITY;
        var rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        var scale = getScaleMatrix(xSize, ySize);

        model = model.translate(new Vec3(this.posX, this.posY, 0.0f));

        applyRotation(xSize, ySize, rotation, model);

        model = model.multiply(scale);

        return model;
    }

    private Mat4 getFinalMatrix(final float x, final float y, final float xSize, final float ySize, final float rotationAngle) {
        var model = Mat4.MAT4_IDENTITY;
        var rotation = Matrices.rotate(rotationAngle, new Vec3(0.0f, 0.0f, 1.0f));
        var scale = getScaleMatrix(xSize, ySize);

        model = model.translate(new Vec3(x, y, 0.0f));

        applyRotation(xSize, ySize, rotation, model);

        model = model.multiply(scale);

        return model;
    }

    private Mat4 getScaleMatrix(final float xSize, final float ySize) {
        return new Mat4(xSize, 0.0f, 0.0f, 0.0f,
                0.0f, ySize, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);


    }

    //TODO: move to separate class
    private void applyRotation(final float xSize, final float ySize, final Mat4 rotation, Mat4 model) {
        model = model.translate(new Vec3(0.5f * xSize, 0.5f * ySize, 0.0f));
        model = model.multiply(rotation);
        model = model.translate(new Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f));
    }

    //TODO: add ability to debug bounding boxes
    private void initBoundingBoxBuffer() {
        gl.glGenBuffers(1, bbBuffer);
        FloatBuffer bbVerticesBuffer = FloatBuffer.wrap(
                new float[]{

                        0f, 0f,
                        1f, 0f,
                        1f, 1f,
                        0f, 1f,
                });

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bbBuffer.get(0));
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, 32, bbVerticesBuffer, GL4.GL_STATIC_DRAW);
    }

    private void genBoundingBoxVertexArray() {
        gl.glGenVertexArrays(1, this.bbVertexArray);
        gl.glBindVertexArray(this.bbVertexArray.get(0));

        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bbBuffer.get(0));
        gl.glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 0, 0);
    }

    private void drawBoundingBox() {
        gl.glBindVertexArray(this.bbVertexArray.get(0));
        gl.glDrawArrays(GL4.GL_LINES, 0, 4);
    }

    private void setUniformName(final String newName) {
        this.uniformName = newName;
    }

    @Override
    public String toString() {
        return "OpenGlObject: \n Number of vertices: " + verticesCount +
                "\n Number of buffers: " + buffersCount + "\n" +
                (isTextured() ? (" Texture id: " + textureId) : " Not textured");
    }
}
