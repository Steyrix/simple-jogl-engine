package engine.core.buffered

object Buffered {
    val RECTANGLE_INDICES = floatArrayOf(
            0f, 1f,
            1f, 0f,
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f)

    val RECTANGLE_VERTICES = getVerticesScaled()

    fun getIndicesScaled(horizontalScale: Float = 1f, verticalScale: Float = 1f) = floatArrayOf(
            0f, verticalScale,
            horizontalScale, 0f,
            0f, 0f,
            0f, verticalScale,
            horizontalScale, verticalScale,
            horizontalScale, 0f
    )

    fun getRectangleSectorVertices(sectorWidth: Float = 1f, sectorHeight: Float = 1f) = floatArrayOf(
            0f, 0f,
            sectorWidth, sectorHeight,
            0f, sectorHeight,
            0f, 0f,
            sectorWidth, 0f,
            sectorWidth, sectorHeight
    )

    fun getVerticesScaled(horizontalScale: Float = 1f, verticalScale: Float = 1f) = floatArrayOf(
            0f, 0f,
            horizontalScale, 0f,
            horizontalScale, 0f,
            horizontalScale, verticalScale,
            horizontalScale, verticalScale,
            0f, verticalScale,
            0f, verticalScale,
            0f, 0f
    )
}