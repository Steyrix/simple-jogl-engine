package engine.collision;

import java.awt.*;

public class BoundingBox {
    protected float posX;

    protected float posY;
    protected float width;
    protected float height;
    protected boolean undefined;

    public BoundingBox() {
        this.posX = 0.0f;
        this.posY = 0.0f;
        this.width = 0.0f;
        this.height = 0.0f;

        this.undefined = true;
    }

    public BoundingBox(float posX, float posY, float width, float height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;

        this.undefined = false;
    }

    public BoundingBox(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        this.width = 0.0f;
        this.height = 0.0f;

        this.undefined = true;
    }

    public BoundingBox(Dimension dimension) {
        this.posX = 0.0f;
        this.posY = 0.0f;
        this.width = dimension.width;
        this.height = dimension.height;

        this.undefined = true;
    }

    public void setPosition(float nX, float nY) {
        this.posX = nX;
        this.posY = nY;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getRight() {
        return this.posX + this.width;
    }

    public float getBottom() {
        return this.posY + this.height;
    }

    public Dimension getSize() {
        return new Dimension((int) this.width, (int) this.height);
    }

    private boolean isTouchingX(BoundingBox anotherBox){
        return anotherBox.posX == this.getRight() || anotherBox.getRight() == this.posX;
    }

    private boolean isTouchingY(BoundingBox anotherBox){
        return anotherBox.posY == this.getBottom() || anotherBox.getBottom() == this.posY;
    }

    public boolean isTouching(BoundingBox anotherBox){
        return isTouchingX(anotherBox) || isTouchingY(anotherBox);
    }

    public boolean intersectX(BoundingBox anotherBox) {
        return !undefined && !((this.posX > anotherBox.posX + anotherBox.width - 1) || (this.getRight() - 1 < anotherBox.posX));
    }

    public boolean intersectY(BoundingBox anotherBox) {
        return !undefined && !((this.posY > anotherBox.posY + anotherBox.height - 1) || (this.getBottom() - 1 < anotherBox.posY));
    }

    public boolean intersects(BoundingBox anotherBox) {
        return intersectX(anotherBox) && intersectY(anotherBox);
    }

    public boolean leftUpperCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.posX, this.posY, 1, 1);
        if(!undefined && (thisPointBox.intersectX(anotherBox) || thisPointBox.intersectY(anotherBox)))
            return true;

        return false;
    }

    public boolean rightUpperCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.getRight(), this.posY, 1, 1);
        if(!undefined && (thisPointBox.intersectX(anotherBox) || thisPointBox.intersectY(anotherBox)))
            return true;

        return false;
    }

    public boolean leftBottomCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.posX, this.getBottom(), 1, 1);
        if(!undefined && ((thisPointBox.intersectX(anotherBox) || thisPointBox.intersectY(anotherBox)) || isTouching(anotherBox)))
            return true;

        return false;
    }

    public boolean rightBottomCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.getRight(), this.getBottom(), 1, 1);
        if(!undefined && ((thisPointBox.intersectX(anotherBox) || thisPointBox.intersectY(anotherBox)) || isTouching(anotherBox)))
            return true;

        return false;
    }

    public CornerCollision detectCornerCollision(BoundingBox anotherBox){
        if(leftUpperCollision(anotherBox)) return CornerCollision.LEFT_UPPER;
        if(leftBottomCollision(anotherBox)) return CornerCollision.LEFT_BOTTOM;
        if(rightUpperCollision(anotherBox)) return CornerCollision.RIGHT_UPPER;
        if(rightBottomCollision(anotherBox)) return CornerCollision.RIGHT_BOTTOM;

        return CornerCollision.NO_COLLISION;
    }
}
