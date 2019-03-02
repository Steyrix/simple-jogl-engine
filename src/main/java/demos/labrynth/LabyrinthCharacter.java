package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.feature.animation.BasicAnimation;
import engine.feature.collision.BoundingBox;
import engine.core.util.utilgeometry.PointF;
import engine.feature.collision.collider.SpeculativeCollider;
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
        initPoints(collisionPoints);

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
        initPoints(collisionPoints);

        this.nextBox = new BoundingBox(posX, posY, width, height);

        this.currentBottomPlatform = null;
        this.isWalking = false;
        this.canJump = true;
        this.jumpTime = 0f;
    }

    private void initPoints(ArrayList<PointF> target){
        target.add(new PointF(posX, posY));
        target.add(new PointF(posX, getBottomY()));
        target.add(new PointF(getRightX(), getBottomY()));
        target.add(new PointF(getRightX(), posY));
        target.add(new PointF(posX + width / 2, posY));
        target.add(new PointF(posX + width / 2, getBottomY()));
        target.add(new PointF(posX, posY + height / 2));
        target.add(new PointF(getRightX(), posY + height / 2));
        target.add(new PointF(posX, posY + height / 4)); // 8
        target.add(new PointF(posX, getBottomY() - height / 4)); // 9
        target.add(new PointF(getRightX(), posY + height / 4)); // 10
        target.add(new PointF(getRightX(), getBottomY() - height / 5)); // 11
    }

    //TODO: fix bug with horizontal collision
    @Override
    public void reactToCollision(BoundingBox anotherBox) {
        if (detectBottomContact(anotherBox)) {
            jumpState = false;
            currentBottomPlatform = anotherBox;
        }

        if (currentBottomPlatform != null && !detectBottomContact(currentBottomPlatform)) {
            currentBottomPlatform = null;
            jumpState = true;
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
        boolean fallingState = !jumpState && this.velocityY < 0f;

        //System.out.println("1 MoveX: " + moveX + ".  MoveY: " + moveY);

        if (horizontalContact)
            moveY = 0f;
        else
            moveX = 0f;

        //System.out.println("2 MoveX: " + moveX + ".  MoveY: " + moveY);

        if (this.velocityX != 0f && this.velocityY != 0f) {
            this.velocityX = 0f;
            if (fallingState)
                this.velocityY = 0f;
            this.posX += moveX;
            this.posY += moveY;
        } else if (this.velocityX != 0f) {
            this.posX += moveX;
            this.posY += moveY;
            this.velocityX = 0f;
        } else if (this.velocityY != 0f) {
            this.posY += moveY;
            this.posX += moveX;
            this.velocityY = 0f;
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

        } else if (this.velocityX != 0 && !jumpState) {
            if (currentAnim != animations.get(0))
                this.setWalkAnim();
        } else if (this.velocityX != 0 && jumpState) {
            this.setJumpAnimation();
        } else if (this.velocityY > 0f && this.velocityY <= 0.2f) {
            this.setAnimation(this.animations.get(2));
            this.currentAnim.setCurrentFrameX(0);
            this.currentAnim.setCurrentFrameY(2);
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
        velocityY -= 25f;
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
