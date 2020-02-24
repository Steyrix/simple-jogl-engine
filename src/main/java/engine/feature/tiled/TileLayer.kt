package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import engine.util.geometry.PointF
import java.awt.Dimension

internal class TileLayer(internal val width: Int,
                         internal val height: Int,
                         internal val tileData: ArrayList<Int>,
                         internal val properties: ArrayList<LayerProperty<out Any>>,
                         private val tileSet: TileSet) {

    private var openGLObject: OpenGlObject2D? = null

    private fun getPosition(num: Int): PointF {
        val x: Int = num % width
        val y: Int = num / width

        return PointF(x.toFloat(), y.toFloat())
    }

    private fun genVertices(pos: PointF): FloatArray {
        return floatArrayOf(
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * pos.y,
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * pos.y,
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * pos.y)
    }

    private fun toOpenGLObject(gl: GL4): OpenGlObject2D {
        val allVertices: ArrayList<Float> = ArrayList()
        val allUV: ArrayList<Float> = ArrayList()

        for (num in 0 until tileData.size) {
            val pos = getPosition(num)
            val verticesArray = genVertices(pos)

            // current problem is with tile ordering in tilesets (In some tilesets order is incorrect)
            // E.g. there are empty spaces between different tiles which make parsing incorrect
            // Also, empty (transparent) tiles are indexed as -1, therefore
            // It should be transparent rectangle for those tiles
            // if id (tileData[num]) == -1 should be transparent

            // temporary solution - to set empty uv for empty tiles
            val uvArray =
                if (tileData[num] == -1) {
                    floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                }
                else tileSet.getTileById(tileData[num]).arrayUV

            allVertices.addAll(verticesArray.toList())
            allUV.addAll(uvArray.toList())
        }

        val out = OpenGlObject2D(2, allVertices.size / 2, gl,
                Dimension(width * tileSet.tileWidth, height * tileSet.tileHeight), 0)

        out.initRenderData(tileSet.texture, allVertices.toFloatArray(), allUV.toFloatArray())

        return out
    }

    private fun checkObject(gl: GL4) {
        if (openGLObject == null) {
            openGLObject = toOpenGLObject(gl)
        }
    }

    fun draw(gl: GL4, shader: Shader) {
        checkObject(gl)
        openGLObject!!.draw(0f, shader)
    }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        checkObject(gl)
        openGLObject!!.draw(xSize, ySize, 0f, shader)
    }
}