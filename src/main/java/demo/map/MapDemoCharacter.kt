package demo.map

import engine.core.*
import engine.core.controllable.ControllableObject
import engine.core.controllable.keyboard.KeyboardUtils
import engine.core.entity.Entity
import engine.feature.collision.ColliderEntity
import java.awt.event.KeyEvent

class MapDemoCharacter internal constructor(
        tileSize: Float,
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        animatedObject: AnimatedObject,
        graphicalObject: OpenGlObject2D
) : CompositeObject(animatedObject, null, graphicalObject) {

    private val keys = KeyboardUtils.getAWSDKeySet()
    private var isWalking: Boolean = true
    private val xVelocityModifier = 2.5f
    private val yVelocityModifier = 2.5f
    private val deltaModifier = 20

    private val controllableObject: ControllableObject = object: ControllableObject() {

        override var shouldCollide: Boolean = true

        override fun reactToCollision(entity: ColliderEntity) {

        }

        override fun isControlledByKey(e: KeyEvent) = keys.containsKey(e.keyCode)

        override fun keyTyped(e: KeyEvent) = Unit

        override fun keyPressed(e: KeyEvent) {
            if (keys.getOrDefault(e.keyCode, false)) {
                keys[e.keyCode] = true
            }
        }

        override fun keyReleased(e: KeyEvent) {
            if (keys.getOrDefault(e.keyCode, false)) {
                keys[e.keyCode] = false
            }
        }
    }

    override fun update(deltaTime: Float) {
        applyVelocityX()
        applyVelocityY()
        changePosition(deltaTime)
    }

    override fun react(entity: Entity) {
        TODO("Not yet implemented")
    }

    private fun applyVelocityX() {
        if (keys[KeyEvent.VK_D] == true) {
            isWalking = true
            velocityX = xVelocityModifier
        }
        if (keys[KeyEvent.VK_A] == true) {
            isWalking = true
            velocityX = -xVelocityModifier
        }
        if (keys[KeyEvent.VK_A] == false && keys[KeyEvent.VK_D] == false) {
            velocityX = 0f
        }
    }

    private fun applyVelocityY() {
        if (keys[KeyEvent.VK_W] == true) {
            isWalking = true
            velocityY = -yVelocityModifier
        }
        if (keys[KeyEvent.VK_S] == true) {
            isWalking = true
            velocityY = yVelocityModifier
        }
        if (keys[KeyEvent.VK_W] == false && keys[KeyEvent.VK_S] == false) {
            velocityY = 0f
        }
    }

    private fun changePosition(deltaTime: Float) = with(graphicalComponent.box) {
        this?.let {
            it.posY += velocityY * deltaTime / deltaModifier
            it.posX += velocityX * deltaTime / deltaModifier
        }
    }
}