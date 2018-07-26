package engine;

import com.jogamp.opengl.GL3;
import engine.collision.BoundingBox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class ControllableObject extends OpenGlObject implements Controllable {

    protected float velocityX;
    protected float velocityY;
    protected float velocityCollX;
    protected float velocityCollY;

    public ControllableObject(int bufferParamsCount, int verticesCount, GL3 gl, Dimension boxDim) {
        super(bufferParamsCount, verticesCount, gl, boxDim);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.velocityCollX = 0.0f;
        this.velocityCollY = 0.0f;
    }

    protected ControllableObject(int bufferParamsCount, int verticesCount, GL3 gl, float posX, float posY, Dimension boxDim) {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);
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
