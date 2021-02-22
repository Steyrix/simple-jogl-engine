package engine.feature.collision.collider

import engine.feature.collision.ColliderEntity

interface SimpleCollider : ColliderEntity {
    fun reactToCollision(entity: ColliderEntity)
}
