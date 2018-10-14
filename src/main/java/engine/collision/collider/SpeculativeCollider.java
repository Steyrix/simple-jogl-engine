package engine.collision.collider;

import engine.collision.BoundingBox;

@SuppressWarnings("unused")
public interface SpeculativeCollider extends SimpleCollider {
    BoundingBox getNextBox();
    void preventCollision();
}
