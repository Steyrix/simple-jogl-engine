package demo.labrynth.character

import engine.feature.collision.BoundingBox

/* TODO: think of a way of updating collision points by updating box's position.
    probably calculating of each point should be a function
 */
class CharacterBoundingBox(
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        shouldCollide: Boolean
) : BoundingBox(posX, posY, width, height, shouldCollide) {

    fun getCollisionPoints() = ColliderData.getCollisionPointsForItem(this)
}