package engine.feature.collision.collider;

import engine.feature.collision.BoundingBox;

@SuppressWarnings("unused")
public interface SimpleCollider {
    void reactToCollision(BoundingBox anotherBox);
}
