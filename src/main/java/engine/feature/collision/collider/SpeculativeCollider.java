package engine.feature.collision.collider;

import engine.feature.collision.BoundingBox;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface SpeculativeCollider extends SimpleCollider {
    @NotNull BoundingBox getNextBox();
    void preventCollision();
}
