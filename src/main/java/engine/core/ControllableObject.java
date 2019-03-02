package engine.core;

import com.jogamp.opengl.GL4;
import engine.feature.animation.AnimatedObject;
import engine.feature.animation.BasicAnimation;
import engine.feature.collision.BoundingBox;
import engine.feature.collision.collider.SimpleCollider;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class ControllableObject extends AnimatedObject implements Controllable, SimpleCollider {

    protected float velocityX;
    protected float velocityY;
    protected boolean jumpState;

    public ControllableObject(final int bufferParamsCount, final int verticesCount, final GL4 gl, final Dimension boxDim, final int id,
                              final float frameSizeX, final float frameSizeY, final BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, id, frameSizeX, frameSizeY, animationSet);

        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    protected ControllableObject(final int bufferParamsCount, final int verticesCount, final GL4 gl, final float posX, final float posY,
                                 final Dimension boxDim, final int id,
                                 final float frameSizeX, final float frameSizeY, final BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    @Override
    public abstract void reactToCollision(final BoundingBox anotherBox);

    @Override
    public abstract void update(float deltaTime);

    @Override
    public abstract void keyTyped(final KeyEvent e);

    @Override
    public abstract void keyPressed(final KeyEvent e);

    @Override
    public abstract void keyReleased(final KeyEvent e);

    @Override
    public String toString(){
        return super.toString() + "\n Controllable";
    }
}
