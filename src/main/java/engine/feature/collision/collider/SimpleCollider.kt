package engine.feature.collision.collider

import engine.feature.collision.BoundingBox

interface SimpleCollider {
    fun reactToCollision(anotherBox: BoundingBox)
}
