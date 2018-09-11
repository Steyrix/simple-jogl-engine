package engine.collision;

import java.awt.*;
import java.util.ArrayList;

public class BoundingBox {
    protected float posX;

    protected float posY;
    protected float width;
    protected float height;
    private boolean undefined;

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

    public float getRightX() {
        return this.posX + this.width;
    }

    public float getBottomY() {
        return this.posY + this.height;
    }

    public Dimension getSize() {
        return new Dimension((int) this.width, (int) this.height);
    }

    public boolean isTouchingX(BoundingBox anotherBox) {
        return anotherBox.posX == this.getRightX() || anotherBox.getRightX() == this.posX;
    }

    public boolean isTouchingY(BoundingBox anotherBox) {
        return anotherBox.posY == this.getBottomY() || anotherBox.getBottomY() == this.posY;
    }

    public boolean isTouching(BoundingBox anotherBox) {
        return isTouchingX(anotherBox) || isTouchingY(anotherBox);
    }

    public boolean intersectsX(BoundingBox anotherBox) {
        return !undefined && !(this.posX > anotherBox.getRightX() - 1 || this.getRightX() - 1 < anotherBox.posX);
    }

    public boolean intersectsY(BoundingBox anotherBox) {
        return !undefined && !(this.posY > anotherBox.getBottomY() - 1 || this.getBottomY() - 1 < anotherBox.posY);
    }

    public boolean intersects(BoundingBox anotherBox) {
        return intersectsX(anotherBox) && intersectsY(anotherBox);
    }

    public boolean containsPoint(PointF... points) {
        for (PointF point : points) {
            if (!undefined && (point.x < this.getRightX() && point.x > this.posX &&
                    point.y < this.getBottomY() && point.y > this.posY))
                return true;
        }

        return false;
    }

    public boolean containsPoint(float x, float y) {
        return !undefined && (x < this.getRightX() && x > this.posX &&
                y < this.getBottomY() && y > this.posY);
    }

    public boolean containsPoint(ArrayList<PointF> pointFS) {
        for (PointF p : pointFS) {
            if (containsPoint(p))
                return true;
        }

        return false;
    }

    public boolean leftUpperCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.posX, this.posY, 1, 1);
        if (!undefined && (thisPointBox.intersectsX(anotherBox) || thisPointBox.intersectsY(anotherBox)))
            return true;

        return false;
    }

    public boolean rightUpperCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.getRightX(), this.posY, 1, 1);
        if (!undefined && (thisPointBox.intersectsX(anotherBox) || thisPointBox.intersectsY(anotherBox)))
            return true;

        return false;
    }

    public boolean leftBottomCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.posX, this.getBottomY(), 1, 1);
        if (!undefined && ((thisPointBox.intersectsX(anotherBox) || thisPointBox.intersectsY(anotherBox)) || isTouching(anotherBox)))
            return true;

        return false;
    }

    public boolean rightBottomCollision(BoundingBox anotherBox) {
        BoundingBox thisPointBox = new BoundingBox(this.getRightX(), this.getBottomY(), 1, 1);
        if (!undefined && ((thisPointBox.intersectsX(anotherBox) || thisPointBox.intersectsY(anotherBox)) || isTouching(anotherBox)))
            return true;

        return false;
    }

    public CornerCollision detectCornerCollision(BoundingBox anotherBox) {
        if (leftUpperCollision(anotherBox)) return CornerCollision.LEFT_UPPER;
        if (leftBottomCollision(anotherBox)) return CornerCollision.LEFT_BOTTOM;
        if (rightUpperCollision(anotherBox)) return CornerCollision.RIGHT_UPPER;
        if (rightBottomCollision(anotherBox)) return CornerCollision.RIGHT_BOTTOM;

        return CornerCollision.NO_COLLISION;
    }

    public String toString() {
        return "posX:" + posX + "; posY:" + posY + "; rightX:" + getRightX() + "; bottomY:" + getBottomY();
    }
}
