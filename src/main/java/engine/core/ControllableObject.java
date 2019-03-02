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

    public ControllableObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim, int id,
                              float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, id, frameSizeX, frameSizeY, animationSet);

        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    protected ControllableObject(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim,
                                 int id,
                                 float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.jumpState = false;
    }

    @Override
    public abstract void reactToCollision(BoundingBox anotherBox);

    @Override
    public abstract void update(float deltaTime);

    @Override
    public abstract void keyTyped(KeyEvent e);

    @Override
    public abstract void keyPressed(KeyEvent e);

    @Override
    public abstract void keyReleased(KeyEvent e);

    @Override
    public String toString(){
        return super.toString() + "\n Controllable";
    }
}
