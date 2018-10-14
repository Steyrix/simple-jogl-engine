package engine.collision.collider;

import engine.collision.BoundingBox;

@SuppressWarnings("unused")
public interface SimpleCollider {
    void reactToCollision(BoundingBox anotherBox);
}
