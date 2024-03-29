package engine.core.controllable

import engine.core.entity.Entity
import engine.feature.collision.ColliderEntity
import engine.feature.collision.collider.SimpleCollider

import java.awt.event.KeyEvent

abstract class ControllableObject : Controllable, SimpleCollider {

    abstract override fun reactToCollision(entity: ColliderEntity)

    abstract override fun keyTyped(e: KeyEvent)

    abstract override fun keyPressed(e: KeyEvent)

    abstract override fun keyReleased(e: KeyEvent)
}
