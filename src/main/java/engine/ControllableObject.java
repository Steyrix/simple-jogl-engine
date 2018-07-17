package engine;

import com.jogamp.opengl.GL3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ControllableObject extends OpenGlObject implements Controllable {

    private float velocityX;
    private float velocityY;
    private float velocityCollX;
    private float velocityCollY;

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

    private void reactToCollision(BoundingBox anotherBox) {
        if (intersects(anotherBox)) {
            if (this.velocityX != 0.0f && this.velocityY != 0.0f) {
                if (this.velocityX > 0.0f)
                    this.posX = anotherBox.posX - this.width + this.velocityX / this.velocityY;
                else
                    this.posX = anotherBox.getWidthX() - this.velocityX / this.velocityY;

                if (this.velocityY > 0.0f)
                    this.posY = anotherBox.posY - this.height + this.velocityY / this.velocityX;
                else
                    this.posY = anotherBox.getHeightY() - this.velocityY / this.velocityX;

                this.velocityCollX = -1.0f * this.velocityX * 0.2f;
                this.velocityX = 0.0f;
                this.velocityCollY = -1.0f * this.velocityY * 0.2f;
                this.velocityY = 0.0f;

            } else if (this.velocityX != 0.0f) {
                if (this.velocityX > 0.0f)
                    this.posX = anotherBox.posX - this.width;
                else
                    this.posX = anotherBox.getWidthX();

                this.velocityCollX = -1.0f * this.velocityX * 0.2f;
                this.velocityX = 0.0f;

            } else if (this.velocityY != 0.0f) {
                if (this.velocityY > 0.0f)
                    this.posY = anotherBox.posY - this.height;
                else
                    this.posY = anotherBox.getHeightY();

                this.velocityCollY = -1.0f * this.velocityY * 0.2f;
                this.velocityY = 0.0f;
            }
        }

    }

    public void collide(BoundingBox anotherBox) {
        this.reactToCollision(anotherBox);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.posX += this.velocityX + this.velocityCollX;
        if (velocityX >= 0.0f && velocityX - 1.0f >= 0.0f)
            velocityX -= 1.0f;
        else if (velocityX < 0.0f && velocityX + 1.0f <= 0.0f)
            velocityX += 1.0f;
        else if(velocityX >= 0.0f && velocityX - 1.0f < 0.0f ||
                velocityX < 0.0f && velocityX + 1.0f > 0.0f)
            velocityX = 0.0f;

        if (velocityCollX >= 0.0f && velocityCollX - 0.1f >= 0.0f)
            velocityCollX -= 0.1f;
        else if (velocityCollX <= 0.0f && velocityCollX + 0.1f <= 0.0f)
            velocityCollX += 0.1f;
        else if(velocityCollX >= 0.0f && velocityCollX - 0.1f < 0.0f ||
                velocityCollX < 0.0f && velocityCollX + 0.1f > 0.0f)
            velocityCollX = 0.0f;

        this.posY += this.velocityY + this.velocityCollY;
        if (velocityY >= 0.0f && velocityY - 1.0f >= 0.0f)
            velocityY -= 1.0f;
        else if (velocityY <= 0.0f && velocityY + 1.0f <= 0.0f)
            velocityY += 1.0f;
        else if(velocityY >= 0.0f && velocityY - 1.0f < 0.0f ||
                velocityY < 0.0f && velocityY + 1.0f > 0.0f)
            velocityY = 0.0f;

        if (velocityCollY >= 0.0f && velocityCollY - 0.1f >= 0.0f)
            velocityCollY -= 0.1f;
        else if (velocityCollY <= 0.0f && velocityCollY + 0.1f <= 0.0f)
            velocityCollY += 0.1f;
        else if(velocityCollY >= 0.0f && velocityCollY - 0.1f < 0.0f ||
                velocityCollY < 0.0f && velocityCollY + 0.1f > 0.0f)
            velocityCollY = 0.0f;


        System.out.println("Pos: " + posX + "; " + posY + "\nVelocity: " + velocityX + "; " + velocityY + "\n \n");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PRESSED");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_D:
                this.velocityX += 10.0f;
                break;
            case KeyEvent.VK_A:
                this.velocityX -= 10.0f;
                break;
            case KeyEvent.VK_W:
                this.velocityY -= 10.0f;
                break;
            case KeyEvent.VK_S:
                this.velocityY += 10.0f;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
