package engine.feature.collision

interface ColliderEntity {
    fun shouldHandleCollision(entity: ColliderEntity) : Boolean = this.shouldCollide && entity.shouldCollide
    var shouldCollide: Boolean
}