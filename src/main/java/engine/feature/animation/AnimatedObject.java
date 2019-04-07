package engine.feature.animation;

import com.jogamp.opengl.GL4;
import engine.core.OpenGlObject;
import engine.feature.shader.Shader;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class AnimatedObject extends OpenGlObject {

    @NotNull protected ArrayList<BasicAnimation> animations;
    @NotNull protected BasicAnimation currentAnim;

    private float frameSizeX;
    private float frameSizeY;

    public AnimatedObject(final int bufferParamsCount,
                          final int verticesCount,
                          @NotNull final GL4 gl,
                          @NotNull final Dimension boxDim,
                          final int id,
                          final float frameSizeX,
                          final float frameSizeY,
                          @NotNull final BasicAnimation... animationSet) {

        super(bufferParamsCount, verticesCount, gl, boxDim, id);

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    public AnimatedObject(final int bufferParamsCount,
                          final int verticesCount,
                          @NotNull final GL4 gl,
                          final float posX,
                          final float posY,
                          @NotNull final Dimension boxDim,
                          final int id,
                          final float frameSizeX,
                          final float frameSizeY,
                          @NotNull final BasicAnimation... animationSet) {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id);

        this.frameSizeX = frameSizeX;
        this.frameSizeY = frameSizeY;
        this.animations = new ArrayList<>();

        Collections.addAll(this.animations, animationSet);
        this.currentAnim = this.animations.get(0);
    }

    @Override
    public void draw(final float x,
                     final float y,
                     final float xSize,
                     final float ySize,
                     final float rotationAngle,
                     @NotNull final Shader shader) {
        shader.use();
        defineAnimationVariables(shader);
        super.draw(x, y, xSize, ySize, rotationAngle, shader);
    }

    @Override
    public void draw(final float xSize,
                     final float ySize,
                     final float rotationAngle,
                     @NotNull final Shader shader) {
        shader.use();
        defineAnimationVariables(shader);
        super.draw(xSize, ySize, rotationAngle, shader);
    }

    private void defineAnimationVariables(@NotNull final Shader shader) {
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

    protected void setAnimation(@NotNull final BasicAnimation a) {
        if (animations.contains(a))
            this.currentAnim = a;
    }

    @NotNull
    @Override
    public String toString(){
        return super.toString() + "\n Animated";
    }

}
