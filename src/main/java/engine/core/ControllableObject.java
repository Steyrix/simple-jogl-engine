package engine.core;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import engine.animation.AnimatedObject;
import engine.animation.BasicAnimation;
import engine.collision.BoundingBox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class ControllableObject extends AnimatedObject implements Controllable {

    protected float velocityX;
    protected float velocityY;
    protected float velocityCollX;
    protected float velocityCollY;

    public ControllableObject(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim,
                              float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, frameSizeX, frameSizeY, animationSet);

        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.velocityCollX = 0.0f;
        this.velocityCollY = 0.0f;
    }

    protected ControllableObject(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim,
                                 float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, frameSizeX, frameSizeY, animationSet);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.velocityCollX = 0.0f;
        this.velocityCollY = 0.0f;

    }

    protected abstract void reactToCollision(BoundingBox anotherBox);

    public void collide(BoundingBox anotherBox) {
        this.reactToCollision(anotherBox);
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);

    @Override
    public abstract void keyTyped(KeyEvent e);

    @Override
    public abstract void keyPressed(KeyEvent e);

    @Override
    public abstract void keyReleased(KeyEvent e);
}
