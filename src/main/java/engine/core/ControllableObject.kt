package engine.core

import engine.feature.collision.BoundingBox
import engine.feature.collision.collider.SimpleCollider

import java.awt.event.KeyEvent

abstract class ControllableObject : Controllable, SimpleCollider, Entity {

    abstract override fun reactToCollision(anotherBox: BoundingBox)

    abstract override fun keyTyped(e: KeyEvent)

    abstract override fun keyPressed(e: KeyEvent)

    abstract override fun keyReleased(e: KeyEvent)
}
