package engine.collision;

import engine.utilgeometry.PointF;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings({"unused","WeakerAccess"})
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

    protected float getRightX() {
        return this.posX + this.width;
    }

    protected float getBottomY() {
        return this.posY + this.height;
    }

    public Dimension getSize() {
        return new Dimension((int) this.width, (int) this.height);
    }

    public boolean intersectsX(BoundingBox anotherBox) {
        return !undefined && !(this.posX > anotherBox.getRightX() || this.getRightX() < anotherBox.posX);
    }

    public boolean intersectsY(BoundingBox anotherBox) {
        return !undefined && !(this.posY > anotherBox.getBottomY() || this.getBottomY() < anotherBox.posY);
    }

    public boolean intersects(BoundingBox anotherBox) {
        return intersectsX(anotherBox) && intersectsY(anotherBox);
    }

    public boolean containsEveryPointOf(PointF... points) {
        for (PointF point : points) {
            if (undefined || !(point.x < this.getRightX() && point.x > this.posX &&
                    point.y < this.getBottomY() && point.y > this.posY))
                return false;
        }

        return true;
    }

    public boolean containsNumberOfPoints(int numberOfPoints, boolean strict, PointF... points) {
        if (numberOfPoints <= 0)
            return true;

        int cnt = 0;
        for (PointF point : points) {
            if (strict) {
                if (!undefined && (point.x < this.getRightX() && point.x > this.posX &&
                        point.y < this.getBottomY() && point.y > this.posY))
                    cnt++;
            } else {
                if (!undefined && (point.x <= this.getRightX() && point.x >= this.posX &&
                        point.y <= this.getBottomY() && point.y >= this.posY))
                    cnt++;
            }

        }

        return cnt >= numberOfPoints;
    }

    public boolean containsAnyPointOf(boolean strict, PointF... points) {
        for (PointF point : points) {
            if (strict) {
                if (!undefined && (point.x < this.getRightX() && point.x > this.posX &&
                        point.y < this.getBottomY() && point.y > this.posY))
                    return true;
            } else {
                if (!undefined && (point.x <= this.getRightX() && point.x >= this.posX &&
                        point.y <= this.getBottomY() && point.y >= this.posY))
                    return true;
            }
        }

        return false;
    }

    public boolean containsPoint(boolean strict, ArrayList<PointF> pointFS) {
        for (PointF p : pointFS) {
            if (containsAnyPointOf(strict, p))
                return true;
        }

        return false;
    }

    public String toString() {
        return "posX:" + posX + "; posY:" + posY + "; rightX:" + getRightX() + "; bottomY:" + getBottomY();
    }

    protected float getIntersectionWidth(BoundingBox anotherBox) {
        return anotherBox.posX >= this.posX ? -(this.getRightX() - anotherBox.posX) : anotherBox.getRightX() - this.posX;
    }

    protected float getIntersectionHeight(BoundingBox anotherBox) {
        return anotherBox.posY >= this.posY ? -(this.getBottomY() - anotherBox.posY) : anotherBox.getBottomY() - this.posY;
    }
}
