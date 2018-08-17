package engine.collision;

public interface SpeculativeCollider extends SimpleCollider {
    BoundingBox getNextBox();
    void preventCollision();
}
