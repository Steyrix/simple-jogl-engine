package engine.animation;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL4;
import engine.core.OpenGlObject;
import engine.shader.Shader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


//TODO: make animation more fancy
public class AnimatedObject extends OpenGlObject {

    protected ArrayList<BasicAnimation> animations;
    protected BasicAnimation currentAnim;

    private float frameSizeX;
    private float frameSizeY;

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim,
                          float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {

        super(bufferParamsCount, verticesCount, gl, boxDim);

        if (animationSet == null)
            throw new Exception("Must be atleast 1 animation!");

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl,
                          float posX, float posY, Dimension boxDim,
                          float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);

        if (animationSet == null)
            throw new Exception("Must be atleast 1 animation!");

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
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

            shader.setFloat("xChanging", currentAnim.currentFrameX * frameSizeX, false);
            shader.setInteger("frameNumberX", currentAnim.currentFrameX + 1, false);
            shader.setFloat("yChanging", currentAnim.currentFrameY * frameSizeY, false);
            shader.setInteger("frameY", currentAnim.currentFrameY + 1, false);


            if (this.texture != null) {
                gl.glActiveTexture(GL4.GL_TEXTURE0);
                this.texture.enable(gl);
                this.texture.bind(gl);
                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureSampler"), 0);
            } else {
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

            shader.setFloat("xChanging", currentAnim.currentFrameX * frameSizeX, false);
            shader.setInteger("frameNumberX", currentAnim.currentFrameX + 1, false);
            shader.setFloat("yChanging", currentAnim.currentFrameY * frameSizeY, false);
            shader.setInteger("frameY", currentAnim.currentFrameY + 1, false);


            if (this.texture != null) {
                gl.glActiveTexture(GL4.GL_TEXTURE0);
                this.texture.enable(gl);
                this.texture.bind(gl);
                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureAtlas"), 0);
            } else {
                gl.glActiveTexture(GL4.GL_TEXTURE0);

                gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, this.textureArray.get(0));

                gl.glUniform1i(gl.glGetUniformLocation(shader.getId(), "textureArray"), 0);
            }

        }

        shader.setMatrix4f("model", model, true);

        gl.glBindVertexArray(this.vertexArray.get(0));
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, this.verticesCount);

    }

    protected void playAnimation(float deltaTime) {
        this.currentAnim.changeFrame();
    }

    protected void setAnimation(BasicAnimation a) {
        if (animations.contains(a))
            this.currentAnim = a;
    }

    protected void changeAnimation() {
    }
}
