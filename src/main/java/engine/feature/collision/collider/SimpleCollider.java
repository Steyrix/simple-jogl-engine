package engine.feature.collision.collider;

import engine.feature.collision.BoundingBox;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface SimpleCollider {
    void reactToCollision(@NotNull BoundingBox anotherBox);
}
