package engine.core;

import com.jogamp.opengl.GL4;
import engine.feature.animation.AnimatedObject;
import engine.feature.animation.BasicAnimation;
import engine.feature.collision.BoundingBox;
import engine.feature.collision.collider.SimpleCollider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class ControllableObject extends AnimatedObject implements Controllable, SimpleCollider {

    protected float velocityX;
    protected float velocityY;
    protected boolean jumpState;

    public ControllableObject(final int bufferParamsCount,
                              final int verticesCount,
                              @NotNull final GL4 gl,
                              @NotNull final Dimension boxDim,
                              final int id,
                              final float frameSizeX,
                              final float frameSizeY,
                              @NotNull final BasicAnimation... animationSet) throws Exception {

        super(bufferParamsCount, verticesCount, gl, boxDim, id, frameSizeX, frameSizeY, animationSet);

        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    protected ControllableObject(final int bufferParamsCount,
                                 final int verticesCount,
                                 @NotNull final GL4 gl,
                                 final float posX,
                                 final float posY,
                                 @NotNull final Dimension boxDim,
                                 final int id,
                                 final float frameSizeX,
                                 final float frameSizeY,
                                 @NotNull final BasicAnimation... animationSet) throws Exception {

        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);

        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    @Override
    public abstract void reactToCollision(@NotNull final BoundingBox anotherBox);

    @Override
    public abstract void keyTyped(@NotNull final KeyEvent e);

    @Override
    public abstract void keyPressed(@NotNull final KeyEvent e);

    @Override
    public abstract void keyReleased(@NotNull final KeyEvent e);

    @NotNull
    @Override
    public String toString(){
        return super.toString() + "\n Controllable";
    }
}
