package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.animation.BasicAnimation;
import engine.collision.BoundingBox;
import engine.core.ControllableObject;

import java.awt.*;
import java.awt.event.KeyEvent;

//TODO: manage collisions
public class LabyrinthCharacter extends ControllableObject {

    private boolean belowContact;
    private boolean aboveContact;
    private boolean[] keys;

    public LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, frameSizeX, frameSizeY, animationSet);
        this.belowContact = false;
        this.aboveContact = false;
        this.keys = new boolean[1000000];
    }

    protected LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, frameSizeX, frameSizeY, animationSet);
        this.belowContact = false;
        this.aboveContact = false;
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
                    this.posX = anotherBox.getRight();

                this.velocityX = 0.0f;

            } else if (this.velocityY != 0.0f) {
                if (this.velocityY > 0.0f)
                    this.posY = anotherBox.getPosY() - this.height;
                else
                    this.posY = anotherBox.getBottom();

                this.velocityY = 0.0f;
            }
        }
    }

    @Override
    public void update(float deltaTime) {

        //Moving horizontally?
        if (keys[KeyEvent.VK_D])
            this.velocityX = 5.0f;
        else if (keys[KeyEvent.VK_A])
            this.velocityX = -5.0f;
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
            if(this.velocityY >= 0.0f) {
                this.velocityY = 0.0f;
                this.jumpState = false;
            }
        }
        this.posY += (this.velocityY * deltaTime) / 20;

        this.posX += (this.velocityX * deltaTime) / 20;

        if (velocityX == 0 && velocityY == 0) {
            setAnimation(this.animations.get(2));
            this.currentAnim.setCurrentFrameX(0);
            this.currentAnim.setCurrentFrameY(0);
        } else if (velocityY != 0) {
            setJumpAnimation();
        }

        System.out.println(this.posX + "   " + this.posY + "||| " + deltaTime + " |||" + this.velocityX + " " + this.velocityY);
        playAnimation(deltaTime);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        keys[e.getKeyCode()] = true;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_D:
                setWalkAnim();
                break;
            case KeyEvent.VK_A:
                setWalkAnim();
                break;
            case KeyEvent.VK_W:
                setJumpAnimation();
                break;
            case KeyEvent.VK_S:
                setJumpAnimation();
                break;
            default:
                break;
        }

        System.out.println(velocityX);
    }

    @Override
    public void keyReleased(KeyEvent e) {

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
        this.currentAnim.setLastPosX(9);
    }

    private void setWalkAnim() {
        this.currentAnim = this.animations.get(0);
        this.currentAnim.setCurrentFrameY(0);
        this.currentAnim.setCurrentFrameX(1);
    }

}
