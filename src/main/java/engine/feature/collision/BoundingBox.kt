package engine.feature.collision

import engine.util.geometry.PointF

import java.util.ArrayList

open class BoundingBox(
        var posX: Float,
        var posY: Float,
        var width: Float,
        var height: Float,
        override var shouldCollide: Boolean
) : Cloneable, ColliderEntity {

    val rightX: Float
        get() = posX + width

    val bottomY: Float
        get() = posY + height

    fun setPosition(nX: Float, nY: Float) {
        posX = nX
        posY = nY
    }

    fun intersectsX(anotherBox: BoundingBox) = posX < anotherBox.rightX || rightX > anotherBox.posX


    fun intersectsY(anotherBox: BoundingBox) = posY < anotherBox.bottomY || bottomY > anotherBox.posY

    fun intersects(anotherBox: BoundingBox) = intersectsX(anotherBox) && intersectsY(anotherBox)

    fun containsEveryPointOf(vararg points: PointF): Boolean {
        points.forEach {
            val doesNotContain = it.x > rightX
                                 || it.x < posX
                                 || it.y > bottomY
                                 || it.y < posY
            if (doesNotContain) {
                return false
            }

        }
        return true
    }

    fun containsNumberOfPoints(numberOfPoints: Int, strict: Boolean, points: List<PointF>): Boolean {
        if (numberOfPoints <= 0) {
            return true
        }

        var cnt = 0
        points.forEach {
            val strictCondition = it.x < rightX
                                  && it.x > posX
                                  && it.y < bottomY
                                  && it.y > posY

            val nonStrictCondition = it.x in posX..rightX
                                     && it.y <= bottomY
                                     && it.y >= posY

            if (strict) {
                if (strictCondition) {
                    cnt++
                }
            } else {
                if (nonStrictCondition) {
                    cnt++
                }
            }
        }

        return cnt >= numberOfPoints
    }

    fun containsAnyPointOf(strict: Boolean, points: List<PointF>): Boolean {
        points.forEach {
            val strictCondition = it.x < rightX
                                  && it.x > posX
                                  && it.y < bottomY
                                  && it.y > posY

            val nonStrictCondition = it.x in posX..rightX
                                     && it.y <= bottomY
                                     && it.y >= posY

            if (strict) {
                if (strictCondition) {
                    return true
                }
            } else {
                if (nonStrictCondition) {
                    return true
                }
            }
        }

        return false
    }

    fun containsPoint(strict: Boolean, pointFS: ArrayList<PointF>): Boolean {
        return containsAnyPointOf(strict, pointFS)
    }

    fun getIntersectionWidth(anotherBox: BoundingBox): Float {
        return if (anotherBox.posX >= posX) {
            -(rightX - anotherBox.posX)
        } else {
            anotherBox.rightX - posX
        }
    }

    fun getIntersectionHeight(anotherBox: BoundingBox): Float {
        return if (anotherBox.posY >= posY) {
            -(bottomY - anotherBox.posY)
        } else {
            anotherBox.bottomY - posY
        }
    }

    override fun toString() = "posX:$posX; posY:$posY; rightX:$rightX; bottomY:$bottomY"

    public override fun clone() = BoundingBox(posX, posY, width, height, shouldCollide)
}
