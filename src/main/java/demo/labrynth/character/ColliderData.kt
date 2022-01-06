package demo.labrynth.character

import engine.feature.collision.BoundingBox
import engine.util.geometry.PointF

object ColliderData {
    fun getCollisionPointsForItem(box: BoundingBox) = arrayListOf(
            PointF(box.posX, box.posY),
            PointF(box.posX, box.bottomY),
            PointF(box.rightX, box.bottomY),
            PointF(box.rightX, box.posY),
            PointF(box.posX + box.width / 2, box.posY),
            PointF(box.posX + box.width / 2, box.bottomY),
            PointF(box.posX, box.posY + box.height / 2),
            PointF(box.rightX, box.posY + box.height / 2),
            PointF(box.posX, box.posY + box.height / 4),
            PointF(box.posX, box.bottomY - box.height / 4),
            PointF(box.rightX, box.posY + box.height / 4),
            PointF(box.rightX, box.bottomY - box.height / 5),
            PointF(box.posX, box.posY + box.height / 3),
            PointF(box.rightX, box.posY + box.height / 3),
            PointF(box.posX, box.bottomY - box.height / 3),
            PointF(box.rightX, box.bottomY - box.height / 3)
    )
}