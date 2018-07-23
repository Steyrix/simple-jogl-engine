package engine;

import com.hackoeur.jglm.Mat4;

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
        return !undefined && ((anotherBox.getRight() <= this.getRight() && anotherBox.getRight() >= this.posX) ||
                (anotherBox.posX >= this.posX && anotherBox.posX <= this.getRight()));
    }

    public boolean intersectY(BoundingBox anotherBox) {
        return !undefined && ((anotherBox.getBottom() <= this.getBottom() && anotherBox.getBottom() >= this.posY) ||
                (anotherBox.posY >= this.posY && anotherBox.posY <= this.getBottom()));
    }

    public boolean intersects(BoundingBox anotherBox) {
        return intersectX(anotherBox) && intersectY(anotherBox);
    }

}
