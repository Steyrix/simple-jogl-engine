package engine.util.utilgeometry

class PointF {
    val x: Float
    val y: Float

    constructor() {
        this.x = 0f
        this.y = 0f
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun toString(): String = "(x: $x ; y: $y )"

    override fun equals(other: Any?): Boolean = if (other is PointF) {
        this.x == other.x && this.y == other.y
    } else false
}
