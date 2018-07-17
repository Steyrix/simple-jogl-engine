package engine;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import engine.Controllable;
import engine.OpenGlObject;
import engine.shaderutil.Shader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ControllableObject extends OpenGlObject implements Controllable{

    private float velocityX;
    private float velocityY;

    public ControllableObject(int bufferParamsCount, int verticesCount, GL3 gl, Dimension boxDim) {
        super(bufferParamsCount, verticesCount, gl, boxDim);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
    }

    public ControllableObject(int bufferParamsCount, int verticesCount, GL3 gl, float posX, float posY, Dimension boxDim){
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
    }

    private void reactToCollision(BoundingBox anotherBox){
        if(intersects(anotherBox)) {
            if(this.velocityX != 0.0f) {
                this.posX = anotherBox.posX - this.width;
                this.velocityX *= -1f;
            }
            else if(this.velocityY != 0.0f) {
                this.posY = anotherBox.posY - this.height;
                this.velocityY *= -1f;
            }
        }
    }

    public void collide(BoundingBox anotherBox){
        this.reactToCollision(anotherBox);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.posX += this.velocityX;
        if(velocityX >= 0.0f && velocityX - 1.0f >= 0.0f)
            velocityX -= 1.0f;
        else if(velocityX <= 0.0f && velocityX + 1.0f <= 0.0f)
            velocityX += 1.0f;


        this.posY += this.velocityY;
        if(velocityY >= 0.0f && velocityY - 1.0f >= 0.0f)
            velocityY -= 1.0f;
        else if(velocityY <= 0.0f && velocityY + 1.0f <= 0.0f)
            velocityY += 1.0f;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PRESSED");
        switch (e.getKeyCode()){
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
