package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.animation.BasicAnimation;
import engine.collision.BoundingBox;
import engine.collision.PointF;
import engine.collision.SpeculativeCollider;
import engine.core.ControllableObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

//TODO: manage collisions with multiple objects at the same time
//TODO: cleanup code
public class LabyrinthCharacter extends ControllableObject implements SpeculativeCollider {

    private boolean[] keys;
    private ArrayList<PointF> collisionPoints;
    private BoundingBox nextBox;
    private BoundingBox currentBottomPlatform;

    private boolean isWalking;
    private boolean canJump;
    private float jumpTime;

    LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, Dimension boxDim, int id, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.keys = new boolean[1000000];

        this.collisionPoints = new ArrayList<>();
        this.collisionPoints.add(new PointF(posX, posY)); //
        this.collisionPoints.add(new PointF(posX, getBottomY())); //
        this.collisionPoints.add(new PointF(getRightX(), getBottomY()));
        this.collisionPoints.add(new PointF(getRightX(), posY));
        this.collisionPoints.add(new PointF(posX + width / 2, posY));
        this.collisionPoints.add(new PointF(posX + width / 2, getBottomY()));
        this.collisionPoints.add(new PointF(posX, posY + height / 2));
        this.collisionPoints.add(new PointF(getRightX(), posY + height / 2));
        this.nextBox = new BoundingBox(posX, posY, width, height);

        this.currentBottomPlatform = null;
        this.isWalking = false;
        this.canJump = true;
        this.jumpTime = 0f;
    }

    LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim, int id, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.keys = new boolean[1000000];

        this.collisionPoints = new ArrayList<>();
        this.collisionPoints.add(new PointF(posX, posY));
        this.collisionPoints.add(new PointF(posX, getBottomY()));
        this.collisionPoints.add(new PointF(getRightX(), getBottomY()));
        this.collisionPoints.add(new PointF(getRightX(), posY));
        this.collisionPoints.add(new PointF(posX + width / 2, posY));
        this.collisionPoints.add(new PointF(posX + width / 2, getBottomY()));
        this.collisionPoints.add(new PointF(posX, posY + height / 2));
        this.collisionPoints.add(new PointF(getRightX(), posY + height / 2));
        this.nextBox = new BoundingBox(posX, posY, width, height);

        this.currentBottomPlatform = null;
        this.isWalking = false;
        this.canJump = true;
        this.jumpTime = 0f;
    }

    //TODO: fix bug with horizontal collision
    @Override
    public void reactToCollision(BoundingBox anotherBox) {
        if (detectBottomContact(anotherBox)) {
            jumpState = false;
            currentBottomPlatform = anotherBox;
        }

        //TODO: fix setting jumpstate to true all the time
        if (currentBottomPlatform != null && !detectBottomContact(currentBottomPlatform)) {
            currentBottomPlatform = null;
            jumpState = true;
        }

        if (anotherBox.containsPoint(this.collisionPoints)) {
            //System.out.println("AnotherBox: " + anotherBox.getPosX() + ", " + anotherBox.getPosY());
            processCollision(anotherBox);
        }
    }

    //TODO: fix bug - moveY is applied because of diagonal move towards the vertical bound
    private void processCollision(BoundingBox anotherBox) {
        float moveX = this.getIntersectionWidth(anotherBox),
                moveY = this.getIntersectionHeight(anotherBox);

        boolean horizontalCollision =
                anotherBox.containsNumberOfPoints(2,
                        this.collisionPoints.get(0),
                        this.collisionPoints.get(1),
                        this.collisionPoints.get(6)) ||
                        anotherBox.containsNumberOfPoints(2,
                                this.collisionPoints.get(2),
                                this.collisionPoints.get(3),
                                this.collisionPoints.get(7));

        boolean fallingState = !jumpState && this.velocityY < 0f;

        //TODO: fix
        if (horizontalCollision) {
            //System.out.println("Setting moveY to zero due to horizontal collision");
            moveY = 0f;
        }

        if (this.velocityX != 0f && this.velocityY != 0f) {
            System.out.println(1);
            this.velocityX = 0f;
            if (fallingState) {
                this.velocityY = 0f;
                this.posX += moveX;
            }

            this.posY += moveY;

        } else if (this.velocityX != 0f) {
            //System.out.println(2);
            this.posX += moveX;
            this.velocityX = 0f;

        } else if (this.velocityY != 0f) {
            //System.out.println(3);
            this.posY += moveY;
            this.velocityY = 0f;
        }
    }

    private boolean detectBottomContact(BoundingBox anotherBox) {
        return anotherBox.containsNumberOfPoints(1,
                this.collisionPoints.get(1),
                this.collisionPoints.get(2),
                this.collisionPoints.get(5));
    }

    @Override
    public void update(float deltaTime) {

        applyVelocityX();
        applyVelocityY();

        processJumpTime(deltaTime);
        processGravityEffect(deltaTime);

        processAnimation();
        changePosition(deltaTime);

        updateCollisionPoints();
        updateNextBox(deltaTime);

        playAnimation(deltaTime);

        System.out.println("JUMPSTATE:" + jumpState);
        //System.out.println(this.velocityX + " " + this.velocityY);

    }

    private void applyVelocityX() {
        if (keys[KeyEvent.VK_D]) {
            isWalking = true;
            this.velocityX = 3.5f;
        } else if (keys[KeyEvent.VK_A]) {
            isWalking = true;
            this.velocityX = -3.5f;
        } else
            this.velocityX = 0f;
    }

    private void applyVelocityY() {
        if (keys[KeyEvent.VK_S]) {
            this.velocityY = 5.0f;
            this.jumpState = true;
            this.canJump = false;
        } else if (keys[KeyEvent.VK_W] && canJump)
            jump();
        else if (!jumpState)
            this.velocityY = 0.0f;
    }

    private void processJumpTime(float deltaTime) {
        if (!canJump) {
            this.jumpTime += deltaTime;
            float jumpTimeLimit = 600f;
            if (jumpTime >= jumpTimeLimit) {
                jumpTime = 0f;
                canJump = true;
            }
        }
    }

    private void processGravityEffect(float deltaTime) {
        float gravity = 1f;
        if (jumpState && currentBottomPlatform == null) {
            //System.out.println("Applying gravity");
            this.velocityY += (gravity * deltaTime) / 10;
        }
    }

    private void processAnimation() {
        if (this.velocityX == 0 && this.velocityY == 0) {
            this.setAnimation(this.animations.get(2));
            this.currentAnim.setCurrentFrameX(0);
            this.currentAnim.setCurrentFrameY(2);
            this.isWalking = false;
        } else if (this.velocityY != 0 && this.velocityX != 0) {
            if (!jumpState)
                this.setJumpAnimation();
        } else if (this.velocityX != 0 && !jumpState) {
            if (currentAnim != animations.get(0))
                this.setWalkAnim();
        } else if (this.velocityX != 0 && jumpState) {
            this.setJumpAnimation();
        }
    }

    private void changePosition(float deltaTime) {
        this.posY += (this.velocityY * deltaTime) / 20;
        this.posX += (this.velocityX * deltaTime) / 20;
    }

    private void updateCollisionPoints() {
        collisionPoints.set(0, new PointF(posX, posY));
        collisionPoints.set(1, new PointF(posX, getBottomY()));
        collisionPoints.set(2, new PointF(getRightX(), getBottomY()));
        collisionPoints.set(3, new PointF(getRightX(), posY));
        collisionPoints.set(4, new PointF(posX + width / 2, posY));
        collisionPoints.set(5, new PointF(posX + width / 2, getBottomY()));
        collisionPoints.set(6, new PointF(posX, posY + height / 2));
        collisionPoints.set(7, new PointF(getRightX(), posY + height / 2));
    }

    @Override
    public void preventCollision() {
        this.velocityY = 0.0f;
        this.velocityX = 0.0f;
        keys[KeyEvent.VK_W] = false;
        keys[KeyEvent.VK_S] = false;
        keys[KeyEvent.VK_A] = false;
        keys[KeyEvent.VK_D] = false;
        isWalking = false;
    }

    private void updateNextBox(float deltaTime) {
        nextBox.setPosition(posX + velocityX * deltaTime, posY + velocityY * deltaTime);
    }

    @Override
    public BoundingBox getNextBox() {
        return this.nextBox;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (!this.keys[e.getKeyCode()])
            this.keys[e.getKeyCode()] = true;

        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A) {
            if (!isWalking && !jumpState) {
                setWalkAnim();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S)
            if (!jumpState)
                setJumpAnimation();

    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (this.keys[e.getKeyCode()])
            this.keys[e.getKeyCode()] = false;

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
        velocityY -= 31f;
        setAirFloating();
    }

    private void setAirFloating() {
        jumpState = true;
        canJump = false;
    }

    private void setJumpAnimation() {
        currentAnim = animations.get(1);
        currentAnim.setCurrentFrameY(1);
        currentAnim.setCurrentFrameX(7);
        currentAnim.setFirstPosX(7);
        currentAnim.setLastPosX(9);
    }

    private void setWalkAnim() {
        currentAnim = animations.get(0);
        currentAnim.setCurrentFrameY(2);
        currentAnim.setCurrentFrameX(1);
        currentAnim.setFirstPosX(1);
        currentAnim.setLastPosX(6);
    }

}
