package engine.core;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import engine.animation.Animated;
import engine.shader.Shader;

import java.awt.*;


//TODO: make animation more fancy
public class AnimatedObject extends OpenGlObject implements Animated {

    private int framesCountX;
    private int framesCountY;
    private int currentFrameX;
    private int currentFrameY;
    private float frameSizeX;
    private float frameSizeY;

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim,
                          int framesCountX, int framesCountY,
                          float frameSizeX, float frameSizeY) {
        super(bufferParamsCount, verticesCount, gl, boxDim);
        this.framesCountX = framesCountX;
        this.framesCountY = framesCountY;
        this.currentFrameX = 1;
        this.currentFrameY = 1;
        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;

    }

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl,
                          float posX, float posY, Dimension boxDim,
                          int framesCountX, int framesCountY,
                          float frameSizeX, float frameSizeY) {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);
        this.framesCountX = framesCountX;
        this.framesCountY = framesCountY;
        this.currentFrameX = 1;
        this.currentFrameY = 1;
        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
    }

    @Override
    public void draw(float x, float y, float xSize, float ySize, float rotationAngle, Shader shader) {

        shader.use();

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

        if (this.texture != null || this.textureArray != null) {

            shader.setFloat("xChanging", currentFrameX * 0.0625f, false);
            shader.setInteger("frameNumberX", currentFrameX + 1, false);
            shader.setFloat("yChanging", currentFrameY * 0.2f, false);
            shader.setInteger("frameY", currentFrameY + 1, false);


            if(this.texture != null) {
                gl.glActiveTexture(GL4.GL_TEXTURE0);
                this.texture.enable(gl);
                this.texture.bind(gl);
                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSampler"), 0);
            }

            else {
                gl.glActiveTexture(GL4.GL_TEXTURE0);

                gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, this.textureArray.get(0));

                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureArray"), 0);
            }

        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);

    }

    @Override
    public void draw(float xSize, float ySize, float rotationAngle, Shader shader) {

        shader.use();

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

        if (this.texture != null || this.textureArray != null) {

            shader.setFloat("xChanging", currentFrameX * frameSizeX, false);
            shader.setInteger("frameNumberX", currentFrameX + 1, false);
            shader.setFloat("yChanging", currentFrameY * frameSizeY, false);
            shader.setInteger("frameY", currentFrameY + 1, false);


            if(this.texture != null) {
                gl.glActiveTexture(GL4.GL_TEXTURE0);
                this.texture.enable(gl);
                this.texture.bind(gl);
                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSampler"), 0);
            }

            else {
                gl.glActiveTexture(GL4.GL_TEXTURE0);

                gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, this.textureArray.get(0));

                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureArray"), 0);
            }

        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);

    }

    @Override
    public void changeFrame() {

        if(currentFrameX + 1 >= framesCountX) {
            currentFrameX = 0;

            if(currentFrameY + 1 >= framesCountY)
                currentFrameY = 0;
            else
                currentFrameY++;
        }

        else
            currentFrameX++;
    }
}
