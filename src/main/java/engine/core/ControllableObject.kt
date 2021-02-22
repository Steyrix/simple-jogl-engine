package engine.core

import engine.feature.collision.ColliderEntity
import engine.feature.collision.collider.SimpleCollider

import java.awt.event.KeyEvent

abstract class ControllableObject : Controllable, SimpleCollider, Entity {

    abstract override fun reactToCollision(entity: ColliderEntity)

    abstract override fun keyTyped(e: KeyEvent)

    abstract override fun keyPressed(e: KeyEvent)

    abstract override fun keyReleased(e: KeyEvent)
}
