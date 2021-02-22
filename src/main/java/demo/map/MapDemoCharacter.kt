package demo.map

import engine.core.AnimatedObject
import engine.core.CompositeObject
import engine.core.Entity
import engine.core.OpenGlObject2D

class MapDemoCharacter internal constructor(
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        animatedObject: AnimatedObject,
        graphicalObject: OpenGlObject2D
) : CompositeObject(animatedObject, null, graphicalObject) {

    override fun update(deltaTime: Float) {
        TODO("Not yet implemented")
    }

    override fun react(entity: Entity) {
        TODO("Not yet implemented")
    }

}