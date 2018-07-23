package engine;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import engine.shader.Shader;
import engine.texture.TextureLoader;

import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;

//TODO: implement adaptive resizing of object dimension according to texture size
public class TexturedObject extends OpenGlObject implements Textured {

    protected Texture texture;
    private String textureFilePath;

    public TexturedObject(int bufferParamsCount, int verticesCount, GL3 gl, Dimension boxDim, String textureFilePath) {
        super(bufferParamsCount, verticesCount, gl, boxDim);
        this.textureFilePath = textureFilePath;
    }

    public TexturedObject(int bufferParamsCount, int verticesCount, GL3 gl, float posX, float posY, Dimension boxDim, String textureFilePath) {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);
        this.textureFilePath = textureFilePath;
    }

    @Override
    public void loadTexture(String filePath) {
        try {
            this.texture = TextureLoader.loadTexture(filePath);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
            texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initRenderData(float[]... dataArrays) {
        addBuffers(dataArrays);
        genVertexArray();
        loadTexture(this.textureFilePath);
    }

    @Override
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

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        this.texture.enable(gl);
        this.texture.bind(gl);

        shader.setMatrix4f("model", model, true);
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSample"), 0);
        //System.out.println(gl.glGetError() + " draw0");

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        //System.out.println(gl.glGetError() + " draw1");
    }

    @Override
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

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        this.texture.enable(gl);
        this.texture.bind(gl);

        shader.setMatrix4f("model", model, true);
        gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "texture"), 0);
        //System.out.println(gl.glGetError() + " draw0");

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.verticesCount);
        //System.out.println(gl.glGetError() + " draw1");
    }

}
