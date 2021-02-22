package engine.feature.collision.collider

import engine.feature.collision.ColliderEntity

interface SpeculativeCollider : SimpleCollider {
    val potentialEntity: ColliderEntity
    fun preventCollision()
}
