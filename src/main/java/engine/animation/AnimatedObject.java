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

public class AnimatedObject extends OpenGlObject {

    protected ArrayList<BasicAnimation> animations;
    protected BasicAnimation currentAnim;

    private float frameSizeX;
    private float frameSizeY;

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim, int id,
                          float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {

        super(bufferParamsCount, verticesCount, gl, boxDim, id);

        if (animationSet == null)
            throw new Exception("Must be atleast 1 animation!");

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    public AnimatedObject(int bufferParamsCount, int verticesCount, GL4 gl,
                          float posX, float posY, Dimension boxDim, int id,
                          float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id);

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

        defineAnimationVariables(shader);

        super.draw(x, y, xSize, ySize, rotationAngle, shader);

    }

    @Override
    public void draw(float xSize, float ySize, float rotationAngle, Shader shader) {

        shader.use();

        defineAnimationVariables(shader);

        super.draw(xSize, ySize, rotationAngle, shader);
    }

    private void defineAnimationVariables(Shader shader) {
        if (this.texture != null || this.textureArray != null) {
            shader.setFloat("xChanging", currentAnim.currentFrameX * frameSizeX, false);
            shader.setInteger("frameNumberX", currentAnim.currentFrameX + 1, false);
            shader.setFloat("yChanging", currentAnim.currentFrameY * frameSizeY, false);
            shader.setInteger("frameY", currentAnim.currentFrameY + 1, false);
        }
    }

    protected void playAnimation() {
        this.currentAnim.changeFrame();
    }

    protected void setAnimation(BasicAnimation a) {
        if (animations.contains(a))
            this.currentAnim = a;
    }

}
