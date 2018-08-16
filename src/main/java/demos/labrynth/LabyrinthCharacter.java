package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.animation.BasicAnimation;
import engine.collision.BoundingBox;
import engine.collision.PointF;
import engine.core.ControllableObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

//TODO: manage collisions
public class LabyrinthCharacter extends ControllableObject {

    private boolean[] keys;
    private ArrayList<PointF> collisionPoints;

    LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim, int id, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.keys = new boolean[1000000];
    }

    LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim, int id, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.keys = new boolean[1000000];
    }

    @Override
    protected void reactToCollision(BoundingBox anotherBox) {

        if (intersects(anotherBox)) {
            if (this.velocityX != 0.0f && this.velocityY != 0.0f) {

                this.velocityX = 0.0f;
                this.velocityY = 0.0f;

            } else if (this.velocityX != 0.0f) {
                if (this.velocityX > 0.0f)
                    this.posX = anotherBox.getPosX() - this.width;
                else
                    this.posX = anotherBox.getRightX();

                this.velocityX = 0.0f;

            } else if (this.velocityY != 0.0f) {
                if (this.velocityY > 0.0f)
                    this.posY = anotherBox.getPosY() - this.height;
                else
                    this.posY = anotherBox.getBottomY();

                this.velocityY = 0.0f;
            }
        }
    }

    @Override
    public void update(float deltaTime) {

        //Moving horizontally?
        if (keys[KeyEvent.VK_D])
            this.velocityX = 7.0f;
        else if (keys[KeyEvent.VK_A])
            this.velocityX = -7.0f;
        else
            this.velocityX = 0f;

        //Moving vertically?
        if (keys[KeyEvent.VK_S])
            this.velocityY = 5.0f;
        else if (keys[KeyEvent.VK_W] && !jumpState)
            jump();
        else if (!keys[KeyEvent.VK_W] && !jumpState)
            this.velocityY = 0.0f;

        float gravity = 5f;

        if (jumpState) {
            this.velocityY += (gravity * deltaTime) / 10;

            if(this.velocityY >= 0.0f && jumpState) {
                this.velocityY = 0.0f;
                this.jumpState = false;
            }
        }
        this.posY += (this.velocityY * deltaTime) / 20;

        this.posX += (this.velocityX * deltaTime) / 20;

        if (velocityX == 0 && velocityY == 0 ) {
            setAnimation(this.animations.get(2));
            this.currentAnim.setCurrentFrameX(0);
            this.currentAnim.setCurrentFrameY(2);
        } else if (velocityY != 0) {
            setJumpAnimation();
        }

        playAnimation();
        //System.out.println(this.posX + "   " + this.posY + "||| " + deltaTime + " |||" + this.velocityX + " " + this.velocityY);

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(!keys[e.getKeyCode()])
            keys[e.getKeyCode()] = true;

        if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A)
            setWalkAnim();
        else if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S)
            setJumpAnimation();

        //System.out.println(velocityX);
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if(keys[e.getKeyCode()])
            keys[e.getKeyCode()] = false;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_D:
                break;
            case KeyEvent.VK_A:
                break;
            case KeyEvent.VK_W:
                break;
            case KeyEvent.VK_S:
                break;
            default:
                break;
        }

        System.out.println(e.getKeyCode() + "R");
    }

    private void jump() {
        this.velocityY -= 25f;
        this.jumpState = true;
    }

    private void setJumpAnimation() {
        this.currentAnim = this.animations.get(1);
        this.currentAnim.setCurrentFrameY(1);
        this.currentAnim.setCurrentFrameX(7);
        this.currentAnim.setFirstPosX(7);
        this.currentAnim.setLastPosX(10);
    }

    private void setWalkAnim() {
        this.currentAnim = this.animations.get(0);
        this.currentAnim.setCurrentFrameY(2);
        this.currentAnim.setCurrentFrameX(1);
        this.currentAnim.setFirstPosX(1);
        this.currentAnim.setLastPosX(6);
    }
    
}
