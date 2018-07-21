package engine;

import com.jogamp.opengl.GL3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ControllableObject extends OpenGlObject implements Controllable {

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

    public ControllableObject(int bufferParamsCount, int verticesCount, GL3 gl, float posX, float posY, Dimension boxDim) {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.velocityCollX = 0.0f;
        this.velocityCollY = 0.0f;

    }

    protected void reactToCollision(BoundingBox anotherBox) {
    }

    public void collide(BoundingBox anotherBox) {
        this.reactToCollision(anotherBox);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
