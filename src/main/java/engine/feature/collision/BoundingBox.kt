package engine.feature.collision

import engine.util.utilgeometry.PointF

import java.awt.*
import java.util.ArrayList

open class BoundingBox {
    var posX: Float = 0f
        protected set

    var posY: Float = 0f
        protected set

    protected var width: Float = 0.toFloat()
    protected var height: Float = 0.toFloat()
    private var undefined: Boolean = false

    protected val rightX: Float
        get() = this.posX + this.width

    protected val bottomY: Float
        get() = this.posY + this.height

    val size: Dimension
        get() = Dimension(this.width.toInt(), this.height.toInt())

    constructor() {
        this.posX = 0.0f
        this.posY = 0.0f
        this.width = 0.0f
        this.height = 0.0f

        this.undefined = true
    }

    constructor(posX: Float, posY: Float, width: Float, height: Float) {
        this.posX = posX
        this.posY = posY
        this.width = width
        this.height = height

        this.undefined = false
    }

    constructor(posX: Float, posY: Float) {
        this.posX = posX
        this.posY = posY
        this.width = 0.0f
        this.height = 0.0f

        this.undefined = true
    }

    constructor(dimension: Dimension) {
        this.posX = 0.0f
        this.posY = 0.0f
        this.width = dimension.width.toFloat()
        this.height = dimension.height.toFloat()

        this.undefined = true
    }

    fun setPosition(nX: Float, nY: Float) {
        this.posX = nX
        this.posY = nY
    }

    fun intersectsX(anotherBox: BoundingBox): Boolean {
        return !undefined && !(this.posX > anotherBox.rightX || this.rightX < anotherBox.posX)
    }

    fun intersectsY(anotherBox: BoundingBox): Boolean {
        return !undefined && !(this.posY > anotherBox.bottomY || this.bottomY < anotherBox.posY)
    }

    fun intersects(anotherBox: BoundingBox): Boolean {
        return intersectsX(anotherBox) && intersectsY(anotherBox)
    }

    fun containsEveryPointOf(vararg points: PointF): Boolean {
        for (point in points) {
            if (undefined || !(point.x < this.rightX && point.x > this.posX &&
                            point.y < this.bottomY && point.y > this.posY))
                return false
        }

        return true
    }

    fun containsNumberOfPoints(numberOfPoints: Int, strict: Boolean, vararg points: PointF): Boolean {
        if (numberOfPoints <= 0)
            return true

        var cnt = 0
        for (point in points) {
            if (strict) {
                if (!undefined && point.x < this.rightX && point.x > this.posX &&
                        point.y < this.bottomY && point.y > this.posY)
                    cnt++
            } else {
                if (!undefined && point.x <= this.rightX && point.x >= this.posX &&
                        point.y <= this.bottomY && point.y >= this.posY)
                    cnt++
            }

        }

        return cnt >= numberOfPoints
    }

    fun containsAnyPointOf(strict: Boolean, vararg points: PointF): Boolean {
        for (point in points) {
            if (strict) {
                if (!undefined && point.x < this.rightX && point.x > this.posX &&
                        point.y < this.bottomY && point.y > this.posY)
                    return true
            } else {
                if (!undefined && point.x <= this.rightX && point.x >= this.posX &&
                        point.y <= this.bottomY && point.y >= this.posY)
                    return true
            }
        }

        return false
    }

    fun containsPoint(strict: Boolean, pointFS: ArrayList<PointF>): Boolean {
        for (p in pointFS) {
            if (containsAnyPointOf(strict, p))
                return true
        }

        return false
    }

    override fun toString(): String {
        return "posX:$posX; posY:$posY; rightX:$rightX; bottomY:$bottomY"
    }

    protected fun getIntersectionWidth(anotherBox: BoundingBox): Float {
        return if (anotherBox.posX >= this.posX) -(this.rightX - anotherBox.posX) else anotherBox.rightX - this.posX
    }

    protected fun getIntersectionHeight(anotherBox: BoundingBox): Float {
        return if (anotherBox.posY >= this.posY) -(this.bottomY - anotherBox.posY) else anotherBox.bottomY - this.posY
    }
}
