package engine.core

import com.jogamp.opengl.GL4
import engine.feature.animation.AnimatedObject
import engine.feature.animation.BasicAnimation
import engine.feature.collision.BoundingBox
import engine.feature.collision.collider.SimpleCollider

import java.awt.*
import java.awt.event.KeyEvent

abstract class ControllableObject : AnimatedObject, Controllable, SimpleCollider {

    protected var velocityX: Float = 0.toFloat()
    protected var velocityY: Float = 0.toFloat()
    protected var jumpState: Boolean = false

    protected constructor(bufferParamsCount: Int,
                          verticesCount: Int,
                          gl: GL4,
                          posX: Float,
                          posY: Float,
                          boxDim: Dimension,
                          id: Int,
                          frameSizeX: Float,
                          frameSizeY: Float,
                          vararg animationSet: BasicAnimation) :
            super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id, frameSizeX, frameSizeY, *animationSet) {

        this.velocityX = 0.0f
        this.velocityY = 0.0f
        this.jumpState = false
    }

    abstract override fun reactToCollision(anotherBox: BoundingBox)

    abstract override fun keyTyped(e: KeyEvent)

    abstract override fun keyPressed(e: KeyEvent)

    abstract override fun keyReleased(e: KeyEvent)

    override fun toString(): String {
        return super.toString() + "\n Controllable"
    }
}
