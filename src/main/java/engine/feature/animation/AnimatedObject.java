package engine.feature.animation;

import com.jogamp.opengl.GL4;
import engine.core.OpenGlObject;
import engine.feature.shader.Shader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class AnimatedObject extends OpenGlObject {

    protected ArrayList<BasicAnimation> animations;
    protected BasicAnimation currentAnim;

    private static String emptyAnimationSetMessage = "Must be at least 1 animation!";

    private float frameSizeX;
    private float frameSizeY;

    public AnimatedObject(final int bufferParamsCount, final int verticesCount, final GL4 gl, final Dimension boxDim, final int id,
                          final float frameSizeX, final float frameSizeY, final BasicAnimation... animationSet) throws Exception {

        super(bufferParamsCount, verticesCount, gl, boxDim, id);

        if (animationSet == null)
            throw new Exception(emptyAnimationSetMessage);

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    public AnimatedObject(final int bufferParamsCount, final int verticesCount, final GL4 gl,
                          final float posX, final float posY, Dimension boxDim, final int id,
                          final float frameSizeX, final float frameSizeY, final BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id);

        if (animationSet == null)
            throw new Exception(emptyAnimationSetMessage);

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    @Override
    public void draw(final float x, final float y, final float xSize, final float ySize, final float rotationAngle, final Shader shader) {
        shader.use();
        defineAnimationVariables(shader);
        super.draw(x, y, xSize, ySize, rotationAngle, shader);
    }

    @Override
    public void draw(final float xSize, final float ySize, final float rotationAngle, final Shader shader) {
        shader.use();
        defineAnimationVariables(shader);
        super.draw(xSize, ySize, rotationAngle, shader);
    }

    private void defineAnimationVariables(final Shader shader) {
        if (this.texture != null || this.textureArray != null) {
            shader.setFloat("xChanging", currentAnim.currentFrameX * frameSizeX, false);
            shader.setInteger("frameNumberX", currentAnim.currentFrameX + 1, false);
            shader.setFloat("yChanging", currentAnim.currentFrameY * frameSizeY, false);
            shader.setInteger("frameY", currentAnim.currentFrameY + 1, false);
        }
    }

    protected void playAnimation(final float deltaTime) {
        this.currentAnim.changeFrame(deltaTime);
    }

    protected void setAnimation(final BasicAnimation a) {
        if (animations.contains(a))
            this.currentAnim = a;
    }

    @Override
    public String toString(){
        return super.toString() + "\n Animated";
    }

}
