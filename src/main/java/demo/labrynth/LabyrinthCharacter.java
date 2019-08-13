package demo.labrynth;

import com.jogamp.opengl.GL4;
import engine.feature.animation.BasicAnimation;
import engine.feature.collision.BoundingBox;
import engine.util.geometry.PointF;
import engine.feature.collision.collider.SpeculativeCollider;
import engine.core.ControllableObject;
import org.jetbrains.annotations.NotNull;

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

    LabyrinthCharacter(int bufferParamsCount, int verticesCount, GL4 gl, float posX, float posY, Dimension boxDim, int id, float frameSizeX, float frameSizeY, BasicAnimation... animationSet) throws Exception {
        super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, animationSet);
        this.keys = new boolean[1000000];

        this.collisionPoints = new ArrayList<>();
        initCollisionPoints(collisionPoints);

        this.nextBox = new BoundingBox(posX, posY, getWidth(), getHeight());

        this.currentBottomPlatform = null;
        this.isWalking = false;
        this.canJump = true;
        this.jumpTime = 0f;
    }

    private void initCollisionPoints(ArrayList<PointF> target){
        target.add(new PointF(getPosX(), getPosY()));
        target.add(new PointF(getPosX(), getBottomY()));
        target.add(new PointF(getRightX(), getBottomY()));
        target.add(new PointF(getRightX(), getPosY()));
        target.add(new PointF(getPosX() + getWidth() / 2, getPosY()));
        target.add(new PointF(getPosX() + getWidth() / 2, getBottomY()));
        target.add(new PointF(getPosX(), getPosY() + getHeight() / 2));
        target.add(new PointF(getRightX(), getPosY() + getHeight() / 2));
        target.add(new PointF(getPosX(), getPosY() + getHeight() / 4)); // 8
        target.add(new PointF(getPosX(), getBottomY() - getHeight() / 4)); // 9
        target.add(new PointF(getRightX(), getPosY() + getHeight() / 4)); // 10
        target.add(new PointF(getRightX(), getBottomY() - getHeight() / 5)); // 11
    }

    //TODO: fix bug with horizontal collision
    @Override
    public void reactToCollision(@NotNull BoundingBox anotherBox) {
        if (detectBottomContact(anotherBox)) {
            setJumpState(false);
            currentBottomPlatform = anotherBox;
        }

        if (currentBottomPlatform != null && !detectBottomContact(currentBottomPlatform)) {
            currentBottomPlatform = null;
            setJumpState(true);
        }

        if (anotherBox.containsPoint(true, this.collisionPoints)) {
            processCollision(anotherBox);
        }
    }

    //TODO: fix bug - collision to horizontal above the vertical is ignored
    private void processCollision(BoundingBox anotherBox) {
        float moveX = this.getIntersectionWidth(anotherBox),
                moveY = this.getIntersectionHeight(anotherBox);
        boolean horizontalContact = detectHorizontalContact(anotherBox);
        boolean fallingState = !getJumpState() && this.getVelocityY() < 0f;

        //System.out.println("1 MoveX: " + moveX + ".  MoveY: " + moveY);

        if (horizontalContact)
            moveY = 0f;
        else
            moveX = 0f;

        //System.out.println("2 MoveX: " + moveX + ".  MoveY: " + moveY);

        if (this.getVelocityX() != 0f && this.getVelocityY() != 0f) {
            this.setVelocityX(0f);
            if (fallingState)
                this.setVelocityY(0f);
            this.setPosX(this.getPosX() + moveX);
            this.setPosY(this.getPosY() + moveY);
        } else if (this.getVelocityX() != 0f) {
            this.setPosX(this.getPosX() + moveX);
            this.setPosY(this.getPosY() + moveY);
            this.setVelocityX(0f);
        } else if (this.getVelocityY() != 0f) {
            this.setPosY(this.getPosY() + moveY);
            this.setPosX(this.getPosX() + moveX);
            this.setVelocityY(0f);
        }
    }

    private boolean detectBottomContact(BoundingBox anotherBox) {
        return anotherBox.containsNumberOfPoints(2, false,
                this.collisionPoints.get(1),
                this.collisionPoints.get(2),
                this.collisionPoints.get(5));
    }

    private boolean detectHorizontalContact(BoundingBox anotherBox) {
        return anotherBox.containsNumberOfPoints(2, true,
                this.collisionPoints.get(0),
                this.collisionPoints.get(1),
                this.collisionPoints.get(6)) ||
                anotherBox.containsNumberOfPoints(2, true,
                        this.collisionPoints.get(2),
                        this.collisionPoints.get(3),
                        this.collisionPoints.get(7)) ||
                anotherBox.containsAnyPointOf(true,
                        this.collisionPoints.get(8),
                        this.collisionPoints.get(9),
                        this.collisionPoints.get(10),
                        this.collisionPoints.get(11));
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

        //System.out.println("Detect bottom contact " + (currentBottomPlatform == null ? "NULL" :
          //      detectBottomContact(currentBottomPlatform)) + "posY " + posY);
        //System.out.println(currentBottomPlatform == null ? "NULL" : currentBottomPlatform.toString());
        //System.out.println("JUMPSTATE:" + jumpState);
        //System.out.println(this.velocityX + " " + this.velocityY);

    }

    private void applyVelocityX() {
        if (keys[KeyEvent.VK_D]) {
            isWalking = true;
            this.setVelocityX(3.5f);
        } else if (keys[KeyEvent.VK_A]) {
            isWalking = true;
            this.setVelocityX(-3.5f);
        } else
            this.setVelocityX(0f);
    }

    private void applyVelocityY() {
        if (keys[KeyEvent.VK_S]) {
            this.setVelocityY(5.0f);
            this.setJumpState(true);
            this.canJump = false;
        } else if (keys[KeyEvent.VK_W] && canJump)
            jump();
        else if (!getJumpState())
            this.setVelocityY(0.0f);
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
        if (getJumpState() && currentBottomPlatform == null) {
            //System.out.println("Applying gravity");
            this.setVelocityY(this.getVelocityY() + (gravity * deltaTime) / 10);
        }
    }

    private void processAnimation() {
        if (this.getVelocityX() == 0 && this.getVelocityY() == 0) {
            this.setAnimation(this.getAnimations().get(2));
            this.getCurrentAnim().setCurrentFrameX(0);
            this.getCurrentAnim().setCurrentFrameY(2);
            this.isWalking = false;
        } else if (this.getVelocityY() != 0 && this.getVelocityX() != 0) {

        } else if (this.getVelocityX() != 0 && !getJumpState()) {
            if (getCurrentAnim() != getAnimations().get(0))
                this.setWalkAnim();
        } else if (this.getVelocityX() != 0 && getJumpState()) {
            this.setJumpAnimation();
        } else if (this.getVelocityY() > 0f && this.getVelocityY() <= 0.2f) {
            this.setAnimation(this.getAnimations().get(2));
            this.getCurrentAnim().setCurrentFrameX(0);
            this.getCurrentAnim().setCurrentFrameY(2);
        }

    }

    private void changePosition(float deltaTime) {
        this.setPosY(this.getPosY() + (this.getVelocityY() * deltaTime) / 20);
        this.setPosX(this.getPosX() + (this.getVelocityX() * deltaTime) / 20);
    }

    private void updateCollisionPoints() {
        collisionPoints.set(0, new PointF(getPosX(), getPosY()));
        collisionPoints.set(1, new PointF(getPosX(), getBottomY()));
        collisionPoints.set(2, new PointF(getRightX(), getBottomY()));
        collisionPoints.set(3, new PointF(getRightX(), getPosY()));
        collisionPoints.set(4, new PointF(getPosX() + getWidth() / 2, getPosY()));
        collisionPoints.set(5, new PointF(getPosX() + getWidth() / 2, getBottomY()));
        collisionPoints.set(6, new PointF(getPosX(), getPosY() + getHeight() / 2));
        collisionPoints.set(7, new PointF(getRightX(), getPosY() + getHeight() / 2));
    }

    @Override
    public void preventCollision() {
        this.setVelocityY(0.0f);
        this.setVelocityX(0.0f);
        keys[KeyEvent.VK_W] = false;
        keys[KeyEvent.VK_S] = false;
        keys[KeyEvent.VK_A] = false;
        keys[KeyEvent.VK_D] = false;
        isWalking = false;
    }

    private void updateNextBox(float deltaTime) {
        nextBox.setPosition(getPosX() + getVelocityX() * deltaTime, getPosY() + getVelocityY() * deltaTime);
    }

    @NotNull
    @Override
    public BoundingBox getNextBox() {
        return this.nextBox;
    }

    @Override
    public void keyTyped(@NotNull KeyEvent e) {
    }

    @Override
    public void keyPressed(@NotNull KeyEvent e) {

        if (!this.keys[e.getKeyCode()])
            this.keys[e.getKeyCode()] = true;

        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A) {
            if (!isWalking && !getJumpState()) {
                setWalkAnim();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_W)
            if (!getJumpState())
                setJumpAnimation();

    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {

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
        setVelocityY(getVelocityY() - 25f);
        setAirFloating();
    }

    private void setAirFloating() {
        setJumpState(true);
        canJump = false;
    }

    private void setJumpAnimation() {
        setCurrentAnim(getAnimations().get(1));
        getCurrentAnim().setCurrentFrameY(1);
        getCurrentAnim().setCurrentFrameX(7);
        getCurrentAnim().setFirstPosX(7);
        getCurrentAnim().setLastPosX(9);
    }

    private void setWalkAnim() {
        setCurrentAnim(getAnimations().get(0));
        getCurrentAnim().setCurrentFrameY(2);
        getCurrentAnim().setCurrentFrameX(1);
        getCurrentAnim().setFirstPosX(1);
        getCurrentAnim().setLastPosX(6);
    }

}
