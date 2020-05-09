package engine.core

import engine.feature.collision.BoundingBox
import engine.feature.shader.Shader
import java.awt.event.KeyEvent

abstract class CompositeObject(protected var animationComponent: AnimatedObject? = null,
                               protected var controllableComponent: ControllableObject? = null,
                               protected var graphicalComponent: OpenGlObject2D) : Entity {

    protected var velocityX: Float = 0.toFloat()
    protected var velocityY: Float = 0.toFloat()
    protected var jumpState: Boolean = false

    fun setAnimation(component: AnimatedObject) {
        animationComponent = component
    }

    fun setControl(component: ControllableObject) {
        controllableComponent = component
    }

    fun setGraphics(component: OpenGlObject2D) {
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

    fun keyTyped(e: KeyEvent) = controllableComponent?.keyTyped(e)

    fun keyPressed(e: KeyEvent) = controllableComponent?.keyPressed(e)

    fun keyReleased(e: KeyEvent)= controllableComponent?.keyReleased(e)

    abstract fun update(deltaTime: Float)

    abstract fun react(entity: Entity)
}