package demo.labrynth.character

import engine.feature.collision.BoundingBox
import engine.util.geometry.PointF

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

    fun getLeftBorderPointSet(): List<PointF> {
        val points = getCollisionPoints()
        return listOf(points[0], points[1], points[6])
    }

    fun getRightBorderPointSet(): List<PointF> {
        val points = getCollisionPoints()
        return listOf(points[2], points[3], points[7])
    }

    fun getHorizontalContactPointSet(): List<PointF> {
        val points = getCollisionPoints()
        return listOf(
                points[8],
                points[9],
                points[10],
                points[11],
                points[12],
                points[13]
        )
    }

    fun getBottomLinePointSet(): List<PointF> {
        val points = getCollisionPoints()
        return listOf(points[1], points[2], points[5])
    }
}