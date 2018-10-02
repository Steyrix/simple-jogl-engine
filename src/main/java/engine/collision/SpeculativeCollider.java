package engine.collision;

@SuppressWarnings("unused")
public interface SpeculativeCollider extends SimpleCollider {
    BoundingBox getNextBox();
    void preventCollision();
}
