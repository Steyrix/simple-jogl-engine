package engine.core

import engine.core.controllable.ControllableObject
import engine.core.entity.Entity
import engine.feature.collision.BoundingBox
import engine.feature.shader.Shader
import java.awt.event.KeyEvent

abstract class CompositeObject(
        protected var animationComponent: AnimatedObject? = null,
        protected var controlComponent: ControllableObject? = null,
        protected var graphicalComponent: OpenGlObject2D
) : Entity {

    protected var velocityX: Float = 0.toFloat()
    protected var velocityY: Float = 0.toFloat()
    protected var jumpState: Boolean = false

    fun setAnimComponent(component: AnimatedObject) {
        animationComponent = component
    }

    fun setCtrlComponent(component: ControllableObject) {
        controlComponent = component
    }

    fun setGraphicsComponent(component: OpenGlObject2D) {
        graphicalComponent = component
    }

    fun setBoundingBox(box: BoundingBox) {
        graphicalComponent.box = box

    }

    fun draw(x: Float, y: Float, xSize: Float, ySize: Float, rotationAngle: Float, shader: Shader) =
        graphicalComponent.draw(x, y, xSize, ySize, rotationAngle, shader)

    fun draw(shader: Shader) = with(graphicalComponent) {
        box?.let {
            draw(it.posX, it.posY, it.width, it.height, 0f, shader)
        }
    }

    fun drawBoundingBox(shader: Shader) {
        if (graphicalComponent.box != null) {
            graphicalComponent.drawBox(shader)
        }
    }

    fun dispose() = graphicalComponent.dispose()

    fun keyTyped(e: KeyEvent) = controlComponent?.keyTyped(e)

    fun keyPressed(e: KeyEvent) = controlComponent?.keyPressed(e)

    fun keyReleased(e: KeyEvent)= controlComponent?.keyReleased(e)

    abstract fun update(deltaTime: Float)

    abstract fun react(entity: Entity)
}