package engine.util.color

import java.awt.*

object ColorUtil {
    fun getBufferForColor(verticesCount: Int, color: Color): FloatArray {
        val colorLineLength = 3
        val out = FloatArray(verticesCount * colorLineLength)

        val colorLine = getColorValues(color)
        var i = 0
        while (i < out.size) {
            out[i] = colorLine[0]
            out[i + 1] = colorLine[1]
            out[i + 2] = colorLine[2]
            println(out[i].toString() + ", " + out[i + 1] + ", " + out[i + 2])
            i += colorLineLength
        }
        return out
    }

    private fun getColorValues(color: Color) =
            floatArrayOf(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
}
