package demo.labrynth.character

import engine.feature.collision.BoundingBox

class CharacterBoundingBox(
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        shouldCollide: Boolean
) : BoundingBox(posX, posY, width, height, shouldCollide) {

    fun getCollisionPoints() = ColliderData.getCollisionPointsForItem(this)
}