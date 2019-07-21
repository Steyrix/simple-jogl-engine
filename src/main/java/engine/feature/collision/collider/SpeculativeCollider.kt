package engine.feature.collision.collider

import engine.feature.collision.BoundingBox

interface SpeculativeCollider : SimpleCollider {
    val nextBox: BoundingBox
    fun preventCollision()
}
