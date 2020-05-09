package engine.util.geometry

class PointF(val x: Float, val y: Float) {

    override fun toString() = "(x: $x ; y: $y )"

    override fun equals(other: Any?) =
            if (other is PointF) {
                x == other.x && y == other.y
            } else {
                false
            }
}
