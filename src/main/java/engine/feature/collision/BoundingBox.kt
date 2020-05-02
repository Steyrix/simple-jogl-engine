package engine.feature.collision

import engine.util.geometry.PointF

import java.util.ArrayList

open class BoundingBox(var posX: Float, var posY: Float, var width: Float, var height: Float) : Cloneable {

    val rightX: Float
        get() = this.posX + this.width

    val bottomY: Float
        get() = this.posY + this.height

    fun setPosition(nX: Float, nY: Float) {
        this.posX = nX
        this.posY = nY
    }

    fun intersectsX(anotherBox: BoundingBox): Boolean {
        return this.posX < anotherBox.rightX || this.rightX > anotherBox.posX
    }

    fun intersectsY(anotherBox: BoundingBox): Boolean {
        return this.posY < anotherBox.bottomY || this.bottomY > anotherBox.posY
    }

    fun intersects(anotherBox: BoundingBox): Boolean {
        return intersectsX(anotherBox) && intersectsY(anotherBox)
    }

    fun containsEveryPointOf(vararg points: PointF): Boolean {
        points.forEach {
            if (!(it.x < this.rightX && it.x > this.posX && it.y < this.bottomY && it.y > this.posY))
                return false
        }
        return true
    }

    fun containsNumberOfPoints(numberOfPoints: Int, strict: Boolean, vararg points: PointF): Boolean {
        if (numberOfPoints <= 0)
            return true

        var cnt = 0
        points.forEach {
            if (strict) {
                if (it.x < this.rightX && it.x > this.posX &&
                        it.y < this.bottomY && it.y > this.posY)
                    cnt++
            } else {
                if (it.x <= this.rightX && it.x >= this.posX &&
                        it.y <= this.bottomY && it.y >= this.posY)
                    cnt++
            }

        }

        return cnt >= numberOfPoints
    }

    fun containsAnyPointOf(strict: Boolean, vararg points: PointF): Boolean {
        points.forEach {
            if (strict) {
                if (it.x < this.rightX && it.x > this.posX &&
                        it.y < this.bottomY && it.y > this.posY)
                    return true
            } else {
                if (it.x <= this.rightX && it.x >= this.posX &&
                        it.y <= this.bottomY && it.y >= this.posY)
                    return true
            }
        }

        return false
    }

    fun containsPoint(strict: Boolean, pointFS: ArrayList<PointF>): Boolean {
        pointFS.forEach {
            if (containsAnyPointOf(strict, it))
                return true
        }

        return false
    }

    fun getIntersectionWidth(anotherBox: BoundingBox): Float {
        return if (anotherBox.posX >= this.posX) -(this.rightX - anotherBox.posX) else anotherBox.rightX - this.posX
    }

    fun getIntersectionHeight(anotherBox: BoundingBox): Float {
        return if (anotherBox.posY >= this.posY) -(this.bottomY - anotherBox.posY) else anotherBox.bottomY - this.posY
    }

    override fun toString(): String {
        return "posX:$posX; posY:$posY; rightX:$rightX; bottomY:$bottomY"
    }

    public override fun clone() = BoundingBox(posX, posY, width, height)
}
