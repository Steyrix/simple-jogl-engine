package engine.feature.collision.collider;

import engine.feature.collision.BoundingBox;

@SuppressWarnings("unused")
public interface SpeculativeCollider extends SimpleCollider {
    BoundingBox getNextBox();
    void preventCollision();
}
